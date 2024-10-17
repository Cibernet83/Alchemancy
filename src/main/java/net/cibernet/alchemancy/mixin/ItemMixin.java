package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Item.class)
public class ItemMixin
{
	@Inject(method = "getUseDuration", at = @At("RETURN"), cancellable = true)
	public void getUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir)
	{
		int original = cir.getReturnValue();
		AtomicInteger result = new AtomicInteger(original);

		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> result.set(propertyHolder.value().modifyUseDuration(stack, original, result.get())));
		cir.setReturnValue(result.get());
	}

	@Inject(method = "getUseAnimation", at = @At("RETURN"), cancellable = true)
	public void getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> cir)
	{
		AtomicReference<Optional<UseAnim>> useAnim = new AtomicReference<>(Optional.empty());

		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> useAnim.set(propertyHolder.value().modifyUseAnimation(stack, cir.getReturnValue(), useAnim.get())));
		if(useAnim.get().isPresent())
			cir.setReturnValue(useAnim.get().get());
	}
}
