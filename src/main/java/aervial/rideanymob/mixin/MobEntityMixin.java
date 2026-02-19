package aervial.rideanymob.mixin;

import aervial.rideanymob.RideToggle;
import aervial.rideanymob.whitelist.RideWhitelist;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void rideanymob$interact(PlayerEntity player, Hand hand,
                                     CallbackInfoReturnable<ActionResult> cir) {

        MobEntity self = (MobEntity)(Object)this;

        if (hand != Hand.MAIN_HAND) return;

        if (!RideToggle.isEnabled()) {
            return; // allow vanilla interaction
        }

        if (self.getWorld().isClient) return;

        if (player.isSneaking()) return;

        if (self.hasPassengers()) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
            return;
        }

        if (self.getType() == EntityType.HORSE ||
                self.getType() == EntityType.PIG ||
                self.getType() == EntityType.STRIDER) {
            return;
        }

        if (!RideWhitelist.isWhitelisted(player.getUuid())) {
            player.sendMessage(
                    Text.literal("You are not whitelisted to ride mobs."),
                    true // action bar
            );
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
            return;
        }

        player.startRiding(self, true);

        cir.setReturnValue(ActionResult.SUCCESS);
        cir.cancel();
    }


    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    private void rideanymob$getController(CallbackInfoReturnable<LivingEntity> cir) {

        MobEntity self = (MobEntity)(Object)this;

        if (self.getFirstPassenger() instanceof PlayerEntity player) {
            cir.setReturnValue(player);
        }
    }
}
