package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class LetsGoGamblingProperty extends Property
{
	static ResourceKey<DamageType> DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "gambling"));

	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(!event.getSource().isDirect() && event.getSource().getEntity() instanceof LivingEntity indirectUser)
			user = indirectUser;

		switch (user.getRandom().nextInt(3))
		{
			case 0 ->
			{
				user.hurt(user.damageSources().source(DAMAGE_KEY), event.getAmount());
				event.setCanceled(true);
			}
			case 1 ->
			{

			}
			case 2 ->
			{
				event.setAmount(event.getAmount() * 2f);
			}
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x972A27;
	}
}
