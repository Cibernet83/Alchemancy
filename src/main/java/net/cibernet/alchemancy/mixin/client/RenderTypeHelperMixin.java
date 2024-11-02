package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.ITintModifier;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(RenderTypeHelper.class)
public class RenderTypeHelperMixin
{
	@WrapOperation(method = "getFallbackItemRenderType", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ChunkRenderTypeSet;contains(Lnet/minecraft/client/renderer/RenderType;)Z"))
	private static boolean isTranslucent(ChunkRenderTypeSet instance, RenderType renderType, Operation<Boolean> original, @Local(argsOnly = true)ItemStack stack)
	{
		if(!original.call(instance, renderType))
			return CommonUtils.hasPropertyDrivenAlpha(stack);
		return true;
	}
}
