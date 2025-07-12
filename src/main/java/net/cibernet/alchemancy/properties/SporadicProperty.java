package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SporadicProperty extends Property
{
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(!user.level().isClientSide() && user.getRandom().nextFloat() < 0.005f)
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(user, user, stack));
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
		if(!root.getLevel().isClientSide() && root.getLevel().getRandom().nextFloat() < 0.005f)
			for (LivingEntity entity : entitiesInBounds)
				InfusedPropertiesHelper.forEachProperty(root.getItem(), propertyHolder -> propertyHolder.value().onActivationByBlock(root.getLevel(), root.getBlockPos(), entity, root.getItem()));
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
		playRootedParticles(root, randomSource, ParticleTypes.MYCELIUM);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x9A6DCE;
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.ITALIC);
	}
}
