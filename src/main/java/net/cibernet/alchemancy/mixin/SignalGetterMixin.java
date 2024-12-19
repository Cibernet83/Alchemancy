package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.util.RedstoneSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin
{
//	@WrapOperation(method = "getSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSignal(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"))
//	default int getSignal(BlockState instance, BlockGetter blockGetter, BlockPos pos, Direction direction, Operation<Integer> original)
//	{
//		if(blockGetter instanceof ServerLevel level)
//			return Math.max(original.call(instance, blockGetter, pos, direction), RedstoneSources.getSourcePower(level, pos));
//		return original.call(instance, blockGetter, pos, direction);
//	}
//
//	@WrapOperation(method = "getDirectSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDirectSignal(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"))
//	default int getDirectSignal(BlockState instance, BlockGetter blockGetter, BlockPos pos, Direction direction, Operation<Integer> original)
//	{
//		if(blockGetter instanceof ServerLevel level)
//		{
//			RedstoneSources.RedstoneSource source = RedstoneSources.getSourceAt(level, pos);
//			System.out.println("pos: " + pos + " source dir: " + source.direction + " check dir: " + direction + " power: " + source.power);
//			if(Direction.UP == direction)
//				return Math.max(original.call(instance, blockGetter, pos, direction), source.power);
//		}
//		return original.call(instance, blockGetter, pos, direction);
//	}

	@WrapOperation(method = "getSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;shouldCheckWeakPower(Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
	default boolean shouldCheckWeakPower(BlockState instance, SignalGetter signalGetter, BlockPos pos, Direction direction, Operation<Boolean> original)
	{
		return original.call(instance, signalGetter, pos, direction) || (this instanceof ServerLevel serverLevel && RedstoneSources.getSourcePower(serverLevel, pos.relative(direction.getOpposite())) > 0);
	}
}
