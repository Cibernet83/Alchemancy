package net.cibernet.alchemancy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.blockentities.AlchemancyCatalystBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.Mod;

import java.util.Optional;

public class AlchemancyCatalystItemRenderer extends BlockEntityWithoutLevelRenderer
{
	private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

	public static final AlchemancyCatalystItemRenderer instance = new AlchemancyCatalystItemRenderer();

	public AlchemancyCatalystItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
	}

	private static final AlchemancyCatalystBlockEntity STATIC_CATALYST = new AlchemancyCatalystBlockEntity(BlockPos.ZERO, AlchemancyBlocks.ALCHEMANCY_CATALYST.get().defaultBlockState());
	public static final ModelResourceLocation FRAME_LOCATION = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "item/alchemancy_catalyst_frame"));

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		this.blockEntityRenderDispatcher.renderItem(STATIC_CATALYST, poseStack, buffer, packedLight, packedOverlay);
		poseStack.translate(-0.5, 0.5, -0.5);
		renderWeaponPart(stack, poseStack, buffer, packedLight, packedOverlay);
	}

	private static void renderWeaponPart(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{

		ModelResourceLocation modelLoc = FRAME_LOCATION;

		ModelManager modelManager = Minecraft.getInstance().getModelManager();
		BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);

		poseStack.pushPose();
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

		for (var modelPasses : model.getRenderPasses(stack, true)) {
			for (var rendertype : modelPasses.getRenderTypes(stack, true))
			{
				VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(bufferSource, rendertype, true, stack.hasFoil());
				itemRenderer.renderModelLists(modelPasses, stack, packedLight, packedOverlay, poseStack, vertexconsumer);
			}
		}
		poseStack.popPose();
	}
}
