package net.cibernet.alchemancy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cibernet.alchemancy.blocks.InfusionPedestalBlock;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class ItemStackHolderRenderer implements BlockEntityRenderer<ItemStackHolderBlockEntity>
{

	private final ItemRenderer itemRenderer;
	protected final EntityRenderDispatcher entityRenderDispatcher;


	public ItemStackHolderRenderer(BlockEntityRendererProvider.Context pContext)
	{
		this.itemRenderer = pContext.getItemRenderer();
		this.entityRenderDispatcher = pContext.getEntityRenderer();
	}

	@Override
	public void render(ItemStackHolderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		poseStack.pushPose();

		if(blockEntity.getBlockState().getBlock() instanceof ItemStackHolderCustomRender block)
		{
			block.render(itemRenderer, blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
		}
		else {
			Level level = blockEntity.getLevel();
			BlockPos itemLightPos = blockEntity.getBlockPos().above();
			double height = blockEntity.getBlockState().getShape(level, blockEntity.getBlockPos()).bounds().maxY;

			poseStack.translate(0.5f, height + 0.2f, 0.5f);
			poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getBlockState().getValue(InfusionPedestalBlock.FACING).toYRot()));
			this.itemRenderer.renderStatic(blockEntity.getItem(), ItemDisplayContext.GROUND, LightTexture.pack(level.getBrightness(LightLayer.BLOCK, itemLightPos),
					level.getBrightness(LightLayer.SKY, itemLightPos)), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, level, 0);
		}
		poseStack.popPose();
	}

}
