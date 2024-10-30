package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.WayfindingUtil;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.UUID;

public class WayfindingProperty extends Property implements IDataHolder<WayfindingProperty.WayfindData>
{
	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(level.isClientSide())
			return;
		WayfindData data = getData(stack);

		if(data.fallbackPos.isEmpty() || !data.fallbackPos.get().dimension().location().equals(level.dimension().location()))
		{
			GlobalPos fallback;

			if((data.targetedPos.isEmpty() || data.targetedPos.get().dimension().location().equals(user.level().dimension().location()))
					&& user instanceof ServerPlayer player && level.dimension().location().equals(player.getRespawnDimension().location()))
			{
				if(player.getRespawnPosition() != null)
					fallback = new GlobalPos(level.dimension(), player.getRespawnPosition());
				else return;
			}
			else if(level.dimensionTypeRegistration().is(AlchemancyTags.Dimensions.WAYFINDING_POINTS_TO_ORIGIN))
				fallback = new GlobalPos(level.dimension(), BlockPos.ZERO);
			else fallback = new GlobalPos(level.dimension(), user.blockPosition());

			setData(stack, data.withFallback(fallback));
		}

	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		WayfindData data = getData(event.getItemStack());
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

		WayfindData data = getData(event.getItemStack());
		if(!data.hasTarget())
		{
			setData(event.getItemStack(), data.withBlockPosition(new GlobalPos(event.getLevel().dimension(), event.getPos())));
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x387CB5;
	}

	@Override
	public WayfindData readData(CompoundTag tag)
	{
		Optional<GlobalPos> targetedPos = Optional.empty();
		Optional<GlobalPos> fallbackPos = Optional.empty();

		if(tag.contains("target_position", CompoundTag.TAG_COMPOUND))
		{
			CompoundTag targetTag = tag.getCompound("target_position");
			targetedPos = Optional.of(
					new GlobalPos(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetTag.getString("dimension"))),
					NbtUtils.readBlockPos(targetTag, "pos").orElse(BlockPos.ZERO)));
		}

		if(tag.contains("fallback_position", CompoundTag.TAG_COMPOUND))
		{
			CompoundTag targetTag = tag.getCompound("fallback_position");
			fallbackPos = Optional.of(
					new GlobalPos(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetTag.getString("dimension"))),
					NbtUtils.readBlockPos(targetTag, "pos").orElse(BlockPos.ZERO)));
		}

		return new WayfindData(
				Optional.ofNullable(tag.hasUUID("target_player") ? new Tuple<>(tag.getUUID("target_player"), tag.contains("target_player_name", Tag.TAG_STRING) ? tag.getString("target_player_name") : "???") : null),
				targetedPos,
				fallbackPos,
				tag.getFloat("previous_rotation")
		);
	}

	@Override
	public Component getName(ItemStack stack)
	{
		WayfindData data = getData(stack);

		if(!data.hasTarget())
			return super.getName(stack);
		Component target = Component.empty();

		if(data.targetedPlayer.isPresent())
		{
			Optional<Player> targetPlayer = CommonUtils.getPlayerByUUID(data.targetedPlayer.get().getA());
			target = Component.literal(targetPlayer.isPresent() ? targetPlayer.get().getGameProfile().getName() : data.targetedPlayer.get().getB());
		}
		else if(data.targetedPos.isPresent())
		{
			GlobalPos pos = data.targetedPos.get();
			if((ServerLifecycleHooks.getCurrentServer() == null || ServerLifecycleHooks.getCurrentServer() instanceof IntegratedServer) && !ClientUtil.getCurrentLevel().dimension().equals(pos.dimension()))
				target = Component.literal(pos.dimension().location().toString());
			else target =  Component.translatable("property.detail.block_position", pos.pos().getX(), pos.pos().getY(), pos.pos().getZ());
		}

		return Component.translatable("property.detail", super.getName(stack), target).withColor(getColor(stack));
	}

	@Override
	public CompoundTag writeData(WayfindData data)
	{
		return new CompoundTag(){{
			data.targetedPlayer.ifPresent(player -> {
				putUUID("target_player", player.getA());
				putString("target_player_name", player.getB());
			});
			if(data.targetedPos.isPresent())
			{
				CompoundTag targetTag = new CompoundTag();
				targetTag.putString("dimension", data.targetedPos.get().dimension().location().toString());
				targetTag.put("pos", NbtUtils.writeBlockPos(data.targetedPos.get().pos()));
				put("target_position", targetTag);
			}
			if(data.fallbackPos.isPresent())
			{
				CompoundTag targetTag = new CompoundTag();
				targetTag.putString("dimension", data.fallbackPos.get().dimension().location().toString());
				targetTag.put("pos", NbtUtils.writeBlockPos(data.fallbackPos.get().pos()));
				put("fallback_position", targetTag);
			}

			putFloat("previous_rotation", data.prevRotation);
		}};
	}


	private static final WayfindData DEFAULT = new WayfindData(Optional.empty(), Optional.empty(), Optional.empty(), -1);
	@Override
	public WayfindData getDefaultData() {
		return DEFAULT;
	}

	public record WayfindData(Optional<Tuple<UUID, String>> targetedPlayer, Optional<GlobalPos> targetedPos, Optional<GlobalPos> fallbackPos, float prevRotation)
	{
		public WayfindData withPlayer(Player targetedPlayer)
		{
			return new WayfindData(Optional.of(new Tuple<>(targetedPlayer.getUUID(), targetedPlayer.getGameProfile().getName())), Optional.empty(), fallbackPos, prevRotation);
		}

		public WayfindData withBlockPosition(GlobalPos targetedPos)
		{
			return new WayfindData(Optional.empty(), Optional.of(targetedPos), fallbackPos, prevRotation);
		}

		public WayfindData withFallback(GlobalPos fallbackPos)
		{
			return new WayfindData(targetedPlayer, targetedPos, Optional.ofNullable(fallbackPos), prevRotation);
		}

		public WayfindData withPreviousRotation(float previousRotation)
		{
			return new WayfindData(targetedPlayer, targetedPos, fallbackPos, previousRotation);
		}

		public float getRotation(Entity user, float partialTicks)
		{
			Level level = user.level();
			Optional<BlockPos> target = getTargetPos(level);

			float result;

			if(target.isPresent())
				result = WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), target.get());
			else if(fallbackPos.isPresent() && fallbackPos.get().dimension().location().equals(user.level().dimension().location()))
				result = WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), fallbackPos.get().pos());
			else {
				GlobalPos worldSpawn = CompassItem.getSpawnPosition(level);
				return worldSpawn != null ?
						WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), worldSpawn.pos()) :
						WayfindingUtil.getRandomlySpinningRotation(0, level.getGameTime());
			}

			return prevRotation <= -1 ? result : Mth.lerp(partialTicks, prevRotation, result);
		}

		public Optional<BlockPos> getTargetPos(Level level)
		{
			if(targetedPlayer.isPresent())
			{
				Player target = level.getPlayerByUUID(targetedPlayer.get().getA());
				if(target != null)
					return Optional.of(target.blockPosition());
			}

			if(targetedPos.isPresent() && targetedPos.get().dimension().location().equals(level.dimension().location()))
				return Optional.of(targetedPos.get().pos());

			return Optional.empty();
		}

		public boolean hasTarget()
		{
			return targetedPos.isPresent() || targetedPlayer.isPresent();
		}
	}
}
