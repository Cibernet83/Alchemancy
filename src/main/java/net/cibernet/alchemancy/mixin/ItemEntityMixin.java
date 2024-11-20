package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
	@Shadow public abstract ItemStack getItem();

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;moveTowardsClosestSpace(DDD)V", ordinal = 0))
	protected void moveTowardsClosestSpace(ItemEntity instance, double x, double y, double z, Operation<Void> original)
	{
		if(!InfusedPropertiesHelper.hasInfusedProperty(instance.getItem(), AlchemancyProperties.PHASING))
			original.call(instance, x, y, z);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;onGround()Z", ordinal = 0))
	protected void setNoPhysics(CallbackInfo ci)
	{
		if(InfusedPropertiesHelper.hasProperty(getItem(), AlchemancyProperties.PHASING))
			((ItemEntity)(Object)this).noPhysics = true;
	}
}
