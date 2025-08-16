package net.cibernet.alchemancy.entity;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.ClientLevelAccessor;
import net.cibernet.alchemancy.mixin.accessors.LevelRendererAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
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

			playSound(SoundEvents.SPLASH_POTION_BREAK, 1, getRandom().nextFloat() * 0.1F + 0.9F);
			this.level().broadcastEntityEvent(this, (byte) 3);

			var infusions = getInfusions();

			if (!infusions.isEmpty()) {
				this.applySplash(infusions);
				level().broadcastEntityEvent(this, (byte) 4);
			}


			this.discard();
		}
	}

	private List<Holder<Property>> getInfusions() {
		ItemStack itemstack = this.getItem().copy();
		var infusions = new ArrayList<>(itemstack.is(AlchemancyTags.Items.DISABLES_INFUSION_ABILITIES) ? InfusedPropertiesHelper.getInfusedProperties(itemstack) : InfusedPropertiesHelper.getStoredProperties(itemstack));
		infusions.removeIf(propertyHolder -> propertyHolder.is(AlchemancyTags.Properties.IGNORED_BY_INFUSION_FLASK));
		return infusions;
	}

	private void playSplashEffects() {

		if (!level().isClientSide()) return;


		ItemStack itemstack = this.getItem().copy();
		var infusions = getInfusions();

		for (Holder<Property> infusion : infusions) {

			int color = infusion.value().getColor(itemstack);
			float r = (float) (color >> 16 & 0xFF) / 255.0F;
			float g = (float) (color >> 8 & 0xFF) / 255.0F;
			float b = (float) (color & 0xFF) / 255.0F;
			ParticleOptions particleoptions = ParticleTypes.INSTANT_EFFECT;
			var vec3 = position();

			for (int i2 = 0; i2 < 100; i2++) {
				double power = getRandom().nextDouble() * 4.0;
				double angle = getRandom().nextDouble() * Math.PI * 2.0;
				double xSpeed = Math.cos(angle) * power;
				double ySpeed = 0.01 + getRandom().nextDouble() * 0.5;
				double zSpeed = Math.sin(angle) * power;
				Particle particle = ((LevelRendererAccessor) ((ClientLevelAccessor) level()).getLevelRenderer()).invokeAddParticleInternal(
						particleoptions, particleoptions.getType().getOverrideLimiter(), vec3.x + xSpeed * 0.1, vec3.y + 0.3, vec3.z + zSpeed * 0.1, xSpeed, ySpeed, zSpeed
				);
				if (particle != null) {
					float colorOff = 0.75F + getRandom().nextFloat() * 0.25F;
					particle.setColor(r * colorOff, g * colorOff, b * colorOff);
					particle.setPower((float) power);
				}
			}
		}
	}

	private ParticleOptions getParticle() {
		ItemStack itemstack = this.getItem();
		return new ItemParticleOption(ParticleTypes.ITEM, itemstack.isEmpty() ? getDefaultItem().getDefaultInstance() : itemstack);
	}

	public void handleEntityEvent(byte id) {
		if (id == 3) {
			ParticleOptions particleoptions = this.getParticle();
			for (int j = 0; j < 8; j++) {
				level().addParticle(
						particleoptions,
						position().x,
						position().y,
						position().z,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15
				);
			}
		} else if (id == 4) {
			playSplashEffects();
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

			ItemStack entityStack = itemEntity.getItem();
			if(entityStack.is(AlchemancyTags.Items.IGNORED_BY_INFUSION_FLASK))
				continue;

			AtomicBoolean success = new AtomicBoolean(false);

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
			if (InfusedPropertiesHelper.canInfuseWithProperty(itemStack, property) && property.value().onInfusedByDormantProperty(itemStack, getItem(), grid, infusions, new AtomicBoolean(false)))
				perform = true;
		}

		if (perform) {
			ItemStack finalItemStack = itemStack;
			infusions.removeIf(propertyHolder -> !InfusedPropertiesHelper.canInfuseWithProperty(finalItemStack, propertyHolder));
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
