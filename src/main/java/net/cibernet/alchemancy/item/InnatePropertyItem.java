package net.cibernet.alchemancy.item;

import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InnatePropertyItem extends Item
{
	public final int useTime;
	public final UseAnim useAnim;

	public static final ArrayList<Item> TOGGLEABLE_ITEMS = new ArrayList<>();

	@SafeVarargs
	public InnatePropertyItem(Properties properties, int useTime, UseAnim useAnim, Holder<Property>... innateProperties)
	{
		super(properties.component(AlchemancyItems.Components.INNATE_PROPERTIES, new InfusedPropertiesComponent(List.of(innateProperties))));
		this.useTime = useTime;
		this.useAnim = useAnim;

		if(Arrays.asList(innateProperties).contains(AlchemancyProperties.TOGGLEABLE))
			TOGGLEABLE_ITEMS.add(this);
	}

	public InnatePropertyItem(Properties properties, Holder<Property>... innateProperties)
	{
		this(properties, 0, UseAnim.NONE, innateProperties);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return useAnim;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return useTime;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(useTime > 0)
		{
			ItemStack itemstack = player.getItemInHand(hand);
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(itemstack);
		}
		else return super.use(level, player, hand);
	}
}
