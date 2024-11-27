package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor
{
	@Accessor("jumping")
	boolean isJumping();

	@Accessor("jumping")
	void setJumping(boolean jumping);
}
