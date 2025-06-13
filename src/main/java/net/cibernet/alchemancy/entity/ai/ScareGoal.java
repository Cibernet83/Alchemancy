package net.cibernet.alchemancy.entity.ai;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyPoiTypes;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ScareGoal extends PanicGoal {
	private static final int RANGE = 10;

	private final TargetingConditions TARGETING;

	private final Predicate<ItemStack> itemStackPredicate;

	public ScareGoal(PathfinderMob mob, double speedModifier, Predicate<ItemStack> itemStackPredicate) {
		super(mob, speedModifier);
		this.itemStackPredicate = itemStackPredicate;
		TARGETING = TargetingConditions.forNonCombat().range(RANGE).ignoreLineOfSight().selector(living ->
		{
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (itemStackPredicate.test(living.getItemBySlot(slot)))
					return true;
			}
			return false;
		});
		;
	}

	public ScareGoal(PathfinderMob mob, double speedModifier, Holder<Property> property) {
		this(mob, speedModifier, stack -> InfusedPropertiesHelper.hasProperty(stack, property));
	}

	@Override
	protected boolean shouldPanic() {
		ServerLevel level = (ServerLevel) mob.level();

		return level.getNearestPlayer(TARGETING, this.mob) != null || isNearRootedScary(level);
	}

	protected boolean isNearRootedScary(ServerLevel level) {

		int distSqr = RANGE * RANGE;
		return RootedItemBlockEntity.getCachedRoots(level).stream()
				.anyMatch(root -> root.getBlockPos().distSqr(mob.blockPosition()) <= distSqr && itemStackPredicate.test(root.getItem()));
	}
}
