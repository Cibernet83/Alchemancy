package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

public class RelentlessProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0x004CFF;
	}

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random)
	{
		return user != null && random.nextFloat() <= 1 / Math.max(0.6f, getEffectScale(user) * 0.6f) ? 0 : resultingAmount;
	}

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		float newDamage = Mth.ceil(
				Math.max(event.getOriginalDamage() * 0.5f,
				event.getNewDamage() * (1 - getEffectScale(user) * 0.2f)));

		if(event.getNewDamage() > newDamage)
			event.setNewDamage(newDamage);
	}

	public static float getEffectScale(LivingEntity user)
	{
		return 1 - (user.getHealth() / user.getMaxHealth());
	}
}
