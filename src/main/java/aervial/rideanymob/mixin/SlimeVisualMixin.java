package aervial.rideanymob.mixin;

import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeEntity.class)
public abstract class SlimeVisualMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void handleVisuals(CallbackInfo ci) {
        SlimeEntity self = (SlimeEntity) (Object) this;
        if (self.getControllingPassenger() instanceof PlayerEntity rider) {
            boolean isJumping = ((LivingEntityAccessor) self).isJumping();
            boolean isMoving = Math.abs(rider.forwardSpeed) > 0.1f ||
                    Math.abs(rider.sidewaysSpeed) > 0.1f ||
                    isJumping;

            if (!isMoving && self.isOnGround()) {
                self.targetStretch = 0.0f;
                self.stretch = 0.0f;
                self.lastStretch = 0.0f;
            }
        }
    }
}
