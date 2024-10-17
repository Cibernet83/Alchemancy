package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.SculkBudBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class SculkingProperty extends Property
{
	@Override
	public void onKill(LivingEntity target, LivingEntity user, ItemStack stack, LivingDeathEvent event)
	{
		if(user.level().isClientSide())
			return;
		createSculkBud(target, event.getSource());
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		for (LivingEntity target : root.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(root.getBlockPos()).inflate(8), living -> living.isDeadOrDying() && !living.wasExperienceConsumed()))
		{
			createSculkBud(target, target.getLastDamageSource());
		}
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource)
	{
		playRootedParticles(root, randomSource, ParticleTypes.SCULK_CHARGE_POP);
	}

	public static void createSculkBud(LivingEntity target, DamageSource damageSource)
	{
		ServerLevel level = (ServerLevel) target.level();
		BlockPos pos = target.blockPosition();
		BlockState state = level.getBlockState(pos);
		BlockState sculk = AlchemancyBlocks.SCULK_BUD.get().defaultBlockState();

		if(sculk.canSurvive(level, pos) && (state.isAir() || state.canBeReplaced()) && level.getBlockState(pos.below()).is(BlockTags.SCULK_REPLACEABLE))
		{
			int charge = target.getExperienceReward(level, Optionull.map(damageSource, DamageSource::getEntity));

			if(charge > 0)
			{
				level.setBlock(pos, sculk, 11);
				SculkBudBlockEntity blockEntity = (SculkBudBlockEntity) level.getBlockEntity(pos);
				blockEntity.addCursor(charge, pos);

				target.skipDropExperience();
			}
		}
	}

	@Override
	public int getColor(ItemStack stack)
	{
		return ColorUtils.interpolateColorsOverTime(4, 0x009295, 0x034150);
	}
}
