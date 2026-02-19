package aervial.rideanymob.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected boolean jumping;
    private static final ThreadLocal<Boolean> IS_TRAVELLING = ThreadLocal.withInitial(() -> false);


    @Inject(method = "travel", at = @At("HEAD"))
    private void cleanUpGravity(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if ((self instanceof FlyingEntity || self instanceof net.minecraft.entity.passive.BatEntity) && !self.hasPassengers()) {
            self.setNoGravity(false);
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!(self instanceof MobEntity mob)) return;
        if (!(mob.getControllingPassenger() instanceof PlayerEntity rider)) return;
        if (IS_TRAVELLING.get()) return;

        mob.fallDistance = 0;
        mob.setTarget(null);

        mob.setYaw(rider.getYaw());
        mob.prevYaw = mob.getYaw();

        mob.setPitch(rider.getPitch() * 0.5f);
        mob.prevPitch = mob.getPitch();

        mob.setHeadYaw(mob.getYaw());
        mob.setBodyYaw(mob.getYaw());
        mob.prevHeadYaw = mob.getYaw();

        if (mob instanceof SlimeEntity slime) {
            handleAutoSlimeMovement(slime, rider);
            ci.cancel();
        }

        else if (mob instanceof FlyingEntity || mob instanceof net.minecraft.entity.passive.BeeEntity ||
                mob instanceof net.minecraft.entity.passive.ParrotEntity || mob instanceof net.minecraft.entity.passive.BatEntity) {

            if (mob instanceof net.minecraft.entity.passive.BatEntity bat) {
                bat.setRoosting(false);
            }

            handleFlyingMovement(mob, rider);
            ci.cancel();
        } else {
            handleGroundMovement(mob, rider, movementInput);
            ci.cancel();
        }
    }

    private void handleFlyingMovement(MobEntity mob, PlayerEntity rider) {
        mob.setNoGravity(true);

        float speedMultiplier = 1.5f;

        if (mob instanceof net.minecraft.entity.passive.BatEntity) {
            speedMultiplier = 0.4f;
        }

        float speed = (float) mob.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED) * speedMultiplier;
        Vec3d lookVec = rider.getRotationVec(1.0f);

        double y = 0;
        if (this.jumping) y = 0.25;
        else if (rider.isSneaking()) y = -0.25;

        mob.setVelocity(
                lookVec.x * rider.forwardSpeed * speed,
                y + (lookVec.y * rider.forwardSpeed * speed),
                lookVec.z * rider.forwardSpeed * speed
        );

        mob.move(net.minecraft.entity.MovementType.SELF, mob.getVelocity());
        mob.velocityDirty = true;

        float friction = (mob instanceof net.minecraft.entity.passive.BatEntity) ? 0.7f : 0.9f;
        mob.setVelocity(mob.getVelocity().multiply(friction));
    }

    private void handleAutoSlimeMovement(SlimeEntity slime, PlayerEntity rider) {
        float forward = rider.forwardSpeed;
        float strafe = rider.sidewaysSpeed;
        boolean isTryingToMove = Math.abs(forward) > 0.1f || Math.abs(strafe) > 0.1f || this.jumping;

        if (slime.isOnGround() && isTryingToMove && slime.getVelocity().y <= 0.01) {
            float sizeBoost = 0.1f * slime.getSize();
            double jumpHeight = 0.42 + sizeBoost;
            double jumpSpeed = 0.3 + (sizeBoost * 0.5);

            float yawRad = slime.getYaw() * 0.017453292F;
            float sinYaw = (float) Math.sin(yawRad);
            float cosYaw = (float) Math.cos(yawRad);

            double velX = ((-sinYaw * forward) + (cosYaw * strafe)) * jumpSpeed;
            double velZ = ((cosYaw * forward) + (sinYaw * strafe)) * jumpSpeed;

            slime.setVelocity(velX, jumpHeight, velZ);
            slime.velocityDirty = true;

            slime.getJumpControl().setActive();
            if (slime instanceof MagmaCubeEntity) {
                slime.playSound(SoundEvents.ENTITY_MAGMA_CUBE_JUMP, 1.0f, 1.0f);
            } else {
                slime.playSound(SoundEvents.ENTITY_SLIME_JUMP, 1.0f, 1.0f);
            }
        }

        if (slime.isOnGround() && !isTryingToMove) {
            slime.targetStretch = 0.0f;
            slime.stretch = 0.0f;
            slime.lastStretch = 0.0f;
        }

        slime.move(MovementType.SELF, slime.getVelocity());

        if (slime.isOnGround()) {
            slime.setVelocity(slime.getVelocity().multiply(0.5, 1.0, 0.5));
        } else {
            Vec3d currentVel = slime.getVelocity();
            slime.setVelocity(currentVel.x * 0.98, currentVel.y - 0.08, currentVel.z * 0.98);
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void rideanymob$forceRotationAfterVanilla(CallbackInfo ci) {

        LivingEntity self = (LivingEntity)(Object)this;

        if (!(self instanceof MobEntity mob)) return;
        if (!(mob.getControllingPassenger() instanceof PlayerEntity rider)) return;

        float yaw = rider.getYaw();
        float pitch = rider.getPitch();

        mob.setYaw(yaw);
        mob.prevYaw = yaw;

        mob.setPitch(pitch);
        mob.prevPitch = pitch;

        mob.setHeadYaw(yaw);
        mob.prevHeadYaw = yaw;

        mob.setBodyYaw(yaw);
    }



    private void handleGroundMovement(MobEntity mob, PlayerEntity rider, Vec3d movementInput) {
        float baseSpeed = (float) mob.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        mob.setMovementSpeed(baseSpeed * (rider.isSprinting() ? 1.3f : 1.0f));

        float forward = rider.forwardSpeed * 0.5f;
        float strafe = rider.sidewaysSpeed * 0.3f;

        if (this.jumping && mob.isOnGround()) {
            mob.setVelocity(mob.getVelocity().x, 0.42, mob.getVelocity().z);
            mob.velocityDirty = true;
        }

        try {
            IS_TRAVELLING.set(true);
            mob.travel(new Vec3d(strafe, movementInput.y, forward));
        } finally {
            IS_TRAVELLING.set(false);
        }
    }
}
