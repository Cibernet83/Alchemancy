package net.cibernet.alchemancy.properties;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.AbstractCauldronAccessor;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TintedProperty extends Property implements IDataHolder<Integer[]>, ITintModifier {
	public static final int DEFAULT_COLOR = FastColor.ARGB32.opaque(0xFFFFFF);
	public static final Integer DEFAULT_COLORS[] = new Integer[0];

	private static final Int2ObjectOpenHashMap<DyeColor> TINT_TO_DYE_MAP = new Int2ObjectOpenHashMap<>(
			Arrays.stream(DyeColor.values()).collect(Collectors.toMap(DyeColor::getTextureDiffuseColor, dye -> dye))
	);

	private static Component getColorName(int color) {
		DyeColor dyecolor = TINT_TO_DYE_MAP.get(color);
		return dyecolor == null ? Component.translatable("property.detail.color", ColorUtils.colorToHexString(color)).withColor(color) :
				Component.translatable("color.minecraft." + dyecolor.getName()).withColor(color);
	}

	@Override
	public boolean cluelessCanReset() {
		return false;
	}

	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, AtomicBoolean consumeItem) {
		Integer[] base = getData(stack);
		Integer[] colors = getDyeColor(propertySource);

		if (colors.length == 0)
			return super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd, consumeItem);

		if (base.length == 0)
			setData(stack, colors);
		else {
			for (int i = 0; i < base.length; i++) {
				base[i] = base[i] == DEFAULT_COLOR ? FastColor.ARGB32.color(255, colors[Math.min(i, colors.length-1)]) : mixColors(base[i], List.of(colors[Math.min(i, colors.length-1)]));
			}
			setData(stack, base);
		}

		return true;
	}

	public Integer[] getDyeColor(ItemStack stack) {
		if (stack.getItem() instanceof DyeItem dyeItem)
			return new Integer[]{FastColor.ARGB32.color(255, dyeItem.getDyeColor().getTextureDiffuseColor())};
		else if (getData(stack).length > 0)
			return getData(stack);
		return DEFAULT_COLORS;
	}

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event) {
		if (!InfusedPropertiesHelper.hasInfusedProperty(event.getItemStack(), asHolder()))
			return;

		BlockState state = event.getLevel().getBlockState(event.getPos());
		if (state.getBlock() instanceof AbstractCauldronBlock cauldron && ((AbstractCauldronAccessor) cauldron).getInteractions().equals(CauldronInteraction.WATER)) {
			if (state.hasProperty(LayeredCauldronBlock.LEVEL))
				LayeredCauldronBlock.lowerFillLevel(state, event.getLevel(), event.getPos());
			setData(event.getItemStack(), getDefaultData());
			InfusedPropertiesHelper.removeProperty(event.getItemStack(), asHolder());
			event.setCancellationResult(ItemInteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint) {
		boolean tintBase = stack.is(AlchemancyTags.Items.TINT_BASE_LAYER);
		boolean dontTintBase = stack.is(AlchemancyTags.Items.DONT_TINT_BASE_LAYER);
		return (tintBase && dontTintBase) || (tintBase && tintIndex > 0) || (dontTintBase && tintIndex == 0) ?
				currentTint : FastColor.ARGB32.color(FastColor.ARGB32.alpha(currentTint), getColor(stack));
	}

	@Override
	public Integer[] readData(CompoundTag tag) {
		return tag.contains("colors", CompoundTag.TAG_INT_ARRAY) ? toIntegerArray(tag.getIntArray("colors")) : new Integer[]{tag.getInt("color")};
	}


	@Override
	public CompoundTag writeData(Integer[] data) {
		return new CompoundTag() {{
			putIntArray("colors", Arrays.stream(data).mapToInt(Integer::valueOf).toArray());
		}};
	}

	public void setData(ItemStack stack, int value) {
		IDataHolder.super.setData(stack, new Integer[]{value});
	}

	@Override
	public Integer[] getDefaultData() {
		return DEFAULT_COLORS;
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(1, Arrays.stream(getData(stack)).mapToInt(Integer::valueOf).toArray());
	}

	private Integer[] toIntegerArray(int... numbers) {
		return Arrays.stream(numbers).boxed().toArray(Integer[]::new);
	}

	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack).copy().withColor(getColor(stack));
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		ArrayList<ItemStack> result = new ArrayList<>();

		for (DyeColor dye : DyeColor.values()) {
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
			float f = (float) l / (float) i1;
			float f1 = (float) Math.max(l2, Math.max(i3, k3));
			l2 = (int) ((float) l2 * f / f1);
			i3 = (int) ((float) i3 * f / f1);
			k3 = (int) ((float) k3 * f / f1);
			return FastColor.ARGB32.color(0, l2, i3, k3);
		}
	}
}
