package net.cibernet.alchemancy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.blockentities.AlchemancyCatalystBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.TickRateManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class AlchemancyCatalystRenderer implements BlockEntityRenderer<AlchemancyCatalystBlockEntity>
{
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "alchemancy_catalyst"), "main");
	public static final ResourceLocation FRAME_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "textures/entity/alchemancy_catalyst/alchemancy_catalyst_frame.png");
	private final ModelPart outer;
	private final ModelPart inner;

	private static float globalTime = 0;

	public AlchemancyCatalystRenderer(BlockEntityRendererProvider.Context context)
	{
		ModelPart root = context.bakeLayer(LAYER_LOCATION);
		this.outer = root.getChild("outer");
		this.inner = outer.getChild("inner");
	}


	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition outer = partdefinition.addOrReplaceChild("outer", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -14.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition inner = outer.addOrReplaceChild("inner", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		inner.addOrReplaceChild("crystal", CubeListBuilder.create().texOffs(0, 32).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.7854F, 0.0F, -0.6109F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void render(AlchemancyCatalystBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		float scale = 1;
		poseStack.translate(0.5, -0.5, 0.5);

		float time = blockEntity.getSpinOffset() + ((globalTime + partialTick) * 0.05f);
		inner.yRot = time % 360;
		inner.y = Mth.sin(time * 1.5f) * 2f / scale;

		outer.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucentCull(FRAME_TEXTURE_LOCATION)), packedLight, packedOverlay);
		poseStack.translate(0, 0.5 - 0.5 * scale, 0);
		poseStack.scale(scale, scale, scale);
		outer.render(poseStack, bufferSource.getBuffer(
				RenderType.entityTranslucentCull(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "textures/entity/alchemancy_catalyst/"+ blockEntity.getCrystalTexture() + ".png"))),
				LightTexture.FULL_BRIGHT, packedOverlay, blockEntity.getTint());
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event)
	{
		if(Minecraft.getInstance().level != null && !Minecraft.getInstance().level.tickRateManager().isFrozen())
			globalTime++;
	}
}
