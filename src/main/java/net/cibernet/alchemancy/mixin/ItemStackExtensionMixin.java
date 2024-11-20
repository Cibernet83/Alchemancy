package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(IItemStackExtension.class)
public interface ItemStackExtensionMixin
{
	@Unique
	default ItemStack self()
	{
		return (ItemStack) (Object) this;
	}

	@WrapOperation(method = "canWalkOnPowderedSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canWalkOnPowderedSnow(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"))
	default boolean canWalkOnPowderedSnow(Item instance, ItemStack stack, LivingEntity living, Operation<Boolean> original)
	{
		return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.LIGHTWEIGHT) || original.call(instance, stack, living);
	}

	@WrapOperation(method = "makesPiglinsNeutral", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;makesPiglinsNeutral(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"))
	default boolean makesPiglinsNeutral(Item instance, ItemStack stack, LivingEntity living, Operation<Boolean> original)
	{
		return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.GILDED) || original.call(instance, stack, living);
	}

	@WrapOperation(method = "canElytraFly", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canElytraFly(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"))
	default boolean canElytryFly(Item instance, ItemStack stack, LivingEntity living, Operation<Boolean> original)
	{
		return (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.GLIDER) && ElytraItem.isFlyEnabled(stack)) || original.call(instance, stack, living);
	}

	@WrapOperation(method = "elytraFlightTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;elytraFlightTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;I)Z"))
	default boolean elytraFlightTick(Item instance, ItemStack stack, LivingEntity living, int i, Operation<Boolean> original)
	{

		return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.GLIDER) || original.call(instance, stack, living, i);
	}

	@Inject(method = "canPerformAction", at = @At("RETURN"), cancellable = true)
	default void canPerformAction(ItemAbility itemAbility, CallbackInfoReturnable<Boolean> cir)
	{
		boolean original = cir.getReturnValue();
		AtomicBoolean result = new AtomicBoolean(original);

		ItemStack stack = self();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> result.set(propertyHolder.value().modifyAcceptAbility(stack, itemAbility, original, result.get())));

		cir.setReturnValue(result.get());
	}

	@Inject(method = "onDestroyed", at = @At("HEAD"))
	default void onDestroyed(ItemEntity itemEntity, DamageSource damageSource, CallbackInfo ci)
	{
		ItemStack stack = self();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEntityItemDestroyed(stack, itemEntity, damageSource));
	}

	@WrapOperation(method = "getEquipmentSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getEquipmentSlot(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
	default EquipmentSlot getEquipmentSlot(Item instance, ItemStack stack, Operation<EquipmentSlot> original)
	{
		EquipmentSlot originalSlot = original.call(instance, stack);
		AtomicReference<EquipmentSlot> slot = new AtomicReference<>(originalSlot);
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> slot.set(propertyHolder.value().modifyWearableSlot(stack, originalSlot, slot.get())));
		return slot.get();
	}
}
