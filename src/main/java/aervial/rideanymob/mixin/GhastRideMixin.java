package aervial.rideanymob.mixin;

import aervial.rideanymob.mixin.LivingEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhastEntity.class)
public abstract class GhastRideMixin extends MobEntity {

    protected GhastRideMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    public void travel(Vec3d movementInput) {
        if (this.isAlive() && this.getControllingPassenger() instanceof PlayerEntity rider) {

            this.setTarget(null);

            this.setYaw(rider.getYaw());
            this.prevYaw = this.getYaw();
            this.setPitch(rider.getPitch() * 0.5f);
            this.setRotation(this.getYaw(), this.getPitch());
            this.bodyYaw = this.getYaw();
            this.headYaw = this.bodyYaw;

            float sideways = rider.sidewaysSpeed * 0.5f;
            float forward = rider.forwardSpeed;
            double vertical = 0;

            if (forward > 0) {
                vertical = -Math.sin(Math.toRadians(rider.getPitch()));
            }

            if (((LivingEntityAccessor) this).isJumping()) {
                vertical = 0.5;
            }

            if (this.isLogicalSideForUpdatingMovement()) {
                float speed = (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                this.setMovementSpeed(speed);
                super.travel(new Vec3d(sideways, vertical, forward));
            }
            return;
        }

        super.travel(movementInput);
    }


    @Inject(method = "setShooting", at = @At("HEAD"), cancellable = true)
    private void stopShooting(boolean shooting, CallbackInfo ci) {
        if (this.getControllingPassenger() instanceof PlayerEntity) {
            if (shooting) {
                ci.cancel();
            }
        }
    }
}