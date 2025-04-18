package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.Function;

public class ConditionalDamageReductionProperty extends Property
{
	private final Function<ItemStack, Integer> color;
	private final TriFunction<LivingEntity, EquipmentSlot, DamageSource, Float> damageMultiplier;

	public static ConditionalDamageReductionProperty reduceShockDamage(int color)
	{
		return new ConditionalDamageReductionProperty((stack) -> color, ((living, equipmentSlot, damageSource) ->
				(equipmentSlot.isArmor() || living.getItemBySlot(equipmentSlot).equals(living.getUseItem())) && damageSource.is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE) ? equipmentSlot == EquipmentSlot.BODY ? 0 : 0.75f : 1));
	}

	public static ConditionalDamageReductionProperty reduceDamageByTag(int color, TagKey<DamageType> damageTypeTag, float multiplier)
	{
		return new ConditionalDamageReductionProperty((stack) -> color, ((living, equipmentSlot, damageSource) ->
				(equipmentSlot.isArmor() || living.getItemBySlot(equipmentSlot).equals(living.getUseItem())) && damageSource.is(damageTypeTag) ? equipmentSlot == EquipmentSlot.BODY ? 0 : multiplier : 1));
	}

	public ConditionalDamageReductionProperty(Function<ItemStack, Integer> color, TriFunction<LivingEntity, EquipmentSlot, DamageSource, Float> damageMultiplier) {
		this.color = color;
		this.damageMultiplier = damageMultiplier;
	}

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		event.setNewDamage(event.getNewDamage() * damageMultiplier.apply(user, slot, event.getSource()));
	}

	@Override
	public int getColor(ItemStack stack) {
		return color.apply(stack);
	}
}
