package net.cibernet.alchemancy.item;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DreamsteelBowItem extends BowItem
{
	public DreamsteelBowItem(Properties properties) {
		super(properties);
	}

	@Override
	protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit)
	{
		InfusedPropertiesHelper.addProperties(ammo, InfusedPropertiesHelper.getInfusedProperties(weapon));
		ammo = ForgeRecipeGrid.resolveInteractions(ammo, level);



		return super.createProjectile(level, shooter, weapon, ammo, isCrit);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return super.use(level, player, hand);
	}
}
