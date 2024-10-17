package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ShockUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class ConductiveProperty extends Property
{

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event) {

		if(event.getSource().is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE) && event.getNewDamage() > 1)
			ShockUtils.meleeShockAttack(user, user.position(), event.getOriginalDamage() * 0.5f);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xC26B4C;
	}
}
