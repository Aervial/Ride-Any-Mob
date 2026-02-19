package aervial.rideanymob.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$SlimeMoveControl")
public interface SlimeMoveControlAccessor {
    @Accessor("ticksUntilJump")
    void setTicksUntilJump(int ticks);
}