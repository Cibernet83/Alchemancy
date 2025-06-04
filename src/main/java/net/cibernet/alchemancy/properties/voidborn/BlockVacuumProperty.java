package net.cibernet.alchemancy.properties.voidborn;

import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockVacuumProperty extends Property implements IDataHolder<BlockVacuumProperty.Data> {

	public static final DustColorTransitionOptions PARTICLES = new DustColorTransitionOptions(
			Vec3.fromRGB24(0xEC8BF8).toVector3f(), Vec3.fromRGB24(0).toVector3f(), .75F
	);


	public static final InnatePropertyItem.Tooltip TOOL_TOOLTIP = (stack, context, tooltipComponents, tooltipFlag) ->
	{
		Data data = AlchemancyProperties.WORLD_OBLITERATOR.value().getData(stack);

		if (data.filterBlock() != null)
			tooltipComponents.add(Component.translatable("item.alchemancy.black_hole_tool.bound_to_block", data.filterBlock().getName()).withColor(AlchemancyProperties.WORLD_OBLITERATOR.value().getColor(stack)));
	};

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {

		if (clickAction != ClickAction.SECONDARY) return;

		var data = getData(stack);
		if (carriedItem.getItem() instanceof BlockItem blockItem && (data.tag() == null || blockItem.getBlock().defaultBlockState().is(data.tag()))) {
			setData(stack, new Data(data.tag(), blockItem.getBlock()));
			isCancelled.set(true);
		} else if (carriedItem.isEmpty()) {
			setData(stack, new Data(data.tag()));
			isCancelled.set(true);
		}
	}

	@Override
	public boolean cluelessCanReset() {
		return false;
	}

	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event) {
		Level level = event.getEntity().level();
		var data = getData(event.getItem());

		if (level.isClientSide())
			return;

		int radius = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.EFFECT_RADIUS, 5f).intValue();
		AABB aabb = new AABB(user.blockPosition()).inflate(radius);

		ArrayList<BlockPos> validPos = new ArrayList<>();
		for (BlockPos pos : BlockPos.betweenClosed(user.blockPosition().offset(radius, radius, radius), user.blockPosition().offset(-radius, -radius, -radius))) {
			if (user.blockPosition().distSqr(pos) <= radius * radius)
				validPos.add(new BlockPos(pos));
		}

		Collections.shuffle(validPos);

		if (!validPos.isEmpty()) {
			BlockPos pos = validPos.getFirst();
			int durabilityConsumed = 0;
			int durabilityConsumedPerBlock = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 1);

			for (int i = 5; i > 0 && pos != null; pos = validPos.isEmpty() ? null : validPos.getFirst()) {
				validPos.removeFirst();

				BlockState state = level.getBlockState(pos);
				if (state.canEntityDestroy(level, pos, user) && state.getBlock().defaultDestroyTime() >= 0 && data.matches(state)) {
					level.destroyBlock(pos, false, event.getEntity());

					ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, state.getBlock().asItem().getDefaultInstance(), 0, 0, 0);
					itemEntity.setDefaultPickUpDelay();
					level.addFreshEntity(itemEntity);
					((ServerLevel) level).sendParticles(PARTICLES, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 5, 0.5, 0.5, 0.5, 0);

					i--;
					durabilityConsumed += durabilityConsumedPerBlock;
				}
			}

			if (durabilityConsumed > 0)
				stack.hurtAndBreak(durabilityConsumed, user, EquipmentSlot.MAINHAND);
		}

		if (user instanceof Player player)
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, aabb, item -> item.getItem().getItem() instanceof BlockItem blockItem && data.matches(blockItem.getBlock().defaultBlockState()))) {
				item.playerTouch(player);
			}
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return 72000;
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return Optional.of(UseAnim.BOW);
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

		event.getEntity().startUsingItem(event.getHand());
		event.setCancellationResult(InteractionResult.CONSUME);
		event.setCanceled(true);
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return List.of();
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(4, 0xEC8BF8, 0x4F3B92);
	}

	@Override
	public BlockVacuumProperty.Data readData(CompoundTag tag) {
		return new Data(tag);
	}

	@Override
	public CompoundTag writeData(BlockVacuumProperty.Data data) {
		return data.save();
	}

	@Override
	public BlockVacuumProperty.Data getDefaultData() {
		return Data.DEFAULT;
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		Component name = super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
		var data = getData(stack);

		if (data.filterBlock() != null)
			return Component.translatable("property.detail", name, data.filterBlock().getName()).withColor(getColor(stack));
		else if (data.tag() != null)
			return Component.translatable("property.detail", name, "#%s".formatted(data.tag().toString())).withColor(getColor(stack));
		else return name;
	}

	@Override
	public boolean hasJournalEntry() {
		return false;
	}

	public record Data(@Nullable TagKey<Block> tag, @Nullable Block filterBlock) {

		public static final Data DEFAULT = new Data(null, null);

		public Data(@Nullable TagKey<Block> tag) {
			this(tag, null);
		}

		public Data(CompoundTag nbt) {
			this(nbt.contains("tag", Tag.TAG_STRING) ? TagKey.create(Registries.BLOCK, ResourceLocation.parse(nbt.getString("tag"))) : null,
					nbt.contains("block", Tag.TAG_STRING) ? BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("block"))) : null);
		}

		public CompoundTag save() {
			return new CompoundTag() {{
				if (tag != null)
					putString("tag", tag.location().toString());
				if (filterBlock != null)
					putString("block", BuiltInRegistries.BLOCK.getKey(filterBlock).toString());
			}};
		}

		public boolean matches(BlockState blockState) {
			if (filterBlock() != null)
				return blockState.is(filterBlock());
			if (tag() != null)
				return blockState.is(tag());
			return true;
		}
	}
}
