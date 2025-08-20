package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DashingProperty extends Property implements IDataHolder<Tuple<Boolean, Integer>> {

	private final int maxDashes;
	private final int[] colors;
	private final float dashStrength;

	public static final ParticleOptions CRYSTAL_PARTICLES = new SparkParticleOptions(AlchemancyParticles.CLOUD_SMOKE.get(), Vec3.fromRGB24(0xD877FF).toVector3f(), 2f, false);
	public static final ParticleOptions CLOUD_PARTICLES = new SparkParticleOptions(AlchemancyParticles.CLOUD_SMOKE.get(), Vec3.fromRGB24(0x54B4FF).toVector3f(), 2f, false);

	public DashingProperty(float dashStrength, int... colors) {
		this.maxDashes = colors.length - 1;
		this.colors = colors;
		this.dashStrength = dashStrength;
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		super.onEquippedTick(user, slot, stack);


		if (user.isSprinting() && !getSprinting(stack)) {
			if (!(!slot.isArmor() && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INTERACTABLE)))
				dash(user, stack, slot);
		} else if (user.onGround())
			setDashCount(stack, 0);

		setSprinting(stack, user.isSprinting());

		if (getData(stack).equals(getDefaultData()))
			removeData(stack);
	}

	private void setDashCount(ItemStack stack, int count) {
		setData(stack, new Tuple<>(getData(stack).getA(), count));
	}

	private int getDashCount(ItemStack stack) {
		return getData(stack).getB();
	}

	private void setSprinting(ItemStack stack, boolean sprinting) {
		setData(stack, new Tuple<>(sprinting, getData(stack).getB()));
	}

	private boolean getSprinting(ItemStack stack) {
		return getData(stack).getA();
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {

		if (source == null) return;
		if (source instanceof LivingEntity user)
			dash(user, stack, EquipmentSlot.MAINHAND);
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
		if (!isCurrentItem && (inventorySlot < 36 || inventorySlot > 40) && !InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AUXILIARY))
			removeData(stack);
	}

	public void dash(LivingEntity user, ItemStack stack, EquipmentSlot slot) {
		int dashes = getDashCount(stack);

		float dashStrength = this.dashStrength * user.getSpeed() * 5;

		if (dashes >= maxDashes) return;

		user.setDeltaMovement(user.getLookAngle().normalize().scale(dashStrength).add(user.getLookAngle().normalize().scale(Math.min(user.getDeltaMovement().length(), 7.5f)).scale(0.4f)));

		playParticles(user, stack, dashes);

		if (PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, stack.isDamageableItem()))
			stack.hurtAndBreak(PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 2), user, slot);

		setDashCount(stack, dashes + 1);
	}


	public void playParticles(Entity user, ItemStack stack, int dashes) {

		var particleSpeed = user.getDeltaMovement().scale(0.2f);
		for (int i = 0; i < 15; i++)
			user.level().addParticle(SparklingProperty.getParticles(stack).orElse(dashes < maxDashes - 1 ? CRYSTAL_PARTICLES : CLOUD_PARTICLES),
					user.getRandomX(1.2f), user.getY(user.getRandom().nextFloat() * 0.6f), user.getRandomZ(1.2f),
					particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
	}

	@Override
	public int getColor(ItemStack stack) {
		return colors[Math.min(colors.length - 1, getDashCount(stack))];
	}

	@Override
	public Tuple<Boolean, Integer> readData(CompoundTag tag) {
		return new Tuple<>(tag.getBoolean("sprinting"), Math.clamp(tag.getInt("dash_count"), 0, maxDashes));
	}

	@Override
	public CompoundTag writeData(Tuple<Boolean, Integer> data) {
		return new CompoundTag() {{
			putBoolean("sprinting", data.getA());
			putInt("dash_count", data.getB());
		}};
	}

	private static final Tuple<Boolean, Integer> DEFAULT = new Tuple<>(false, 0);

	@Override
	public Tuple<Boolean, Integer> getDefaultData() {
		return DEFAULT;
	}
}
