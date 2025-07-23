package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin
{
	@WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
	default  <T> T get(DataComponentMap instance, DataComponentType<? extends T> dataComponentType, Operation<T> original)
	{
		return alchemancy$get(dataComponentType, original.call(instance, dataComponentType));
	}

	@WrapOperation(method = "getOrDefault", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
	default  <T> T getOrDefault(DataComponentMap instance, DataComponentType<? extends T> component, T defaultValue, Operation<T> original)
	{
		return alchemancy$get(component, original.call(instance, component, defaultValue));
	}

	@WrapOperation(method = "has", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
	default boolean has(DataComponentMap instance, DataComponentType<?> component, Operation<Boolean> original)
	{
		return original.call(instance, component) || alchemancy$get(component, null) != null;
	}

	@Unique
	default <T> T alchemancy$get(DataComponentType<? extends T> dataComponentType, T original)
	{
		if(!AlchemancyItems.Components.INFUSED_PROPERTIES.isBound())
			return original;

		List<DataComponentType<?>> COMPONENTS_TO_SKIP = List.of(
				AlchemancyItems.Components.INFUSED_PROPERTIES.value(),
				AlchemancyItems.Components.INNATE_PROPERTIES.value(),
				AlchemancyItems.Components.PROPERTY_DATA.value());

		if(!COMPONENTS_TO_SKIP.contains(dataComponentType) && (Object)this instanceof ItemStack stack)
		{
			AtomicReference<T> result = new AtomicReference<>(original);

			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
			{
				T val = (T) propertyHolder.value().modifyDataComponent(stack, dataComponentType, result.get());
				result.set(val);
			}, !BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(dataComponentType).is(AlchemancyTags.DataComponents.UNTOGGLEABLE));

			return result.get();
		}
		return original;
	}
}
