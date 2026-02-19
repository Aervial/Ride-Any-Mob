package aervial.rideanymob.mixin;

import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$SlimeMoveControl")
public abstract class SlimeSoundKillerMixin {

    @Shadow @Final
    private SlimeEntity slime;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void silenceAi(CallbackInfo ci) {
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