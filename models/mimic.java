// Made with Blockbench 4.1.3
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class mimic extends EntityModel<Entity> {
	private final ModelRenderer bottom;
	private final ModelRenderer teethBottom_r1;
	private final ModelRenderer base_r1;
	private final ModelRenderer top;
	private final ModelRenderer teethTop_r1;
	private final ModelRenderer nose_r1;
	private final ModelRenderer bb_main;
	private final ModelRenderer legLButt_r1;
	private final ModelRenderer legRButt_r1;
	private final ModelRenderer legLFace_r1;
	private final ModelRenderer legRFront_r1;
	private final ModelRenderer legLBack_r1;
	private final ModelRenderer legLMid_r1;
	private final ModelRenderer legLFront_r1;
	private final ModelRenderer legRBack_r1;
	private final ModelRenderer legRFace_r1;
	private final ModelRenderer legRMid_r1;

	public mimic() {
		texWidth = 64;
		texHeight = 64;

		bottom = new ModelRenderer(this);
		bottom.setPos(0.0F, 20.0F, 0.0F);
		

		teethBottom_r1 = new ModelRenderer(this);
		teethBottom_r1.setPos(0.0F, 1.0F, 0.0F);
		bottom.addChild(teethBottom_r1);
		setRotationAngle(teethBottom_r1, -1.5708F, 0.0F, 0.0F);
		teethBottom_r1.texOffs(0, 43).addBox(-6.0F, -6.0F, -11.0F, 12.0F, 12.0F, 1.0F, 0.0F, false);

		base_r1 = new ModelRenderer(this);
		base_r1.setPos(0.0F, 1.0F, 0.0F);
		bottom.addChild(base_r1);
		setRotationAngle(base_r1, 3.1416F, 0.0F, 0.0F);
		base_r1.texOffs(0, 19).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 10.0F, 14.0F, 0.0F, false);

		top = new ModelRenderer(this);
		top.setPos(0.0F, 12.0F, 7.0F);
		

		teethTop_r1 = new ModelRenderer(this);
		teethTop_r1.setPos(0.0F, 0.0F, 0.0F);
		top.addChild(teethTop_r1);
		setRotationAngle(teethTop_r1, -1.5708F, 0.0F, 0.0F);
		teethTop_r1.texOffs(38, 43).addBox(-6.0F, 1.0F, 0.0F, 12.0F, 12.0F, 1.0F, 0.0F, false);

		nose_r1 = new ModelRenderer(this);
		nose_r1.setPos(0.0F, 0.0F, 0.0F);
		top.addChild(nose_r1);
		setRotationAngle(nose_r1, 3.1416F, 0.0F, 0.0F);
		nose_r1.texOffs(0, 0).addBox(-1.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
		nose_r1.texOffs(0, 0).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F, false);

		bb_main = new ModelRenderer(this);
		bb_main.setPos(0.0F, 24.0F, 0.0F);
		

		legLButt_r1 = new ModelRenderer(this);
		legLButt_r1.setPos(3.0F, -3.0F, 6.0F);
		bb_main.addChild(legLButt_r1);
		setRotationAngle(legLButt_r1, 0.7854F, 0.0F, -0.3491F);
		legLButt_r1.texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legRButt_r1 = new ModelRenderer(this);
		legRButt_r1.setPos(-3.0F, -3.0F, 6.0F);
		bb_main.addChild(legRButt_r1);
		setRotationAngle(legRButt_r1, 0.7854F, 0.0F, 0.3491F);
		legRButt_r1.texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legLFace_r1 = new ModelRenderer(this);
		legLFace_r1.setPos(3.0F, -3.0F, -6.0F);
		bb_main.addChild(legLFace_r1);
		setRotationAngle(legLFace_r1, -0.7854F, 0.0F, -0.3491F);
		legLFace_r1.texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legRFront_r1 = new ModelRenderer(this);
		legRFront_r1.setPos(-6.0F, -3.0F, -4.0F);
		bb_main.addChild(legRFront_r1);
		setRotationAngle(legRFront_r1, -0.3491F, 0.0F, 0.7854F);
		legRFront_r1.texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legLBack_r1 = new ModelRenderer(this);
		legLBack_r1.setPos(6.0F, -3.0F, 4.0F);
		bb_main.addChild(legLBack_r1);
		setRotationAngle(legLBack_r1, 0.3491F, 0.0F, -0.7854F);
		legLBack_r1.texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legLMid_r1 = new ModelRenderer(this);
		legLMid_r1.setPos(6.0F, -3.0F, 0.0F);
		bb_main.addChild(legLMid_r1);
		setRotationAngle(legLMid_r1, 0.0F, 0.0F, -0.7854F);
		legLMid_r1.texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legLFront_r1 = new ModelRenderer(this);
		legLFront_r1.setPos(6.0F, -3.0F, -4.0F);
		bb_main.addChild(legLFront_r1);
		setRotationAngle(legLFront_r1, -0.3491F, 0.0F, -0.7854F);
		legLFront_r1.texOffs(1, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legRBack_r1 = new ModelRenderer(this);
		legRBack_r1.setPos(-6.0F, -3.0F, 4.0F);
		bb_main.addChild(legRBack_r1);
		setRotationAngle(legRBack_r1, 0.3491F, 0.0F, 0.7854F);
		legRBack_r1.texOffs(1, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legRFace_r1 = new ModelRenderer(this);
		legRFace_r1.setPos(-3.0F, -3.0F, -6.0F);
		bb_main.addChild(legRFace_r1);
		setRotationAngle(legRFace_r1, -0.7854F, 0.0F, 0.3491F);
		legRFace_r1.texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

		legRMid_r1 = new ModelRenderer(this);
		legRMid_r1.setPos(-6.0F, -3.0F, 0.0F);
		bb_main.addChild(legRMid_r1);
		setRotationAngle(legRMid_r1, 0.0F, 0.0F, 0.7854F);
		legRMid_r1.texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bottom.render(matrixStack, buffer, packedLight, packedOverlay);
		top.render(matrixStack, buffer, packedLight, packedOverlay);
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}