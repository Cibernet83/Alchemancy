package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.events.handler.MobTemptHandler;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin {

	@Shadow @Nullable public abstract LivingEntity getControllingPassenger();

	@Shadow @Final public GoalSelector goalSelector;

	@Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
	public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir)
	{
		if(alchemancy$self().getFirstPassenger() instanceof LivingEntity living)
			MobTemptHandler.performIfTempted(alchemancy$self(), living, EquipmentSlotGroup.HAND, () ->
			cir.setReturnValue((LivingEntity) ((Mob) (Object)this).getFirstPassenger()));
	}

	@Inject(method = "updateControlFlags", at = @At("RETURN"))
	public void updateControlFlags(CallbackInfo ci)
	{

		if(alchemancy$self().getFirstPassenger() instanceof LivingEntity living)
			MobTemptHandler.performIfTempted(alchemancy$self(), living, EquipmentSlotGroup.HAND, () ->
			{
				goalSelector.setControlFlag(Goal.Flag.MOVE, true);
				goalSelector.setControlFlag(Goal.Flag.LOOK, true);
			});
	}

	@Unique
	private Mob alchemancy$self()
	{
		return (Mob) (Object) this;
	}

}
