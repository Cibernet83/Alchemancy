package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(IBlockStateExtension.class)
public interface BlockStateExtensionMixin
{
	@WrapOperation(method = "getFriction", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/Block;getFriction(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
	default float getFriction(Block instance, BlockState state, LevelReader levelReader, BlockPos pos, Entity entity, Operation<Float> original)
	{
		float originalResult = original.call(instance, state, levelReader, pos, entity);
		AtomicReference<Float> result = new AtomicReference<>(originalResult);

		if(entity instanceof ItemEntity itemEntity)
		{
			ItemStack stack = itemEntity.getItem();
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> result.set(propertyHolder.value().modifyStepOnFriction(entity, stack, originalResult, result.get())));
		}
		else if(entity instanceof LivingEntity living)
		{
			ItemStack stack = living.getItemBySlot(EquipmentSlot.FEET);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> result.set(propertyHolder.value().modifyStepOnFriction(entity, stack, originalResult, result.get())));
		}

		return result.get();
	}
}
