package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.ITintModifier;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ShockUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulbindProperty extends Property implements ITintModifier
{
	static ResourceKey<DamageType> SOUL_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "soul_escaped"));

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(!user.level().isClientSide() && user.getRandom().nextFloat() < 0.001f && InfusedPropertiesHelper.hasInfusedProperty(stack, asHolder()))
		{
			Vec3 lookVec = user.getLookAngle().scale(0.5);
			((ServerLevel)user.level()).sendParticles(ParticleTypes.SOUL, user.position().x + lookVec.x, user.getEyeY() + lookVec.y, user.position().z + lookVec.z, 1, 0, 0.05, 0, 0.05);
			user.playSound(SoundEvents.SOUL_ESCAPE.value(), 1, 1);

			InfusedPropertiesHelper.removeProperty(stack, asHolder());

			if(user.getRandom().nextFloat() < 0.2f && user instanceof LivingEntity living)
				living.hurt(new DamageSource(user.damageSources().damageTypes.getHolderOrThrow(SOUL_DAMAGE_KEY)), 3);

		}
	}

	@Override
	public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint)
	{
		float partialSecond = ((System.currentTimeMillis() % (1000L * (long) 2)) / 1000f);
		return FastColor.ARGB32.lerp(Mth.sin((Mth.DEG_TO_RAD * 360 * (partialSecond / 2))) * 0.5f + 0.5f, currentTint, FastColor.ARGB32.color(FastColor.ARGB32.alpha(currentTint), 0xC4FDFF));
	}

	@Override
	public int getColor(ItemStack stack)
	{
		float partialSecond = ((System.currentTimeMillis() % (1000L * (long) 2)) / 1000f);
		return FastColor.ARGB32.lerp(Mth.sin((Mth.DEG_TO_RAD * 360 * (partialSecond / 2))) * 0.5f + 0.5f, 0x2AC9CF, 0xC4FDFF);
	}
}
