package net.cibernet.alchemancy.properties;

import it.unimi.dsi.fastutil.ints.IntList;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.network.S2CPlayFireworksPayload;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CracklingProperty extends Property implements IDataHolder<Fireworks> {

	@Override
	public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) {
		if (target.level() instanceof ServerLevel level) {
			var fireworks = getData(weapon);
			var targetPos = target.getEyePosition();
			float damage = 2.0F;

			if (fireworks != null) {
				PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new S2CPlayFireworksPayload(fireworks, targetPos));
				List<FireworkExplosion> list = fireworks.explosions();
				if (!list.isEmpty()) {
					damage = 5.0F + (float) (list.size() * 2);
				}
			} else PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new S2CPlayFireworksPayload(DEFAULT, targetPos));

			for (LivingEntity livingentity : level.getEntitiesOfClass(LivingEntity.class, CommonUtils.boundingBoxAroundPoint(targetPos, 5))) {
				if (target != user && !(targetPos.distanceToSqr(livingentity.position()) > 25.0)) {
					boolean flag = false;

					for (int i = 0; i < 2; i++) {
						Vec3 vec31 = new Vec3(livingentity.getX(), livingentity.getY(0.5 * (double) i), livingentity.getZ());
						HitResult hitresult = level.clip(new ClipContext(targetPos, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target));
						if (hitresult.getType() == HitResult.Type.MISS) {
							flag = true;
							break;
						}
					}

					if (flag) {
						float localDamage = damage * (float) Math.sqrt((5.0 - targetPos.distanceTo(livingentity.position())) / 5.0);
						livingentity.hurt(level.damageSources().source(DamageTypes.FIREWORKS, user, user), localDamage);
					}
				}
			}

			if(PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, weapon.isDamageableItem()))
			{
				int durabilityConsumed = PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, (int) (damage / 2));
				if(user instanceof LivingEntity living)
					weapon.hurtAndBreak(durabilityConsumed, living, EquipmentSlot.MAINHAND);
				else if(target.level() instanceof ServerLevel serverLevel) weapon.hurtAndBreak(durabilityConsumed, serverLevel, null, (item) -> {});
			}
			else consumeItem(user, weapon, EquipmentSlot.MAINHAND);
		}
	}

	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, AtomicBoolean consumeItem) {

		Fireworks stackFireworks = propertySource.get(DataComponents.FIREWORKS);
		if (stackFireworks == null && propertySource.has(DataComponents.FIREWORK_EXPLOSION))
			stackFireworks = new Fireworks(0, List.of(propertySource.get(DataComponents.FIREWORK_EXPLOSION)));
		if(stackFireworks != null && stackFireworks.explosions().isEmpty())
			return false;

		if (InfusedPropertiesHelper.hasInfusedProperty(stack, asHolder())) {
			var stackExplosions = stackFireworks == null ? List.of() : stackFireworks.explosions();
			Fireworks currentFireworks = getData(stack);
			var currentExplosions = currentFireworks == null ? List.of() : currentFireworks.explosions();

			if (stackExplosions == currentExplosions) return false;
		}

		setData(stack, stackFireworks);
		return true;
	}

	private static final float timePerStar = 2;

	@Override
	public Component getDisplayText(ItemStack stack) {

		var fireworks = getData(stack);
		if (fireworks == null || fireworks.explosions().isEmpty()) return super.getDisplayText(stack);

		var explosions = fireworks.explosions();
		int currentIndex = (int) Math.abs((System.currentTimeMillis() / (1000 * (double)timePerStar)) % explosions.size());
		var explosion = explosions.get(currentIndex);

		MutableComponent colors = Component.empty();

		for (int i = 0; i < explosion.colors().size(); i++) {
			colors = colors.append(Component.translatable("property.detail.star").withColor(
					explosion.fadeColors().isEmpty() ? explosion.colors().getInt(i) :
							ColorUtils.interpolateColorsOverTime(timePerStar,
									explosion.colors().getInt(i),
									explosion.fadeColors().getInt(Math.min(i, explosion.fadeColors().size() - 1))
							)));
		}

		return Component.translatable("property.crackling_detail", super.getDisplayText(stack), colors, explosion.shape().getName().withStyle(ChatFormatting.WHITE)).withColor(getColor(stack));
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(0.5f, 0xD62A2A, 0xFF8888);
	}

	@Override
	public Fireworks readData(CompoundTag tag) {
		return tag.contains("fireworks") ? Fireworks.CODEC.parse(NbtOps.INSTANCE, tag.get("fireworks")).getOrThrow() : null;
	}

	@Override
	public CompoundTag writeData(Fireworks data) {
		return new CompoundTag() {{
			if(data != null)
				put("fireworks", Fireworks.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
		}};
	}

	private static final Fireworks DEFAULT = new Fireworks(0, List.of(new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(0xFFFFFF), IntList.of(), false, false)));

	@Override
	public Fireworks getDefaultData() {
		return DEFAULT;
	}
}
