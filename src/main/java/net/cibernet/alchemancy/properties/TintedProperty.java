package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.AbstractCauldronAccessor;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TintedProperty extends IncreaseInfuseSlotsProperty implements IDataHolder<Integer>, ITintModifier
{
	public static final int DEFAULT_COLOR = FastColor.ARGB32.opaque(0xFFFFFF);

	public TintedProperty()
	{
		super(1);
	}

	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid)
	{
		int base = getData(stack);
		int color = -1;

		if(propertySource.getItem() instanceof DyeItem dyeItem)
			color = dyeItem.getDyeColor().getTextureDiffuseColor();
		else if(!getData(propertySource).equals(getDefaultData()))
			color = getData(propertySource);

		if(color == -1)
			return super.onInfusedByDormantProperty(stack, propertySource, grid);

		setData(stack, base == getDefaultData() ? FastColor.ARGB32.color(255, color) :  mixColors(base, List.of(color)));
		return true;
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(!InfusedPropertiesHelper.hasInfusedProperty(event.getItemStack(), asHolder()))
			return;

		BlockState state = event.getLevel().getBlockState(event.getPos());
		if(state.getBlock() instanceof AbstractCauldronBlock cauldron && ((AbstractCauldronAccessor)cauldron).getInteractions().equals(CauldronInteraction.WATER))
		{
			if(state.hasProperty(LayeredCauldronBlock.LEVEL))
				LayeredCauldronBlock.lowerFillLevel(state, event.getLevel(), event.getPos());
			setData(event.getItemStack(), getDefaultData());
			InfusedPropertiesHelper.removeProperty(event.getItemStack(), asHolder());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint)
	{
		boolean tintBase = stack.is(AlchemancyTags.Items.TINT_BASE_LAYER);
		boolean dontTintBase = stack.is(AlchemancyTags.Items.DONT_TINT_BASE_LAYER);
		return (tintBase && dontTintBase) || (tintBase && tintIndex > 0) || (dontTintBase && tintIndex == 0) ?
						currentTint : FastColor.ARGB32.color(FastColor.ARGB32.alpha(currentTint), getData(stack));
	}

	@Override
	public Integer readData(CompoundTag tag) {
		return tag.getInt("color");
	}

	@Override
	public CompoundTag writeData(Integer data) {
		return new CompoundTag(){{putInt("color", data);}};
	}

	@Override
	public Integer getDefaultData() {
		return DEFAULT_COLOR;
	}

	@Override
	public int getColor(ItemStack stack) {
		return getData(stack);
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder)
	{
		ArrayList<ItemStack> result = new ArrayList<>();

		for(DyeColor dye : DyeColor.values())
		{
			ItemStack stack = capsuleItem.toStack();
			stack.set(AlchemancyItems.Components.STORED_PROPERTIES, new InfusedPropertiesComponent(List.of(holder)));
			setData(stack, dye.getTextureDiffuseColor());
			result.add(stack);
		}

		return result;
	}

	public static int mixColors(int base, List<Integer> dyes) {
		{
			int i = 0;
			int j = 0;
			int k = 0;
			int l = 0;
			int i1 = 0;
			{
				int j1 = FastColor.ARGB32.red(base);
				int k1 = FastColor.ARGB32.green(base);
				int l1 = FastColor.ARGB32.blue(base);
				l += Math.max(j1, Math.max(k1, l1));
				i += j1;
				j += k1;
				k += l1;
				i1++;
			}

			for (int dyeitem : dyes) {
				int i2 = FastColor.ARGB32.red(dyeitem);
				int j2 = FastColor.ARGB32.green(dyeitem);
				int k2 = FastColor.ARGB32.blue(dyeitem);
				l += Math.max(i2, Math.max(j2, k2));
				i += i2;
				j += j2;
				k += k2;
				i1++;
			}

			int l2 = i / i1;
			int i3 = j / i1;
			int k3 = k / i1;
			float f = (float)l / (float)i1;
			float f1 = (float)Math.max(l2, Math.max(i3, k3));
			l2 = (int)((float)l2 * f / f1);
			i3 = (int)((float)i3 * f / f1);
			k3 = (int)((float)k3 * f / f1);
			return FastColor.ARGB32.color(0, l2, i3, k3);
		}
	}
}
