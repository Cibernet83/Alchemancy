package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ShatteringProperty extends Property implements IDataHolder<Boolean>
{

	private static final float RADIUS = 3;

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{
		if(stack.getMaxDamage() <= stack.getDamageValue() + resultingAmount)
			shatter(user.level(), user, stack);

		return resultingAmount;
	}

	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource cause)
	{
		if(!itemEntity.isRemoved())
		{
			itemEntity.discard();
			Level level = itemEntity.level();
			shatter(level, itemEntity, stack);
		}
	}

	public void shatter(Level level, Entity source, ItemStack stack)
	{
		if(getData(stack))
			return;

		setData(stack, true);

		RandomSource rand = level.getRandom();
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(RADIUS));
		DamageSource damageSource = activationDamageSource(level, source, source.position());

		if(level instanceof ServerLevel serverLevel)
			for(int i = 0; i < 20; i++)
			{
				serverLevel.sendParticles(ParticleTypes.CRIT, source.position().x, source.position().y, source.position().z, 1,
						0, 0, 0, rand.nextDouble() * 0.5);
			}

		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
		{
			propertyHolder.value().onActivation(source, source, stack);
			for (LivingEntity target : entities) {
				if(target.distanceTo(source) <= RADIUS)
				{
					propertyHolder.value().onAttack(source, stack, damageSource, target);
				}
			}
		});
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x53A6B5;
	}

	@Override
	public Boolean readData(CompoundTag tag) {
		return tag.getBoolean("activated");
	}

	@Override
	public CompoundTag writeData(Boolean data) {
		return new CompoundTag(){{putBoolean("activated", data);}};
	}

	@Override
	public Boolean getDefaultData() {
		return false;
	}
}
