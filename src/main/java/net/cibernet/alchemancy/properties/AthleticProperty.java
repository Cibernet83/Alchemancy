package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class AthleticProperty extends Property
{

	private static final ResourceLocation MOD_KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "athletic_property_modifier");
	private static final AttributeModifier SPEED_MOD = new AttributeModifier(MOD_KEY, 0.65f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	private static final AttributeModifier SAFE_FALL_MOD = new AttributeModifier(MOD_KEY, 3f, AttributeModifier.Operation.ADD_VALUE);

	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event)
	{
		event.setNewDamage(event.getNewDamage() * (1 + getDepthScale(user) * 0.75f));
	}


	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if((slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY))
		{
			AttributeInstance movementSpeed = user.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
			if(movementSpeed != null)
			{
				movementSpeed.removeModifier(MOD_KEY);
				if(user.isSprinting())
					movementSpeed.addPermanentModifier(SPEED_MOD);
			}
			AttributeInstance jumpStrength = user.getAttributes().getInstance(Attributes.JUMP_STRENGTH);
			if(jumpStrength != null)
			{
				jumpStrength.removeModifier(MOD_KEY);
				if(user.isSprinting())
					jumpStrength.addPermanentModifier(SPEED_MOD);
			}
			AttributeInstance safeFall = user.getAttributes().getInstance(Attributes.SAFE_FALL_DISTANCE);
			if(safeFall != null)
			{
				safeFall.removeModifier(MOD_KEY);
				if(user.isSprinting())
					safeFall.addPermanentModifier(SAFE_FALL_MOD);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPlayerTick(PlayerTickEvent.Pre event)
	{
		if(event.getEntity().level().isClientSide)
			return;

		Player player = event.getEntity();

		AttributeInstance moveSpeed = player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
		AttributeInstance jumpStrength = player.getAttributes().getInstance(Attributes.JUMP_STRENGTH);
		AttributeInstance safeFall = player.getAttributes().getInstance(Attributes.SAFE_FALL_DISTANCE);
		if((moveSpeed != null && moveSpeed.hasModifier(MOD_KEY))
				|| (jumpStrength != null && jumpStrength.hasModifier(MOD_KEY))
				|| (safeFall != null && safeFall.hasModifier(MOD_KEY)))
		{
			boolean equipped = false;
			for (EquipmentSlot slot : EquipmentSlot.values())
				if(slot.isArmor() && InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.ATHLETIC))
				{
					equipped = true;
					break;
				}

			if(!equipped)
			{
				if(moveSpeed != null) moveSpeed.removeModifier(MOD_KEY);
				if(jumpStrength != null) jumpStrength.removeModifier(MOD_KEY);
				if(safeFall != null) safeFall.removeModifier(MOD_KEY);
			}
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
		return 0x409DB7;
	}
}
