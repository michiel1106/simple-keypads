package com.bikerboys.simplekeypads.entity.client;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.*;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;

@OnlyIn(Dist.CLIENT)
public class KeypadRenderer extends EntityRenderer<KeypadEntity> {
    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SimpleKeypads.MODID, "textures/entity/keypad.png");

    private final KeypadModel<KeypadEntity> model;

    public KeypadRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new KeypadModel<>(context.bakeLayer(ModModelLayers.KEYPAD_LAYER));
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(KeypadEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose(); // Adjust so it’s not inside the ground

        poseStack.translate(0.0D, 1.20D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        // compute Y rotation and add a 180° correction on east/west so those faces render flipped
        float yRot = 180.0F - entity.getYRot();
        Direction dir = entity.getDirection();
        if (dir == Direction.EAST || dir == Direction.WEST) {
            yRot += 180.0F;
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        VertexConsumer vertexconsumer = buffer.getBuffer(model.renderType(TEXTURE));
        this.model.renderToBuffer(
                poseStack,
                vertexconsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }





    @Override
    public @NotNull ResourceLocation getTextureLocation(KeypadEntity entity) {
        return TEXTURE;
    }
}