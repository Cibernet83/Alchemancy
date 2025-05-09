package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Mixin(value = ItemStack.class)
public abstract class ItemStackMixin
{
	@Shadow
	public abstract int getDamageValue();

	@Unique
	public ItemStack alchemancy$self()
	{
		return (ItemStack) (Object) this;
	}

	@Inject(at = @At(value = "CONSTANT",
			shift = At.Shift.BEFORE, args = "classValue=net/minecraft/server/level/ServerPlayer", ordinal = 0),
			method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V")
	public void hurtAndBreak(int damageAmount, ServerLevel level, LivingEntity user, Consumer<Item> onBreak, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalIntRef newDamageAmount)
	{
		ItemStack stack = alchemancy$self();
		int originalResult = newDamageAmount.get();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
				newDamageAmount.set(propertyHolder.value().modifyDurabilityConsumed(stack, level, user, originalResult, newDamageAmount.get())));
	}

	@WrapOperation(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder$Reference;is(Lnet/minecraft/tags/TagKey;)Z"))
	public boolean isInTag(Holder.Reference<Item> instance, TagKey<Item> tagKey, Operation<Boolean> original)
	{
		ItemStack stack = alchemancy$self();

		for (Holder<Property> property : InfusedPropertiesHelper.getInfusedProperties(stack)) {
			TriState result = property.value().isItemInTag(stack, tagKey);

			if(!result.isDefault())
				return result.isTrue();
		}

		return original.call(instance, tagKey);
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	public void finishUsingItem(Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir)
	{
		ItemStack stack = alchemancy$self();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
			if(propertyHolder.value().onFinishUsingItem(livingEntity, level, stack))
				cir.setReturnValue(stack);
		});
		if(stack.getFoodProperties(livingEntity) != null)
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(livingEntity, livingEntity, stack));
	}

	@WrapOperation(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getDestroySpeed(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)F"))
	public float getDestroySpeed(Item instance, ItemStack stack, BlockState state, Operation<Float> original)
	{
		Tool tool = stack.get(DataComponents.TOOL);
		return original.call(instance, stack, state) * (tool != null && tool.isCorrectForDrops(state) ? AlchemancyProperties.RUSTY.get().getMiningSpeedMultiplier(stack) : 1);
	}

	@Inject(method = "getUseDuration", at = @At("RETURN"), cancellable = true)
	public void getUseDuration(LivingEntity entity, CallbackInfoReturnable<Integer> cir)
	{
		int original = cir.getReturnValue();
		AtomicInteger result = new AtomicInteger(original);
		ItemStack stack = alchemancy$self();

		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> result.set(propertyHolder.value().modifyUseDuration(stack, original, result.get())));
		cir.setReturnValue(result.get());
	}

	@Inject(method = "getUseAnimation", at = @At("RETURN"), cancellable = true)
	public void getUseAnimation(CallbackInfoReturnable<UseAnim> cir)
	{
		AtomicReference<Optional<UseAnim>> useAnim = new AtomicReference<>(Optional.empty());
		ItemStack stack = alchemancy$self();

		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> useAnim.set(propertyHolder.value().modifyUseAnimation(stack, cir.getReturnValue(), useAnim.get())));
		if(useAnim.get().isPresent())
			cir.setReturnValue(useAnim.get().get());
	}
}
