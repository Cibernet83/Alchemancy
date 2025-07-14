package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.Nullable;

public class LaunchingProperty extends Property implements IDataHolder<Long>
{
	@Override
	public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target)
	{
		launch(user, weapon, target, 1f);
		setData(weapon, target.level().getGameTime());
	}

	@Override
	public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack)
	{
		launch(null, stack, target, 1.2f);
	}

	public static final DustColorTransitionOptions PARTICLES = new DustColorTransitionOptions(
			Vec3.fromRGB24(0xE266FF).toVector3f(), Vec3.fromRGB24(0x6672FF).toVector3f(), 1.0F
	);

	@Override
	public void modifyKnockBackApplied(LivingEntity user, ItemStack weapon, LivingEntity target, LivingKnockBackEvent event)
	{
		if(getData(weapon) == target.level().getGameTime())
			event.setCanceled(true);

	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random) {

		BlockPos pPos = root.getBlockPos();
		Level pLevel = root.getLevel();

		double d0 = pPos.getX();
		double d1 = pPos.getY();
		double d2 = pPos.getZ();

		double d5 = random.nextDouble();
		double d6 = random.nextDouble();
		double d7 = random.nextDouble();
		pLevel.addParticle(PARTICLES, d0 + d5, d1 + d6, d2 + d7, 0.0D, 4D, 0.0D);
	}

	public void launch(@Nullable LivingEntity user, ItemStack weapon, Entity target, float strength)
	{
		target.setDeltaMovement(target.getDeltaMovement().multiply(1, 0, 1).add(0, strength, 0));
		target.hasImpulse = true;
		target.hurtMarked = true;

		if(PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, weapon.isDamageableItem()))
		{
			int durabilityConsumed = PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 20);
			if(user != null)
				weapon.hurtAndBreak(durabilityConsumed, user, EquipmentSlot.MAINHAND);
			else if(target.level() instanceof ServerLevel serverLevel) weapon.hurtAndBreak(durabilityConsumed, serverLevel, null, (item) -> {});
		}
		else consumeItem(user, weapon, EquipmentSlot.MAINHAND);
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE266FF;
	}

	@Override
	public Long readData(CompoundTag tag) {
		return tag.getLong("crit_timestamp");
	}

	@Override
	public CompoundTag writeData(Long data) {
		return new CompoundTag(){{putLong("crit_timestamp", data);}};
	}

	@Override
	public Long getDefaultData() {
		return 0L;
	}
}
