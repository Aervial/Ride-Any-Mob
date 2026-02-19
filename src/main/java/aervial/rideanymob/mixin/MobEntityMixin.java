package aervial.rideanymob.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        MobEntity self = (MobEntity) (Object) this;
        if (player.isSneaking() || self.hasPassengers()) return;

        EntityType<?> type = self.getType();
        if (type == EntityType.HORSE || type == EntityType.PIG || type == EntityType.STRIDER) return;

        if (!self.getWorld().isClient) {
            player.startRiding(self, true);
        }
        cir.setReturnValue(ActionResult.success(self.getWorld().isClient));
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    private void onGetControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        MobEntity self = (MobEntity) (Object) this;
        if (self.getFirstPassenger() instanceof PlayerEntity player) {
            cir.setReturnValue(player);
        }
    }
}