package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.crafting.EssenceExtractionRecipe;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EssenceExtractorBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, RecipeInput, IEssenceHolder {
	protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
	public final EssenceContainer storedEssence = new EssenceContainer(1000);

	public static final int[] SLOTS = new int[] {0};
	private static final RecipeManager.CachedCheck<EssenceExtractorBlockEntity, EssenceExtractionRecipe> RECIPE_CHECK = null;//RecipeManager.createCheck(AlchemancyRecipeTypes.ESSENCE_EXTRACTION.get());

	public EssenceExtractorBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(AlchemancyBlockEntities.ESSENCE_EXTRACTOR.get(), pos, blockState);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, EssenceExtractorBlockEntity blockEntity)
	{
		Optional<RecipeHolder<EssenceExtractionRecipe>> recipeHolder = RECIPE_CHECK.getRecipeFor(blockEntity, level);

		recipeHolder.ifPresent(recipe -> recipe.value().assemble(blockEntity, level.registryAccess()));
	}


	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		return true;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.alchemancy.essence_extractor");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public void fillStackedContents(StackedContents helper) {
		for (ItemStack itemstack : this.items) {
			helper.accountStack(itemstack);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);

		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.put("essence", storedEssence.saveToTag(new CompoundTag()));
	}


	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		ContainerHelper.loadAllItems(tag, this.items, registries);
		storedEssence.loadFromTag(tag.getCompound("essence"));
	}

	@Override
	public int size() {
		return getContainerSize();
	}

	@Override
	public EssenceContainer getEssenceContainer() {
		return storedEssence;
	}
}
