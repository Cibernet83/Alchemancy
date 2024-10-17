package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor
{
	@Invoker("collide")
	Vec3 invokeCollide(Vec3 vec3);

	@Invoker("getInputVector")
	static Vec3 invokeGetInputVector(Vec3 relative, float motionScaler, float facing) {
		return null;
	}
}
