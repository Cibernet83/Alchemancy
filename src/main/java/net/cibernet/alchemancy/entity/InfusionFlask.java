package net.cibernet.alchemancy.entity;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class InfusionFlask extends ThrowableItemProjectile implements ItemSupplier {

	public static final double SPLASH_RANGE = 4.0;

	public InfusionFlask(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public InfusionFlask(Level level, double x, double y, double z) {
		super(AlchemancyEntities.INFUSION_FLASK.get(), x, y, z, level);
	}


	public InfusionFlask(Level level, LivingEntity shooter) {
		super(AlchemancyEntities.INFUSION_FLASK.get(), shooter, level);
	}

	/**
	 * Called when this EntityFireball hits a block or entity.
	 */
	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level().isClientSide) {
			ItemStack itemstack = this.getItem();
			var infusions = itemstack.is(AlchemancyTags.Items.DISABLES_INFUSION_ABILITIES) ? InfusedPropertiesHelper.getInfusedProperties(itemstack) : InfusedPropertiesHelper.getStoredProperties(itemstack);

			if (!infusions.isEmpty()) {
				this.applySplash(infusions);
			}

			for (Holder<Property> infusion : infusions) {
				this.level().levelEvent(2007, this.blockPosition(), infusion.value().getColor(itemstack));
			}
			this.discard();
		}
	}

	@Override
	protected double getDefaultGravity() {
		return 0.05;
	}

	private void applySplash(List<Holder<Property>> infusions) {

		AABB aabb = this.getBoundingBox().inflate(SPLASH_RANGE, SPLASH_RANGE * 0.5, SPLASH_RANGE);

		for (ItemEntity itemEntity : this.level().getEntitiesOfClass(ItemEntity.class, aabb)) {
			ItemStack itemStack = infuseItem(itemEntity.getItem(), infusions);

			if (itemStack.isEmpty())
				itemEntity.discard();
			else itemEntity.setItem(itemStack);
		}

		for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				living.setItemSlot(slot, infuseItem(living.getItemBySlot(slot), infusions));
			}
		}
	}

	private ItemStack infuseItem(ItemStack itemStack, List<Holder<Property>> infusions) {

		if(itemStack.is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS))
			return itemStack;

		boolean perform = false;
		var grid = new ForgeRecipeGrid(itemStack);

		for (Holder<Property> property : List.copyOf(infusions)) {
			if (property.value().onInfusedByDormantProperty(itemStack, getItem(), grid, infusions))
				perform = true;
		}

		if (perform) {
			InfusedPropertiesHelper.addProperties(itemStack, infusions);
			itemStack = ForgeRecipeGrid.resolveInteractions(itemStack, level());
		}

		return itemStack;
	}

	@Override
	protected Item getDefaultItem() {
		return AlchemancyItems.INFUSION_FLASK.asItem();
	}
}
