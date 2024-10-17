package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor
{
	@Invoker("getWaterInertia")
	float invokeGetWaterInertia();

	@Accessor("inGroundTime")
	int accessInGroundTime();

	@Accessor("inGround")
	boolean accessInGround();

	@Accessor("inGround")
	void setInGround(boolean value);


}
