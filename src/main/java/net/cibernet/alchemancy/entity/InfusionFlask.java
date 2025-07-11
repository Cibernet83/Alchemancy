package net.cibernet.alchemancy.entity;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

			this.level().broadcastEntityEvent(this, (byte)3);

			ItemStack itemstack = this.getItem();
			var infusions = new ArrayList<>(itemstack.is(AlchemancyTags.Items.DISABLES_INFUSION_ABILITIES) ? InfusedPropertiesHelper.getInfusedProperties(itemstack) : InfusedPropertiesHelper.getStoredProperties(itemstack));
			infusions.removeIf(propertyHolder -> propertyHolder.is(AlchemancyTags.Properties.IGNORED_BY_INFUSION_FLASK));

			if (!infusions.isEmpty()) {
				this.applySplash(infusions);
				for (Holder<Property> infusion : infusions) {
					this.level().levelEvent(2007, this.blockPosition(), infusion.value().getColor(itemstack));
				}
			}


			this.discard();
		}
	}

	private ParticleOptions getParticle() {
		ItemStack itemstack = this.getItem();
		return new ItemParticleOption(ParticleTypes.ITEM, itemstack.isEmpty() ? getDefaultItem().getDefaultInstance() : itemstack);
	}

	public void handleEntityEvent(byte id) {
		if (id == 3) {
			ParticleOptions particleoptions = this.getParticle();

			for(int i = 0; i < 8; ++i) {
				this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.2 * random.nextDouble() - 0.1, random.nextDouble() * 0.2 + 0.05, 0.2 * random.nextDouble() - 0.1);
			}
		}

	}

	@Override
	protected double getDefaultGravity() {
		return 0.05;
	}

	private static final int MAX_AFFECTED_ITEMS = 32; //FIXME maybe make this configurable

	private void applySplash(List<Holder<Property>> infusions) {

		AABB aabb = this.getBoundingBox().inflate(SPLASH_RANGE, SPLASH_RANGE * 0.5, SPLASH_RANGE);

		int items = 0;

		for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {

				AtomicBoolean success = new AtomicBoolean(false);

				ItemStack entityStack = living.getItemBySlot(slot);
				ItemStack splitStack = entityStack.split(MAX_AFFECTED_ITEMS - items);
				ItemStack itemStack = infuseItem(splitStack, infusions, success);

				if (!entityStack.isEmpty()) {
					if (!itemStack.isEmpty())
						level().addFreshEntity(new ItemEntity(level(), living.getX(), living.getEyeY(), living.getZ(), itemStack));
				} else living.setItemSlot(slot, itemStack);

				if (success.get()) {
					items += splitStack.getCount();
					if (items >= MAX_AFFECTED_ITEMS)
						return;
				}
			}
		}

		for (ItemEntity itemEntity : this.level().getEntitiesOfClass(ItemEntity.class, aabb)) {

			AtomicBoolean success = new AtomicBoolean(false);

			ItemStack entityStack = itemEntity.getItem();
			ItemStack splitStack = entityStack.split(MAX_AFFECTED_ITEMS - items);
			ItemStack itemStack = infuseItem(splitStack, infusions, success);

			if (!entityStack.isEmpty()) {
				if (!itemStack.isEmpty()) {
					var newItem = new ItemEntity(level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemStack);
					newItem.setDefaultPickUpDelay();
					level().addFreshEntity(newItem);
				}
			} else {
				if (itemStack.isEmpty())
					itemEntity.discard();
				else itemEntity.setItem(itemStack);
			}
			if (success.get()) {
				items += splitStack.getCount();
				if (items >= MAX_AFFECTED_ITEMS)
					return;
			}
		}
	}

	private ItemStack infuseItem(ItemStack itemStack, List<Holder<Property>> infusions, AtomicBoolean successful) {

		if (itemStack.is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS))
			return itemStack;

		boolean perform = false;
		var grid = new ForgeRecipeGrid(itemStack);

		for (Holder<Property> property : List.copyOf(infusions)) {
			if (property.value().onInfusedByDormantProperty(itemStack, getItem(), grid, infusions, new AtomicBoolean(false)))
				perform = true;
		}

		if (perform) {
			InfusedPropertiesHelper.addProperties(itemStack, infusions);
			itemStack = ForgeRecipeGrid.resolveInteractions(itemStack, level());
			successful.set(true);
		}

		return itemStack;
	}

	@Override
	protected Item getDefaultItem() {
		return AlchemancyItems.INFUSION_FLASK.asItem();
	}
}
