package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Projectile.class)
public interface ProjectileAccessor
{
	@Invoker("onHit")
	void invokeOnHit(HitResult result);

	@Invoker("canHitEntity")
	boolean invokeCanHitEntity(Entity entity);
}
