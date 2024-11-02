package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class DepthDwellerProperty extends Property
{

	private static final ResourceLocation MOD_KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "depth_dweller_property_modifier");

	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event)
	{
		event.setNewDamage(event.getNewDamage() * (1 + getDepthScale(user) * 0.75f));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		float value = getDepthScale(user) * 0.5f;
		if(user instanceof Player player && slot == EquipmentSlot.MAINHAND)
		{
			AttributeInstance blockBreakSpeed = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
			blockBreakSpeed.removeModifier(MOD_KEY);
			blockBreakSpeed.addPermanentModifier(new AttributeModifier(MOD_KEY, value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}

		if(slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY)
		{
			AttributeInstance movementSpeed = user.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
			movementSpeed.removeModifier(MOD_KEY);
			movementSpeed.addPermanentModifier(new AttributeModifier(MOD_KEY, value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPlayerTick(PlayerTickEvent.Pre event)
	{
		if(event.getEntity().level().isClientSide)
			return;

		Player player = event.getEntity();

		AttributeInstance blockBreakSpeed = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
		if(blockBreakSpeed != null && blockBreakSpeed.hasModifier(MOD_KEY) && !InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.DEPTH_DWELLER))
				blockBreakSpeed.removeModifier(MOD_KEY);


		AttributeInstance moveSpeed = player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
		if(moveSpeed != null && moveSpeed.hasModifier(MOD_KEY))
		{
			boolean equipped = false;
			for (EquipmentSlot slot : EquipmentSlot.values())
				if(slot.isArmor() && InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.DEPTH_DWELLER))
				{
					equipped = true;
					break;
				}

			if(!equipped)
				moveSpeed.removeModifier(MOD_KEY);
		}
	}

	public static float getDepthScale(Entity user)
	{
		if(user.level().dimensionTypeRegistration().is(AlchemancyTags.Dimensions.DEPTH_DWELLER_EFFECTIVE))
			return 2;
		return Mth.clamp(((float) user.position().y - 10) / (user.level().getMinBuildHeight() - 10), 0, 1);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		if(ServerLifecycleHooks.getCurrentServer() == null || ServerLifecycleHooks.getCurrentServer() instanceof IntegratedServer)
		{
			float scale = getDepthScale(ClientUtil.getLocalPlayer());
			if(scale > 1)
				return 0x511515;
			else return FastColor.ARGB32.lerp(scale, 0x646464, 0x2F2F37);
		}
		else return 0x646464;
	}
}
