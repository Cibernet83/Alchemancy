package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;

import java.util.List;

public class StickyProperty extends Property
{
	private static final AttributeModifier SPEED_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "sticky_property_speed_penalty"), -0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	@Override
	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult) {
		return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
	}

	@Override
	public void onItemTossed(Player player, ItemStack stack, ItemEntity itemEntity, ItemTossEvent event)
	{
		stack = stack.copy();
		if(event.getPlayer().getMainHandItem().isEmpty())
		{
			event.getPlayer().setItemInHand(InteractionHand.MAIN_HAND, stack);
			event.setCanceled(true);
		}
		else if(event.getPlayer().addItem(stack))
			event.setCanceled(true);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{

		if(slot != EquipmentSlot.FEET && user.hurtTime > 0) return;

		Vec3 delta = user.getDeltaMovement();
		float halfWidth = user.getBbWidth()/2f;
		Level level = user.level();

		if(slot == EquipmentSlot.HEAD)
		{
			if(!level.noBlockCollision(user, new AABB(user.getX()-halfWidth, user.getY()+user.getBbHeight(), user.getZ()-halfWidth,
					user.getX()+halfWidth, user.getY()+user.getBbHeight() + 0.1f, user.getZ()+halfWidth)))
			{
				user.setDeltaMovement(delta.x, Math.max(delta.y, 0), delta.z);
				user.resetFallDistance();
			}
		}
		else if (slot == EquipmentSlot.FEET)
		{
			if(!level.noBlockCollision(user, new AABB(user.getX()-halfWidth, user.getY(), user.getZ()-halfWidth,
								user.getX()+halfWidth, user.getY() - 0.1f, user.getZ()+halfWidth)))
				user.setDeltaMovement(delta.x, Math.min(delta.y, 0.05), delta.z);
		}
		else if(slot == EquipmentSlot.CHEST)
		{
			halfWidth += 0.05f;
			if(!level.noBlockCollision(user, new AABB(user.getX()-halfWidth, user.getY() + user.getBbHeight() * 0.5f, user.getZ()-halfWidth,
					user.getX()+halfWidth, user.getY() + user.getBbHeight(), user.getZ()+halfWidth)))
			{
				user.setDeltaMovement(delta.x, Math.max(delta.y, 0), delta.z);
				user.resetFallDistance();
			}
		} if(slot == EquipmentSlot.LEGS)
		{
			halfWidth += 0.05f;
			if(!level.noBlockCollision(user, new AABB(user.getX()-halfWidth, user.getY(), user.getZ()-halfWidth,
					user.getX()+halfWidth, user.getY() + user.getBbHeight() * 0.5f, user.getZ()+halfWidth)))
			{
				user.setDeltaMovement(delta.x, Math.max(delta.y, 0), delta.z);
				user.resetFallDistance();
			}
		}
	}

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		if(getEquipmentSlotForItem(event.getItemStack()) == EquipmentSlot.FEET)
			event.addModifier(Attributes.MOVEMENT_SPEED, SPEED_MOD, EquipmentSlotGroup.FEET);
		else if(getEquipmentSlotForItem(event.getItemStack()) == EquipmentSlot.BODY)
			event.addModifier(Attributes.MOVEMENT_SPEED, SPEED_MOD, EquipmentSlotGroup.BODY);
	}

	@Override
	public void onJump(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingEvent.LivingJumpEvent event)
	{
		float halfWidth = user.getBbWidth()/2f;
		Vec3 delta = user.getDeltaMovement();

		if(slot == EquipmentSlot.FEET && !user.level().noBlockCollision(user, new AABB(user.getX()-halfWidth, user.getY(), user.getZ()-halfWidth,
				user.getX()+halfWidth, user.getY() - 0.1f, user.getZ()+halfWidth)))
			user.setDeltaMovement(delta.x, Math.min(delta.y, 0.05), delta.z);
	}

	@Override
	public void modifyKnockBackReceived(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingKnockBackEvent event)
	{
		float halfWidth = user.getBbWidth()/2f;

		if(slot == EquipmentSlot.FEET && !user.level().noBlockCollision(user, new AABB(user.getX()-halfWidth, user.getY(), user.getZ()-halfWidth,
				user.getX()+halfWidth, user.getY() - 0.1f, user.getZ()+halfWidth)))
			event.setCanceled(true);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		for (LivingEntity entity : entitiesInBounds) {
			entity.makeStuckInBlock(root.getBlockState(), new Vec3(0.25, 0.05F, 0.25));
		}
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		playRootedParticles(root, random, ParticleTypes.ITEM_COBWEB);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xA8C4A4;
	}
}
