package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.*;

public class BlockVacuumProperty extends Property implements IDataHolder<Block>
{

	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event)
	{
		Level level = event.getEntity().level();
		Block block = getData(event.getItem());

		if(level.isClientSide() || block.equals(getDefaultData()))
			return;

		int radius = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.EFFECT_RADIUS, 8f).intValue();
		AABB aabb = new AABB(user.blockPosition()).inflate(radius);

		ArrayList<BlockPos> validPos = new ArrayList<>();
		for (BlockPos pos : BlockPos.betweenClosed(user.blockPosition().offset(radius, radius, radius), user.blockPosition().offset(-radius, -radius, -radius))) {
			validPos.add(new BlockPos(pos));
		}

		Collections.shuffle(validPos);

		if(!validPos.isEmpty())
		{
			BlockPos pos = validPos.getFirst();
			for (int i = 5; i > 0 && pos != null; pos = validPos.isEmpty() ? null : validPos.getFirst()) {
				validPos.removeFirst();

				if (level.getBlockState(pos).is(block)) {
					level.destroyBlock(pos, true, event.getEntity());
					i--;
				}
			}
		}

		if(user instanceof Player player)
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, aabb, item -> item.getItem().is(block.asItem()))) {
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
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(getData(event.getItemStack()).equals(getDefaultData()))
		{
			setData(event.getItemStack(), event.getLevel().getBlockState(event.getPos()).getBlock());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

		if(!getData(event.getItemStack()).equals(getDefaultData()))
		{
			event.getEntity().startUsingItem(event.getHand());
			event.setCancellationResult(InteractionResult.CONSUME);
			event.setCanceled(true);
		}
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return List.of();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFFFFFF;
	}

	@Override
	public Block readData(CompoundTag tag)
	{
		return tag.contains("block", CompoundTag.TAG_STRING) ? BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("block"))) : getDefaultData();
	}

	@Override
	public CompoundTag writeData(Block data) {
		return new CompoundTag(){{putString("block", BuiltInRegistries.BLOCK.getKey(data).toString());}};
	}

	@Override
	public Block getDefaultData() {
		return Blocks.AIR;
	}

	@Override
	public Component getName(ItemStack stack)
	{
		Component name = super.getName(stack);
		Block storedBlock = getData(stack);

		if(!storedBlock.equals(getDefaultData()))
			return Component.translatable("property.detail", name, storedBlock.getName()).withColor(getColor(stack));
		return name;
	}

	@Override
	public boolean hasJournalEntry() {
		return false;
	}
}
