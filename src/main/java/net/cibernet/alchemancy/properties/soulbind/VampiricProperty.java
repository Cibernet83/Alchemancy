package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class VampiricProperty extends Property
{
	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event)
	{
		if(user instanceof LivingEntity living)
			living.heal(Mth.clamp(event.getNewDamage() * 0.2f, 1, event.getNewDamage()));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		Level level = user.level();
		if(!level.isClientSide() && level.canSeeSky(user.blockPosition()) && level.isDay() && !level.isRaining() && user.getRandom().nextFloat() < 0.02f)
		{
			if(user.getRandom().nextFloat() < 0.1f)
				user.setRemainingFireTicks(user.getRemainingFireTicks() + 80);
			else if(stack.isDamageableItem())
				stack.hurtAndBreak(1, user, slot);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xC41B26;
	}
}
