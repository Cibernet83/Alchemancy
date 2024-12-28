package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class HellbentProperty extends Property implements IDataHolder<Holder<Block>>
{
	private static final ResourceLocation SPEED_MOD_KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "hellbent_property_modifier");

	@Override
	public void modifyCriticalAttack(Player user, ItemStack weapon, CriticalHitEvent event)
	{
		event.setCriticalHit(true);
	}

	@Override
	public void modifyBlockDrops(Entity breaker, ItemStack tool, EquipmentSlot slot, List<ItemEntity> drops, BlockDropsEvent event)
	{
		if(breaker instanceof Player player)
		{
			AttributeInstance attribute = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
			if(attribute == null)
				return;


			AttributeModifier mod = attribute.getModifier(SPEED_MOD_KEY);
			double value = Math.min(1.2, (mod == null ? 0 : mod.amount()) + 0.05);
			attribute.removeModifier(SPEED_MOD_KEY);

			Holder<Block> blockString = getData(tool);

			if(event.getState().is(blockString))
				attribute.addPermanentModifier(new AttributeModifier(SPEED_MOD_KEY, value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
			else setData(tool, event.getState().getBlockHolder());
 		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPlayerTick(PlayerTickEvent.Pre event)
	{
		if(event.getEntity().level().isClientSide)
			return;

		Player player = event.getEntity();
		AttributeInstance attribute = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);

		if(attribute != null && attribute.hasModifier(SPEED_MOD_KEY) && !InfusedPropertiesHelper.hasProperty(player.getItemBySlot(EquipmentSlot.MAINHAND), AlchemancyProperties.HELLBENT))
			attribute.removeModifier(SPEED_MOD_KEY);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x723232;
	}

	@Override
	public Holder<Block> readData(CompoundTag tag)
	{
		RegistryAccess registryAccess = CommonUtils.registryAccessStatic();
		Optional<Holder.Reference<Block>> optional = registryAccess.holder(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse(tag.getString("block_id"))));
		return optional.isEmpty() ? getDefaultData() : optional.get();
	}

	@Override
	public CompoundTag writeData(Holder<Block> data)
	{
		return new CompoundTag(){{putString("block_id", data.getKey().location().toString());}};
	}

	@Override
	public Holder<Block> getDefaultData() {
		return Blocks.AIR.builtInRegistryHolder();
	}
}
