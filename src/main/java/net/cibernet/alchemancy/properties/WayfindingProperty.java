package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.WayfindingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber
public class WayfindingProperty extends Property implements IDataHolder<Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData>>
{
	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd)
	{
		if(super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd))
		{
			if(propertySource.has(DataComponents.LODESTONE_TRACKER))
			{
				LodestoneTracker tracker = propertySource.get(DataComponents.LODESTONE_TRACKER);
				if(tracker.target().isPresent())
					setData(stack, getDefaultData().getA().withBlockPosition(tracker.target().get()));
			}

			return true;
		}

		return false;
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(level.isClientSide())
			return;
		WayfindData data = getData(stack).getA();

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
		WayfindData data = getData(event.getItemStack()).getA();
		if(!data.hasTarget() && event.getTarget() instanceof Player target)
		{
			setData(event.getItemStack(), data.withPlayer(target));
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event)
	{
		if(!event.getLevel().getBlockState(event.getPos()).is(AlchemancyTags.Blocks.WAYFINDING_TARGETABLE))
			return;

		WayfindData data = getData(event.getItemStack()).getA();
		if(!data.hasTarget())
		{
			setData(event.getItemStack(), data.withBlockPosition(new GlobalPos(event.getLevel().dimension(), event.getPos())));
			event.setCancellationResult(ItemInteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerSetSpawn(PlayerSetSpawnEvent event)
	{
		Inventory inventory = event.getEntity().getInventory();

		if(event.getNewSpawn() != null)
			for(int slot = 0; slot < inventory.getContainerSize(); slot++)
			{
				ItemStack stack = inventory.getItem(slot);
				final int currentSlot = slot;
				if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.WAYFINDING))
				{
					WayfindData data = AlchemancyProperties.WAYFINDING.get().getData(stack).getA();

					if(data.fallbackPos().isPresent() && data.fallbackPos().get().dimension().equals(event.getSpawnLevel()))
						AlchemancyProperties.WAYFINDING.get().setData(stack, data.withFallback(new GlobalPos(event.getSpawnLevel(), event.getNewSpawn())));
				}
			}
	}

	@Override
	public Component getDisplayText(ItemStack stack)
	{
		WayfindData data = getData(stack).getA();

		if(!data.hasTarget())
			return super.getDisplayText(stack);
		Component target = Component.empty();

		if(data.targetedPlayer.isPresent())
		{
			Optional<Player> targetPlayer = CommonUtils.getPlayerByUUID(data.targetedPlayer.get().getA());
			target = Component.literal(targetPlayer.isPresent() ? targetPlayer.get().getGameProfile().getName() : data.targetedPlayer.get().getB());
		}
		else if(data.targetedPos.isPresent())
		{
			GlobalPos pos = data.targetedPos.get();
			if((ServerLifecycleHooks.getCurrentServer() == null || !(ServerLifecycleHooks.getCurrentServer() instanceof DedicatedServer)) && !ClientUtil.getCurrentLevel().dimension().equals(pos.dimension()))
				target = Component.literal(pos.dimension().location().toString());
			else target =  Component.translatable("property.detail.block_position", pos.pos().getX(), pos.pos().getY(), pos.pos().getZ());
		}

		return Component.translatable("property.detail", super.getDisplayText(stack), target).withColor(getColor(stack));
	}

	public void setData(ItemStack item, WayfindData value)
	{
		IDataHolder.super.setData(item, new Tuple<>(value, ServerLifecycleHooks.getCurrentServer() == null || !(ServerLifecycleHooks.getCurrentServer() instanceof DedicatedServer) ?
				getData(item).getB() : RotationData.DEFAULT));
	}

	public void setData(ItemStack item, RotationData value)
	{
		IDataHolder.super.setData(item, new Tuple<>(getData(item).getA(), value));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x387CB5;
	}

	@Override
	public Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> readData(CompoundTag tag)
	{
		return new Tuple<>(WayfindData.fromNbt(tag), RotationData.fromNbt(tag.getCompound("rotation_data")));
	}

	@Override
	public CompoundTag writeData(Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> data)
	{
		CompoundTag tag = data.getA().toNbt();
		tag.put("rotation_data", data.getB().toNbt());
		return tag;
	}

	private static final Tuple<WayfindData, RotationData> DEFAULT = new Tuple<>(WayfindData.DEFAULT, RotationData.DEFAULT);
	@Override
	public Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> getDefaultData() {
		return DEFAULT;
	}

	public record RotationData(float rotation, float previousRotaion, long lastUpdateTick)
	{
		public static final RotationData DEFAULT = new RotationData(0, 0, 0);

		public RotationData step(float rotation, long currentTick)
		{
			return new RotationData(rotation, this.rotation, currentTick);
		}

		public static RotationData fromNbt(CompoundTag tag)
		{
			return new RotationData(tag.getFloat("rotation"), tag.getFloat("previous_rotation"), tag.getLong("last_update_tick"));
		}

		public CompoundTag toNbt()
		{
			return new CompoundTag(){{
				putFloat("rotation", rotation);
				putFloat("previous_rotation", previousRotaion);
				putLong("last_update_tick", lastUpdateTick);
			}};
		}

		public boolean shouldUpdate(long gameTime) {
			return lastUpdateTick != gameTime;
		}
	}

	public record WayfindData(Optional<Tuple<UUID, String>> targetedPlayer, Optional<GlobalPos> targetedPos, Optional<GlobalPos> fallbackPos)
	{
		public static final WayfindData DEFAULT = new WayfindData(Optional.empty(), Optional.empty(), Optional.empty());

		public static WayfindData fromNbt(CompoundTag tag) {
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
					fallbackPos
			);
		}

		public WayfindData withPlayer(Player targetedPlayer)
		{
			return new WayfindData(Optional.of(new Tuple<>(targetedPlayer.getUUID(), targetedPlayer.getGameProfile().getName())), Optional.empty(), fallbackPos);
		}

		public WayfindData withBlockPosition(GlobalPos targetedPos)
		{
			return new WayfindData(Optional.empty(), Optional.of(targetedPos), fallbackPos);
		}

		public WayfindData withFallback(GlobalPos fallbackPos)
		{
			return new WayfindData(targetedPlayer, targetedPos, Optional.ofNullable(fallbackPos));
		}

		public float getRotation(Entity user)
		{
			Level level = user.level();
			Optional<BlockPos> target = getTargetPos(level);

			float result;

			if(targetedPlayer.isPresent() && user instanceof Player player && player.getUUID().equals(targetedPlayer.get().getA()))
				result = WayfindingUtil.getRandomlySpinningRotation(0, level.getGameTime());
			else if(target.isPresent())
				result = WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), target.get());
			else if(fallbackPos.isPresent() && fallbackPos.get().dimension().location().equals(user.level().dimension().location()))
				result = WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), fallbackPos.get().pos());
			else {
				GlobalPos worldSpawn = CompassItem.getSpawnPosition(level);
				return worldSpawn != null ?
						WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), worldSpawn.pos()) :
						WayfindingUtil.getRandomlySpinningRotation(0, level.getGameTime());
			}

			return result;
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

		public CompoundTag toNbt() {
			return new CompoundTag(){{
				targetedPlayer.ifPresent(player -> {
					putUUID("target_player", player.getA());
					putString("target_player_name", player.getB());
				});
				if(targetedPos.isPresent())
				{
					CompoundTag targetTag = new CompoundTag();
					targetTag.putString("dimension", targetedPos.get().dimension().location().toString());
					targetTag.put("pos", NbtUtils.writeBlockPos(targetedPos.get().pos()));
					put("target_position", targetTag);
				}
				if(fallbackPos.isPresent())
				{
					CompoundTag targetTag = new CompoundTag();
					targetTag.putString("dimension", fallbackPos.get().dimension().location().toString());
					targetTag.put("pos", NbtUtils.writeBlockPos(fallbackPos.get().pos()));
					put("fallback_position", targetTag);
				}
			}};
		}

		public Optional<ResourceKey<Level>> getTargetDimension(Level level) {

			if(targetedPlayer.isPresent())
			{
				Player target = level.getPlayerByUUID(targetedPlayer.get().getA());
				if(target != null)
					return Optional.of(target.level().dimension());
			}

			if(targetedPos.isPresent() && targetedPos.get().dimension().location().equals(level.dimension().location()))
				return Optional.of(targetedPos.get().dimension());

			return Optional.empty();
		}
	}
}
