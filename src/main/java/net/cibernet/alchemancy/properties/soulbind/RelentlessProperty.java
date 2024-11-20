package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class RelentlessProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0x004CFF;
	}

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{
		return user.getRandom().nextFloat() <= 1 / (getEffectScale(user) * 0.8f) ? 0 : resultingAmount;
	}

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		float newDamage = Mth.ceil(
				Math.max(event.getOriginalDamage() * 0.5f,
				event.getNewDamage() * (1 - getEffectScale(user) * 0.02f)));

		if(event.getNewDamage() > newDamage)
			event.setNewDamage(newDamage);
	}

	public static float getEffectScale(LivingEntity user)
	{
		return 1 - (user.getHealth() / user.getMaxHealth());
	}
}
