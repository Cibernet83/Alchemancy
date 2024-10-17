package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class RandomEffectProperty extends Property implements IDataHolder<Long>
{
	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event) {
		getRandomEffect(weapon).value().modifyAttackDamage(user, weapon, event);
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
		getRandomEffect(weapon).value().onAttack(user, weapon, damageSource, target);
	}

	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event) {
		getRandomEffect(weapon).value().onIncomingAttack(user, weapon, target, event);
	}

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event) {
		getRandomEffect(weapon).value().modifyDamageReceived(user, weapon, slot, event);
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
		getRandomEffect(stack).value().onActivation(source, target, stack);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		return getRandomEffect(stack).value().getColor(stack);
	}

	public Holder<Property> getRandomEffect(ItemStack stack)
	{
		return getRandomEffect(new Random(getData(stack) + CommonUtils.getLevelData().getGameTime() / 2L));
	}

	public static Holder<Property> getRandomEffect(Random random)
	{
		ArrayList<DeferredHolder<Property, ? extends Property>> properties = new ArrayList<>(AlchemancyProperties.REGISTRY.getEntries());
		properties.remove(AlchemancyProperties.RANDOM);
		Collections.shuffle(properties, random);
		return properties.getFirst();
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {

		if(getData(stack) == 0)
			setData(stack, level.random.nextLong());
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		if(getData(stack) == 0)
			setData(stack, itemEntity.getRandom().nextLong());
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		if(getData(stack) == 0)
			setData(stack, projectile.getRandom().nextLong());
	}

	@Override
	public Long readData(CompoundTag tag) {
		return tag.getLong("seed");
	}

	@Override
	public CompoundTag writeData(Long data) {
		return new CompoundTag(){{putLong("seed", data);}};
	}

	@Override
	public Long getDefaultData() {
		return 0L;
	}
}
