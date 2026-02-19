package aervial.rideanymob.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void isCamera(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        if (self.getVehicle() instanceof MobEntity) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "tickRiding", at = @At("TAIL"))
    private void onTickRiding(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        if (self.getVehicle() instanceof MobEntity mob) {
            mob.setJumping(self.input.jumping);
        }
    }
}