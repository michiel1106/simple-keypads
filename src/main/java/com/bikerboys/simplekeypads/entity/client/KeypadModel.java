package com.bikerboys.simplekeypads.entity.client;// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import static com.bikerboys.simplekeypads.SimpleKeypads.MODID;

public class KeypadModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "keypad"), "main");
	private final ModelPart Body;
	private final ModelPart screen;
	private final ModelPart Buttons;

	public KeypadModel(ModelPart root) {
		this.Body = root.getChild("Body");
		this.screen = root.getChild("screen");
		this.Buttons = root.getChild("Buttons");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -9.0F, 0.0F, 5.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition screen = partdefinition.addOrReplaceChild("screen", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -8.5F, -0.25F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Buttons = partdefinition.addOrReplaceChild("Buttons", CubeListBuilder.create().texOffs(8, 10).addBox(-1.25F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 0).addBox(-1.25F, -2.25F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 2).addBox(-1.25F, -3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 4).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 6).addBox(1.25F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 12).addBox(1.25F, -2.25F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 8).addBox(-1.25F, -4.75F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 10).addBox(0.0F, -3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(12, 12).addBox(1.25F, -3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 13).addBox(0.0F, -2.25F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 13).addBox(1.25F, -4.75F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 14).addBox(0.0F, -4.75F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 23.0F, 0.75F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		screen.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		Buttons.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}


}