package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.properties.EnderProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.WayfindingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class WaywardWarpProperty extends Property implements IDataHolder<WayfindingProperty.WayfindData>
{
	public static final Component DIMENSION_MISSMATCH = Component.translatable("property.alchemancy.wayward_warp.dimension_missmatch");
	public static final Component MISSING_DESTINATION = Component.translatable("property.alchemancy.wayward_warp.missing_destination");
	public static final Component OBSTRUCTED_DESTINATION = Component.translatable("property.alchemancy.wayward_warp.obstructed_destination");

	public static final InnatePropertyItem.Tooltip MEDALLION_TOOLTIP = (stack, context, tooltipComponents, tooltipFlag) ->
	{
		WayfindingProperty.WayfindData data = AlchemancyProperties.WAYWARD_WARP.value().getData(stack);

		if(data.hasTarget())
		{
			int color = AlchemancyProperties.WAYWARD_WARP.value().getColor(stack);

			if(data.targetedPlayer().isPresent())
			{
				Optional<Player> targetPlayer = CommonUtils.getPlayerByUUID(data.targetedPlayer().get().getA());
				tooltipComponents.add(Component.translatable("item.alchemancy.wayward_medallion.bound_to_player", targetPlayer.isPresent() ? targetPlayer.get().getGameProfile().getName() : data.targetedPlayer().get().getB()).withColor(color));
			}
			else if(data.targetedPos().isPresent())
			{
				GlobalPos pos = data.targetedPos().get();
				tooltipComponents.add(Component.translatable("item.alchemancy.wayward_medallion.bound_to_position", pos.pos().getX(), pos.pos().getY(), pos.pos().getZ()).withColor(color));
				tooltipComponents.add(Component.translatable("item.alchemancy.wayward_medallion.in", pos.dimension().location().toString()).withColor(color));
			}

		}
	};

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		WayfindingProperty.WayfindData data = getData(event.getItemStack());
		if(!data.hasTarget() && event.getTarget() instanceof Player target)
		{
			setData(event.getItemStack(), data.withPlayer(target));
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(!event.getLevel().getBlockState(event.getPos()).is(AlchemancyTags.Blocks.WAYFINDING_TARGETABLE))
			return;

		WayfindingProperty.WayfindData data = getData(event.getItemStack());
		if(!data.hasTarget())
		{
			setData(event.getItemStack(), data.withBlockPosition(new GlobalPos(event.getLevel().dimension(), event.getPos())));
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(getData(event.getItemStack()).hasTarget())
		{
			event.getEntity().startUsingItem(event.getHand());
			event.setCancellationResult(InteractionResult.CONSUME);
			event.setCanceled(true);
		}
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result)
	{
		return Math.max(32, result);
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current)
	{
		return current.isEmpty() && original == UseAnim.NONE ? Optional.of(UseAnim.BOW) : current;
	}

	@Override
	public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack)
	{
		return teleport(user, level, stack);
	}

	public boolean teleport(LivingEntity user, Level level, ItemStack stack)
	{
		if(level.isClientSide())
			return false;

		WayfindingProperty.WayfindData data = getData(stack);

		if(!data.hasTarget())
			return false;

		Optional<ResourceKey<Level>> targetDimension = data.getTargetDimension(level);
		if(targetDimension.isPresent() && !targetDimension.get().location().equals(user.level().dimension().location()))
		{
			if (user instanceof Player player) {
				player.displayClientMessage(DIMENSION_MISSMATCH, true);
			}
			return false;
		}

		Optional<BlockPos> targetPos = data.getTargetPos(level);

		if(targetPos.isEmpty() || (data.targetedPos().isPresent() && !level.getBlockState(targetPos.get()).is(AlchemancyTags.Blocks.WAYFINDING_TARGETABLE)))
		{
			if (user instanceof Player player) {
				player.displayClientMessage(MISSING_DESTINATION, true);
			}
			return false;
		}

		Optional<Vec3> destination = RespawnAnchorBlock.findStandUpPosition(user.getType(), user.level(), targetPos.get());

		if(destination.isPresent())
		{
			user.teleportTo(destination.get().x, destination.get().y, destination.get().z);
			EnderProperty.playSound(level, destination.get());
			EnderProperty.playParticles(level, destination.get(), user.getRandom());

			EquipmentSlot slot = user.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
			if(stack.isDamageableItem())
				stack.hurtAndBreak(10, user, slot);
			else consumeItem(user, stack, slot);
			return true;
		}
		else if(user instanceof Player player)
		{
			player.displayClientMessage(OBSTRUCTED_DESTINATION, true);
		}

		return false;
	}

	@Override
	public int getColor(ItemStack stack) {

		return ColorUtils.interpolateColorsOverTime(2f, 0xFF387CB5, 0xFFCC00FA);
	}

	@Override
	public WayfindingProperty.WayfindData readData(CompoundTag tag) {
		return WayfindingProperty.WayfindData.fromNbt(tag);
	}

	@Override
	public CompoundTag writeData(WayfindingProperty.WayfindData data) {
		return data.toNbt();
	}

	@Override
	public WayfindingProperty.WayfindData getDefaultData() {
		return WayfindingProperty.WayfindData.DEFAULT;
	}

	@Override
	public boolean hasJournalEntry() {
		return false;
	}
}
