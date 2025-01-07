package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;
import java.util.List;

public class ClayMoldProperty extends Property implements IDataHolder<ItemStack>
{

	public static final InnatePropertyItem.Tooltip ITEM_TOOLTIP = (stack, context, tooltipComponents, tooltipFlag) ->
	{
		ItemStack storedItem = AlchemancyProperties.CLAY_MOLD.value().getData(stack);

		if(!storedItem.isEmpty())
			tooltipComponents.add(Component.translatable("item.alchemancy.unshaped_clay.tooltip", storedItem.getHoverName()).withColor(AlchemancyProperties.CLAY_MOLD.get().getColor(stack)));
	};

	@Override
	public int getColor(ItemStack stack) {
		return 0xAFB9D6;
	}

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(carriedItem.is(AlchemancyTags.Items.REPAIRS_UNSHAPED_CLAY))
		{
			ItemStack storedItem = repair(getData(stackedOnItem));

			stackedOnItem.shrink(1);
			carriedItem.shrink(1);

			if(stackedOnItem.isEmpty())
				event.getSlot().set(storedItem);
			else if(carriedItem.isEmpty())
				event.getCarriedSlotAccess().set(storedItem);
			else if(!player.addItem(storedItem))
				player.drop(storedItem, true);

			playRepairEffects(player);
			event.setCanceled(true);
		}
	}

	private static final BlockParticleOption PARTICLE_OPTIONS = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CLAY.defaultBlockState());

	public static void playRepairEffects(Entity source)
	{
		source.level().playSound(null, source.position().x, source.position().y, source.position().z, AlchemancySoundEvents.CLAY_MOLD, SoundSource.PLAYERS, 1, 0.75f);

		if(source.level() instanceof ServerLevel serverLevel)
			for(int i = 0; i < 20; i++)
			{
				serverLevel.sendParticles(PARTICLE_OPTIONS, source.position().x, source.getEyeY() - 0.2f, source.position().z, 1,
						0, 0, 0, source.getRandom().nextDouble() * 0.25);
			}
	}

	public static ItemStack repair(ItemStack storedItem)
	{
		if(storedItem.isDamageableItem())
			storedItem.setDamageValue(Math.min(storedItem.getDamageValue(), (int) (storedItem.getMaxDamage() * 0.8f)));
		return storedItem;
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return List.of();
	}

	@Override
	public boolean hasJournalEntry() {
		return false;
	}

	@Override
	public ItemStack readData(CompoundTag tag)
	{
		return tag.isEmpty() ? getDefaultData() : ItemStack.parse(CommonUtils.registryAccessStatic(), tag.getCompound("item")).orElse(getDefaultData());
	}

	@Override
	public CompoundTag writeData(ItemStack data) {
		return new CompoundTag() {{
			if(!data.isEmpty())
				put("item", data.save(CommonUtils.registryAccessStatic()));
		}};
	}

	@Override
	public ItemStack getDefaultData()
	{
		return Items.CLAY_BALL.getDefaultInstance();
	}


	@Override
	public Component getDisplayText(ItemStack stack)
	{
		Component name = super.getDisplayText(stack);
		ItemStack storedStack = getData(stack);

		if(!storedStack.isEmpty())
			return Component.translatable("property.detail", name, storedStack.getHoverName()).withColor(getColor(stack));
		return name;
	}

}
