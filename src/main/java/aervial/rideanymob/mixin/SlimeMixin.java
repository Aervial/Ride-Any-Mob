package aervial.rideanymob.mixin;

import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeEntity.class)
public abstract class SlimeMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void resetFallDistance(CallbackInfo ci) {
        SlimeEntity self = (SlimeEntity) (Object) this;
        if (self.getControllingPassenger() instanceof PlayerEntity) {
            ((EntityAccessor) self).setFallDistance(0.0f);
        }
    }

    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SlimeEntity;isOnGround()Z")
    )
    private boolean hideGroundState(SlimeEntity instance) {
        if (instance.getControllingPassenger() instanceof PlayerEntity rider) {
            boolean isMoving = Math.abs(rider.forwardSpeed) > 0.1f ||
                    Math.abs(rider.sidewaysSpeed) > 0.1f ||
                    ((LivingEntityAccessor) instance).isJumping();

            if (!isMoving && instance.isOnGround()) return false;
        }
        return instance.isOnGround();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void forceStillness(CallbackInfo ci) {
        SlimeEntity self = (SlimeEntity) (Object) this;
        if (self.getControllingPassenger() instanceof PlayerEntity rider) {
            boolean isMoving = Math.abs(rider.forwardSpeed) > 0.1f ||
                    Math.abs(rider.sidewaysSpeed) > 0.1f ||
                    ((LivingEntityAccessor) self).isJumping();

            if (!isMoving && self.isOnGround()) {
                self.targetStretch = 0.0f;
                self.stretch = 0.0f;
                self.lastStretch = 0.0f;
            }
        }
    }

    @Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$SlimeMoveControl")
    public static abstract class SlimeMoveControlMixin {
        @Shadow @Final private SlimeEntity slime;

        @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
        private void stopAi(CallbackInfo ci) {
            if (slime.getControllingPassenger() instanceof PlayerEntity rider) {
                boolean isMoving = Math.abs(rider.forwardSpeed) > 0.1f ||
                        Math.abs(rider.sidewaysSpeed) > 0.1f ||
                        ((LivingEntityAccessor) slime).isJumping();

                if (!isMoving && slime.isOnGround()) {
                    ci.cancel();
                }
            }
        }
    }
}