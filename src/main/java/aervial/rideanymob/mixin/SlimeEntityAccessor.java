package aervial.rideanymob.mixin;

import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlimeEntity.class)
public interface SlimeEntityAccessor {
    @Accessor("onGroundLastTick")
    void setOnGroundLastTick(boolean value);
}