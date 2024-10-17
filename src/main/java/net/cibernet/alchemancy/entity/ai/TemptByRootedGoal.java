package net.cibernet.alchemancy.entity.ai;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class TemptByRootedGoal extends Goal
{
	private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
	protected final PathfinderMob mob;
	private final double speedModifier;

	@Nullable
	BlockPos targetPos;
	private int calmDown;
	private boolean isRunning;
	private final Predicate<ItemStack> items;

	public TemptByRootedGoal(PathfinderMob mob, double speedModifier, Predicate<ItemStack> items)
	{
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.items = items;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public TemptByRootedGoal(PathfinderMob mob, double speedModifier, Holder<Property> property)
	{
		this(mob, speedModifier, stack -> InfusedPropertiesHelper.hasProperty(stack, property));
	}

	@Override
	public boolean canUse() {
		if (this.calmDown > 0) {
			this.calmDown--;
			return false;
		} else {

			targetPos = BlockPos.findClosestMatch(mob.blockPosition(), 36, 1,
					checkPos -> mob.level().getBlockEntity(checkPos) instanceof RootedItemBlockEntity root && this.items.test(root.getItem())).orElse(null);

			return targetPos != null;
		}
	}

	@Override
	public boolean canContinueToUse()
	{
		return this.canUse();
	}

	@Override
	public void start() {
		this.isRunning = true;
	}

	@Override
	public void stop() {
		this.targetPos = null;
		this.mob.getNavigation().stop();
		this.calmDown = reducedTickDelay(100);
		this.isRunning = false;
	}

	@Override
	public void tick()
	{
		Vec3 pos = this.targetPos.getCenter();
		this.mob.getLookControl().setLookAt(pos.x, pos.y, pos.z, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
		if (this.mob.distanceToSqr(pos) < 6.25) {
			this.mob.getNavigation().stop();
		} else {
			this.mob.getNavigation().moveTo(pos.x, pos.y, pos.z, this.speedModifier);
		}
	}

	public boolean isRunning() {
		return this.isRunning;
	}
}
