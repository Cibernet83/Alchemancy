package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.MixinUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

public class SodiumCompat
{
	@Mixin({ItemRenderer.class})
	public static class ItemRendererMixin
	{
		@WrapMethod(method = "render")
		public void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, Operation<Void> original)
		{
			if(CommonUtils.hasPropertyDrivenTint(itemStack))
				MixinUtils.sodiumCompat$processedStack = itemStack;
			original.call(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, p_model);
			MixinUtils.sodiumCompat$processedStack = null;
		}
	}

	@Mixin(BakedModelEncoder.class)
	public static class BakedModelEncoderMixin
	{
		@WrapMethod(method = "writeQuadVertices(Lnet/caffeinemc/mods/sodium/api/vertex/buffer/VertexBufferWriter;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;IIIZ)V")
		private static void writeQuadVertices(VertexBufferWriter writer, PoseStack.Pose matrices, ModelQuadView quad, int color, int light, int overlay, boolean colorize, Operation<Void> original)
		{
			if(MixinUtils.sodiumCompat$processedStack != null)
			{
				color = FastColor.ARGB32.color(FastColor.ARGB32.alpha(color), FastColor.ARGB32.blue(color), FastColor.ARGB32.green(color), FastColor.ARGB32.red(color));
				color = CommonUtils.getPropertyDrivenTint(MixinUtils.sodiumCompat$processedStack, quad.getColorIndex(), color);
				color = FastColor.ARGB32.color(FastColor.ARGB32.alpha(color), FastColor.ARGB32.blue(color), FastColor.ARGB32.green(color), FastColor.ARGB32.red(color));
			}
			original.call(writer, matrices, quad, color, light, overlay, colorize);
		}
	}
}
