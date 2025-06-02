package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.client.particle.SparkParticle;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlinkingProperty extends Property implements IDataHolder<Boolean> {

	public static final ParticleOptions PARTICLES = new SparkParticle.Options(Vec3.fromRGB24(0x00FFFF).toVector3f(), 1);

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		super.onEquippedTick(user, slot, stack);

		if (user.isSprinting() && !getData(stack) && !(!slot.isArmor() && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INTERACTABLE)))
			blink(user, stack, slot);

		setData(stack, user.isSprinting());
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {

		if (source == null) return;
		if (source instanceof LivingEntity user)
			blink(user, stack, EquipmentSlot.MAINHAND);
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
		if (!isCurrentItem && (inventorySlot < 36 || inventorySlot > 40)) removeData(stack);
	}

	public void blink(LivingEntity user, ItemStack stack, EquipmentSlot slot) {
		float range = 10 * (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.EXTENDED) ? 2 : 1);

		for (int i = 0; i < range; i++) {
			var hit = user.level().clip(new ClipContext(user.getEyePosition(), user.getEyePosition().add(user.getLookAngle().normalize().scale(range - i)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, user));
			var hitPos = hit.getLocation().subtract(hit.getLocation().subtract(user.getEyePosition()).normalize().scale(user.getBbWidth() * 0.5f));
			var verticalHit = user.level().clip(new ClipContext(hitPos, hitPos.subtract(0, user.getEyeHeight(), 0), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, user));

			if (user.level().noBlockCollision(user, user.getLocalBoundsForPose(Pose.SWIMMING).move(verticalHit.getLocation()).move(-user.getBbWidth() * 0.5f, 0, -user.getBbWidth() * 0.5f))) {

				playParticles(user, verticalHit.getLocation(), stack, 20, 5);
				user.moveTo(verticalHit.getLocation());
				user.setDeltaMovement(user.getLookAngle().normalize().scale(user.getDeltaMovement().length()));

				if(PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, stack.isDamageableItem()))
					stack.hurtAndBreak(PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 5), user, slot);
				else consumeItem(user, stack, slot);

				break;
			}
		}
	}


	public static void playParticles(Entity user, Vec3 destination, ItemStack stack, int segments, int layers) {
		if (user.level() instanceof ServerLevel serverLevel)
			for (int i = 0; i < segments; i++) {
				int currentLayers = layers - Math.max(0, (layers - Math.ceilDiv(i, 3)));
				for (int y = 0; y < currentLayers; y++) {
					Vec3 vec = user.position().lerp(destination, (double) i / segments);
					double yy = (double) y / layers * user.getBbHeight() + (user.getBbHeight() / (currentLayers + 1));

					serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(PARTICLES), vec.x, vec.y + (yy), vec.z, 1, 0, 0, 0, 0);//user.getBbWidth() * 0.5f, 0, user.getBbWidth() * 0.5f, 0);
				}
			}
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(0.5f, 0x00FFFF, 0x8CFFFF);
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
	}

	@Override
	public Boolean readData(CompoundTag tag) {
		return tag.getBoolean("sprinting");
	}

	@Override
	public CompoundTag writeData(Boolean data) {
		return new CompoundTag() {{
			putBoolean("sprinting", data);
		}};
	}

	@Override
	public Boolean getDefaultData() {
		return false;
	}
}
