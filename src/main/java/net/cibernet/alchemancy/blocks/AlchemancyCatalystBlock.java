package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.blocks.blockentities.AlchemancyCatalystBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.network.S2CDiscoverCodexIngredientsPacket;
import net.cibernet.alchemancy.network.S2CUnlockCodexEntriesPacket;
import net.cibernet.alchemancy.registries.*;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlchemancyCatalystBlock extends TransparentBlock implements EntityBlock
{
	private static RecipeManager.CachedCheck<ForgeRecipeGrid, AbstractForgeRecipe<?>> RECIPE_CHECK;
	private static MapCodec<AlchemancyCatalystBlock> CODEC = simpleCodec(AlchemancyCatalystBlock::new);

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public AlchemancyCatalystBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		if(!level.isClientSide())
			performRecipe(player, level, pos);
		return InteractionResult.SUCCESS_NO_ITEM_USED;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(stack.getItem() instanceof DyeItem dye && level.getBlockEntity(pos) instanceof AlchemancyCatalystBlockEntity catalyst)
		{
			int[] tint = getTint(stack);

			if(!catalyst.getCrystalTexture().equals(dye.getDyeColor().getName()) ||
					catalyst.getTint() != CommonUtils.getPropertyDrivenTint(stack) ||
					InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.MUFFLED) != catalyst.silent)
			{
				catalyst.setCrystalTexture(dye.getDyeColor());
				catalyst.setTint(tint);
				catalyst.silent = InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.MUFFLED);

				stack.consume(1, player);
				return ItemInteractionResult.SUCCESS;
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	private int[] getTint(ItemStack stack) {

		int alpha = FastColor.ARGB32.alpha(CommonUtils.getPropertyDrivenTint(stack));
		int[] tintedColors = Arrays.stream(AlchemancyProperties.TINTED.get().getData(stack)).mapToInt(c -> FastColor.ARGB32.color(alpha, c)).toArray();
		return tintedColors.length == 0 ? new int[] {FastColor.ARGB32.color(alpha, 0xFFFFFF)} : tintedColors;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	{
		boolean neighborPowered = level.hasNeighborSignal(pos);
		boolean powered = state.getValue(POWERED);
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (neighborPowered && !powered)
		{
			level.scheduleTick(pos, this, 4);
			level.setBlock(pos, state.setValue(POWERED, Boolean.TRUE), 2);

			performRecipe(null, level, pos);
		} else if (!neighborPowered && powered) {
			level.setBlock(pos, state.setValue(POWERED, Boolean.FALSE), 2);
		}
	}

	public static void performRecipe(@Nullable Player player,  Level level, BlockPos catalystPos)
	{
		if(RECIPE_CHECK == null)
			RECIPE_CHECK = createCheck(AlchemancyRecipeTypes.ALCHEMANCY_FORGE.get());

		BlockPos forgePos = catalystPos.below(2);

		if(level.getBlockState(forgePos).is(AlchemancyBlocks.ALCHEMANCY_FORGE) && level.getBlockEntity(forgePos) instanceof ItemStackHolderBlockEntity forge)
		{
			ForgeRecipeGrid grid = new ForgeRecipeGrid(level, forgePos, forge);
			var itemsToDiscover = grid.getItemPedestals().stream().filter(pedestal -> !pedestal.isEmpty()).map(pedestal -> BuiltInRegistries.ITEM.getKey(pedestal.getItem().getItem())).toList();


			AtomicBoolean loop = new AtomicBoolean(true);
			for(int i = 0; i < 128 && loop.get() && !grid.isPerformingTransmutation(); i++)
			{
				RECIPE_CHECK.getRecipeFor(grid, level).ifPresentOrElse((recipe) ->
				{
					if(player instanceof ServerPlayer serverPlayer)
						AlchemancyCriteriaTriggers.PERFORM_FORGE_RECIPE.get().trigger(serverPlayer, recipe, grid);
					grid.processRecipe(recipe.value(), level.registryAccess());
				}, () ->
						loop.set(false));
			}

			ItemStack output = grid.getCurrentOutput();


			if(player instanceof ServerPlayer serverPlayer)
			{
				AlchemancyCriteriaTriggers.DISCOVER_PROPERTY.get().trigger(serverPlayer, output);
				PacketDistributor.sendToPlayer(serverPlayer, new S2CUnlockCodexEntriesPacket(output));
				if(!itemsToDiscover.isEmpty())
					PacketDistributor.sendToPlayer(serverPlayer, new S2CDiscoverCodexIngredientsPacket(itemsToDiscover));
			}

			if(grid.shouldConsumeWarped())
				InfusedPropertiesHelper.removeProperty(output, AlchemancyProperties.WARPED);

			forge.removeItem(1);
			InfusedPropertiesHelper.truncateProperties(output);

			grid.applyGlint.ifPresent(aBoolean -> output.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, aBoolean));

			ItemStackHolderBlockEntity.dropItem(level, forgePos, output);

			if(level.getBlockEntity(catalystPos) instanceof AlchemancyCatalystBlockEntity catalyst)
				catalyst.playAnimation(false);
		}
	}



	public static RecipeManager.CachedCheck<ForgeRecipeGrid, AbstractForgeRecipe<?>> createCheck(final RecipeType<AbstractForgeRecipe<?>> recipeType)
	{
		return (input, level) -> level.getRecipeManager().getRecipesFor(recipeType, input, level).stream().filter(recipe -> recipe.value().matches(input, level))
				.min(Comparator.comparingInt(recipe -> recipe.value().getRecipeCompareValue(input)));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AlchemancyCatalystBlockEntity(pos, state);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if(level.getBlockEntity(pos) instanceof AlchemancyCatalystBlockEntity catalyst)
			catalyst.randomizeSpinOffset(level.random);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
	{
		return (BlockEntityTicker<T>) createTicker(level, blockEntityType);
	}

	@javax.annotation.Nullable
	protected static <T extends AlchemancyCatalystBlockEntity> BlockEntityTicker<AlchemancyCatalystBlockEntity> createTicker(
			Level level, BlockEntityType<? extends BlockEntity> serverType) {
		return serverType != AlchemancyBlockEntities.ALCHEMANCY_CATALYST.get() ? null : level.isClientSide ? AlchemancyCatalystBlockEntity::clientTick : AlchemancyCatalystBlockEntity::serverTick;
	}
}
