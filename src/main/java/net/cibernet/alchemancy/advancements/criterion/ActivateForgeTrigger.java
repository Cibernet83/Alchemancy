package net.cibernet.alchemancy.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ActivateForgeTrigger extends SimpleCriterionTrigger<ActivateForgeTrigger.TriggerInstance>
{

	@Override
	public Codec<TriggerInstance> codec() {
		return TriggerInstance.CODEC;
	}


	public void trigger(ServerPlayer player, BlockPos pos)
	{

		super.trigger(player, triggerInsance -> triggerInsance.matches(pos));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<BlockPos> pos) implements SimpleInstance
	{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						BlockPos.CODEC.optionalFieldOf("forge_position").forGetter(TriggerInstance::pos)
				).apply(instance, TriggerInstance::new)
		);

		public boolean matches(BlockPos pos) {
			return this.pos.isEmpty() || this.pos.get().equals(pos);
		}
	}
}
