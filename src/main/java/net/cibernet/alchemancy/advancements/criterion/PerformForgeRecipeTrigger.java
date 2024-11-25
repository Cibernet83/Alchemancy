package net.cibernet.alchemancy.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;

public class PerformForgeRecipeTrigger extends SimpleCriterionTrigger<PerformForgeRecipeTrigger.TriggerInstance>
{
	@Override
	public Codec<TriggerInstance> codec() {
		return TriggerInstance.CODEC;
	}


	public void trigger(ServerPlayer player, RecipeHolder<AbstractForgeRecipe<?>> recipe, ForgeRecipeGrid grid)
	{
		super.trigger(player, triggerInsance -> triggerInsance.matches(recipe, grid));
	}


	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ForgeRecipePredicate> predicate) implements SimpleCriterionTrigger.SimpleInstance
	{
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						ForgeRecipePredicate.CODEC.optionalFieldOf("recipe_predicate").forGetter(TriggerInstance::predicate)
				).apply(instance, TriggerInstance::new)
		);

		public boolean matches(RecipeHolder<AbstractForgeRecipe<?>> recipe, ForgeRecipeGrid grid)
		{
			return predicate.isEmpty() || predicate.get().matches(recipe, grid);
		}
	}
}
