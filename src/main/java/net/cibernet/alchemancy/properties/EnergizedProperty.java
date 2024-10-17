package net.cibernet.alchemancy.properties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class EnergizedProperty extends AbstractTimerProperty
{
	private static final ResourceLocation SPEED_MOD_KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "energized_property_modifier");
	private static final long ENERGIZED_DURATION = 1200;


	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event)
	{
		Player player = event.getEntity();
		AttributeMap attributeMap = player.getAttributes();
		float energizedValue = 0;

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = player.getItemBySlot(slot);
			if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ENERGIZED))
				energizedValue += AlchemancyProperties.ENERGIZED.get().getEnergizedTime(stack);
		}
		energizedValue *= 0.35f;

		Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
		attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(SPEED_MOD_KEY, energizedValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MOD_KEY, energizedValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		attributes.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(SPEED_MOD_KEY, energizedValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

		attributeMap.removeAttributeModifiers(attributes);
		if(energizedValue > 0)
			attributeMap.addTransientAttributeModifiers(attributes);
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		if(damageSource.is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE))
			resetStartTimestamp(weapon);
	}

	public float getEnergizedTime(ItemStack stack)
	{
		if(getData(stack) == 0)
			return 0;

		return 1 - Mth.clamp(getElapsedTime(stack) / ENERGIZED_DURATION, 0, 1);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		return FastColor.ARGB32.lerp(getEnergizedTime(stack), 0x730C00, 0xE62008);
	}
}
