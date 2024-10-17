package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LeapingProperty extends Property implements IDataHolder<LeapingProperty.LeapData>
{
	public static final AttributeModifier SAFE_FALL_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "leaping_property_modifier"), 7, AttributeModifier.Operation.ADD_VALUE);
	public static final AttributeModifier ANIMAL_JUMP_BOOST_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "leaping_property_modifier"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	private static final int GRACE_TIME = 4;
	private static final int MAX_CHAIN = 4;

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		EquipmentSlot slot = getEquipmentSlotForItem(event.getItemStack());
		if(slot == EquipmentSlot.LEGS)
			event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.LEGS);
		else if(slot == EquipmentSlot.FEET)
			event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.FEET);
		else if(slot == EquipmentSlot.BODY)
		{
			event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.BODY);
			event.addModifier(Attributes.JUMP_STRENGTH, ANIMAL_JUMP_BOOST_MOD, EquipmentSlotGroup.BODY);
		}
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(!user.level().isClientSide || (slot != EquipmentSlot.FEET && slot != EquipmentSlot.LEGS))
			return;

		LeapData data = getData(stack);
		int chain = data.chain;
		int timestamp = data.lastLandedTimestamp;
		boolean wasOnGround = data.wasOnGround;

		if(wasOnGround != user.onGround())
		{
			if(user.onGround())
				timestamp = user.tickCount;
			wasOnGround = user.onGround();
		}
		else if(user.onGround() && (user.tickCount < timestamp || user.tickCount > timestamp + GRACE_TIME))
			chain = 0;

		LeapData newData = new LeapData(chain, timestamp, wasOnGround);
		if(!data.equals(newData))
			setData(stack, newData);
	}

	@Override
	public void onJump(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingEvent.LivingJumpEvent event)
	{
		if(!entity.level().isClientSide() || (slot != EquipmentSlot.FEET && slot != EquipmentSlot.LEGS))
			return;

		LeapData data = getData(stack);

		System.out.println(entity.level().isClientSide() + " chain: " + data.chain + " timestamp: " + data.lastLandedTimestamp + " player tick: " + event.getEntity().tickCount);

		event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().multiply(1, 1.2 + data.chain * 0.2, 1));
		if(data.chain < MAX_CHAIN)
			setData(stack, new LeapData(data.chain + 1, data.lastLandedTimestamp, data.wasOnGround));
	}

	@Override
	public int getColor(ItemStack stack) {
		return MobEffects.JUMP.value().getColor();
	}

	@Override
	public LeapData readData(CompoundTag tag) {
		return new LeapData(tag.getInt("chain"), tag.getInt("last_landed_timestamp"), tag.getBoolean("was_on_ground"));
	}

	@Override
	public CompoundTag writeData(LeapData data) {
		return new CompoundTag(){{
			putInt("chain", data.chain);
			putInt("last_landed_timestamp", data.lastLandedTimestamp);
			putBoolean("was_on_ground", data.wasOnGround);
		}};
	}

	private static final LeapData DEFAULT = new LeapData(0, 0, true);

	@Override
	public LeapData getDefaultData() {
		return DEFAULT;
	}

	public record LeapData(int chain, int lastLandedTimestamp, boolean wasOnGround){}
}
