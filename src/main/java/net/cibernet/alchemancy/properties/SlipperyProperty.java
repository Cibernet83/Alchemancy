package net.cibernet.alchemancy.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public class SlipperyProperty extends Property
{
	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		if(user instanceof Player player)
		{
			player.drop(weapon, true);
			player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		}
		else if (user instanceof LivingEntity entity)
		{
			HollowProperty.nonPlayerDrop(user, weapon, false, true);
			entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		}
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		event.getEntity().drop(event.getItemStack(), true);
		event.getEntity().setItemInHand(event.getHand(), ItemStack.EMPTY);
	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		event.getEntity().drop(event.getItemStack(), true);
		event.getEntity().setItemInHand(event.getHand(), ItemStack.EMPTY);
	}

	@Override
	public void onFall(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingFallEvent event)
	{
		if(slot == EquipmentSlot.FEET)
		{
			BlockState state = entity.getBlockStateOn();
			if(state.getBlock() instanceof StairBlock)
			{
				Vec3i dir = state.getValue(StairBlock.FACING).getOpposite().getNormal();
				Vec3 delta = entity.getDeltaMovement();
				entity.setDeltaMovement(delta.add(new Vec3(dir.getX(), dir.getY(), dir.getZ()).scale(.7f)));
			}
		}
	}

	@Override
	public float modifyStepOnFriction(Entity user, ItemStack stack, float originalResult, float result)
	{
		return Math.min(result + .38f, Math.max(result, 0.999998f));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xC5F9F2;
	}

	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack).copy().withStyle(ChatFormatting.ITALIC);
	}
}
