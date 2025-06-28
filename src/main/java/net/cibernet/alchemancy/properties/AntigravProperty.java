package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.mixin.accessors.ProjectileAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.List;

public class AntigravProperty extends Property
{
	private static final AttributeModifier GRAVITY_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "zero_gravity_property"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		if(getEquipmentSlotForItem(event.getItemStack()).isArmor())
			event.addModifier(Attributes.GRAVITY, GRAVITY_MOD, EquipmentSlotGroup.ARMOR);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.setNoGravity(true);


		if(!projectile.onGround() && projectile.getKnownMovement().length() < 0.005f) {
			List<Entity> targets = projectile.level().getEntitiesOfClass(Entity.class, projectile.getBoundingBox(), entity -> ((ProjectileAccessor) projectile).invokeCanHitEntity(entity));
			if(!targets.isEmpty())
			{
				var hitresult = new EntityHitResult(targets.getFirst());
				if (!net.neoforged.neoforge.event.EventHooks.onProjectileImpact(projectile, hitresult))
					((ProjectileAccessor) projectile).invokeOnHit(hitresult);
			}
		}
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity)
	{
		itemEntity.setNoGravity(true);
	}

	@Override
	public void onFall(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingFallEvent event)
	{
		if(slot.isArmor())
			event.setDamageMultiplier(0);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x423EB6;
	}
}
