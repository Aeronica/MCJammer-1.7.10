package net.aeronica.mods.mcjammer.client.gui;

import net.aeronica.mods.mcjammer.common.inventory.ContainerInstrument;
import net.aeronica.mods.mcjammer.common.items.InstrumentEnum;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import scala.Char;

public class GuiInstInvAdjustRotations extends GuiContainer {
	public static final int GUI_ID = 6;

	private Minecraft mc;
	private FontRenderer fontRenderer = null;

	private static final ResourceLocation inventoryTexture = new ResourceLocation(
			ModInfo.ID.toLowerCase(), "textures/gui/instr_inv_adjust.png");

	int theInvItemSlot;
	/**
	 * x and y size of the inventory window in pixels. Defined as float, passed
	 * as int These are used for drawing the player model.
	 */
	private float xSize_lo;
	private float ySize_lo;
	private int subtype;
	InstrumentEnum instrEnum;

	GuiSlider gsERotX, gsERotY, gsERotZ, gsETrnX, gsETrnY, gsETrnZ, gsETrnS;
	GuiSlider gsPitch, gsYaw;
	
	// Player rotations
	float pitch = 0f;
	float yaw = 0f;

	/** The inventory to render on screen */
	// private final InventoryInstrument inventory;

	public GuiInstInvAdjustRotations(ContainerInstrument containerInstrument) {
		super(containerInstrument);
		this.mc = Minecraft.getMinecraft();
		this.fontRenderer = mc.fontRenderer;

		// The slot inventory.currentItem is 0 based
		this.theInvItemSlot = mc.thePlayer.inventory.currentItem;
		xSize = 255;
		ySize = 255;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		subtype = mc.thePlayer.getHeldItem().getItemDamage();
		instrEnum = InstrumentEnum.values()[this.subtype];
		// clear control list
		buttonList.clear();

		// create sliders
		int posX = guiLeft + 84;
		int intY = guiTop + 160;
		// int id, int posX, int posY, int width, int height, String name, float
		// value, float valueMin, float valueMax, float valueStep
		gsERotX = new GuiSlider(0, posX, intY + 00, 150, 10, "EqRotX", instrEnum.rendEQUIProt1[0], -180f, 180f, 1.0f);
		gsERotY = new GuiSlider(1, posX, intY + 10, 150, 10, "EqRotY", instrEnum.rendEQUIProt1[1], -180f, 180f, 1.0f);
		gsERotZ = new GuiSlider(1, posX, intY + 20, 150, 10, "EqRotZ", instrEnum.rendEQUIProt1[2], -180f, 180f, 1.0f);
		gsETrnX = new GuiSlider(2, posX, intY + 30, 150, 10, "EqTrnX", instrEnum.rendEQUIPtrans[0], -2f, 2f, .01f);
		gsETrnY = new GuiSlider(3, posX, intY + 40, 150, 10, "EqTrnY", instrEnum.rendEQUIPtrans[1], -2f, 2f, .01f);
		gsETrnZ = new GuiSlider(4, posX, intY + 50, 150, 10, "EqTrnZ", instrEnum.rendEQUIPtrans[2], -2f, 2f, .01f);
		gsETrnS = new GuiSlider(5, posX, intY + 60, 150, 10, "EqTrnS", instrEnum.rendEQUIPtrans[3],  0f, 5f, .05f);
		
		// player rotations
		gsPitch = new GuiSlider(3, posX, intY + 71, 100, 10, "Player Pit", pitch, -90f, 90f, 1f);
		gsYaw = new GuiSlider(3, posX, intY + 81, 100, 10, "Player Yaw", yaw, -90f, 90f, 1f);

		// add sliders to control list
		buttonList.add(gsERotX);
		buttonList.add(gsERotY);
		buttonList.add(gsERotZ);
		buttonList.add(gsETrnX);
		buttonList.add(gsETrnY);
		buttonList.add(gsETrnZ);
		buttonList.add(gsETrnS);
		buttonList.add(gsPitch);
		buttonList.add(gsYaw);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		this.xSize_lo = (float) par1;
		this.ySize_lo = (float) par2;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		if (mc.thePlayer.getHeldItem() == null)
			return;
		String s = mc.thePlayer.getHeldItem().getDisplayName();
		drawStringRight(s, 12, 4210752);

		instrEnum.rendEQUIProt1[0] = gsERotX.getValue();
		instrEnum.rendEQUIProt1[1] = gsERotY.getValue();
		instrEnum.rendEQUIProt1[2] = gsERotZ.getValue();
		instrEnum.rendEQUIPtrans[0] = gsETrnX.getValue();
		instrEnum.rendEQUIPtrans[1] = gsETrnY.getValue();
		instrEnum.rendEQUIPtrans[2] = gsETrnZ.getValue();
		instrEnum.rendEQUIPtrans[3] = gsETrnS.getValue();

	}

	private void drawStringRight(String s, int y, int color) {
		this.fontRenderer
				.drawString(s, this.xSize - this.fontRenderer.getStringWidth(s)
						- 12, y, color);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(inventoryTexture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		/*
		 * Draw the player model
		 */
		int k = guiLeft; // (this.width - this.xSize) / 2;
		int l = guiTop;  // (this.height - this.ySize) / 2;
		int center = 45;
		int feet = 245;
		int height = 40;
		drawPlayerModel(k + center, l + 245, height, (float) (k + center) - this.xSize_lo,
				(float) (l + feet - height) - this.ySize_lo, this.mc.thePlayer, gsPitch.getValue(), gsYaw.getValue());
	}

	/**
	 * This renders the player model in standard inventory position
	 */
	public static void drawPlayerModel(int par0, int par1, int par2,
			float par3, float par4, EntityLivingBase par5EntityLivingBase, float pitch, float yaw) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) par0, (float) par1, 50.0F);
		GL11.glScalef((float) (-par2), (float) par2, (float) par2);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		float f2 = par5EntityLivingBase.renderYawOffset;
		float f3 = par5EntityLivingBase.rotationYaw;
		float f4 = par5EntityLivingBase.rotationPitch;
		float f5 = par5EntityLivingBase.prevRotationYawHead;
		float f6 = par5EntityLivingBase.rotationYawHead;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F); 
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-(((float) Math.atan((double) (par4 / 40.0F))) * 20.0F),
				1.0F, 0.0F, 0.0F);
		par5EntityLivingBase.renderYawOffset = (float) Math
				.atan((double) (par3 / 40.0F)) * 20.0F;
		par5EntityLivingBase.rotationYaw = (float) Math
				.atan((double) (par3 / 40.0F)) * 40.0F;
		par5EntityLivingBase.rotationPitch = -((float) Math
				.atan((double) (par4 / 40.0F))) * 20.0F;
		par5EntityLivingBase.rotationYawHead = par5EntityLivingBase.rotationYaw;
		par5EntityLivingBase.prevRotationYawHead = par5EntityLivingBase.rotationYaw;
		GL11.glTranslatef(0.0F, par5EntityLivingBase.yOffset, 0.0F);
		
		GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0f, -.5f, 0f);
		GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0f, .5f, 0f);
		
		RenderManager.instance.playerViewY = 180F;
		RenderManager.instance.renderEntityWithPosYaw(par5EntityLivingBase,
				0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		par5EntityLivingBase.renderYawOffset = f2;
		par5EntityLivingBase.rotationYaw = f3;
		par5EntityLivingBase.rotationPitch = f4;
		par5EntityLivingBase.prevRotationYawHead = f5;
		par5EntityLivingBase.rotationYawHead = f6;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
	}

	/**
	 * Called when the mouse is moved or a mouse button is released. Signature:
	 * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
	 * mouseUp
	 * 
	 * This is a hack. For some reason GuiContainer eats the mouseReleased
	 * events.
	 */
	@Override
	protected void mouseMovedOrUp(int posX, int posY, int which) {
		super.mouseMovedOrUp(posX, posY, which);
		if (which == 0) {
			for (int l = 0; l < this.buttonList.size(); ++l) {
				GuiSlider guislider = (GuiSlider) this.buttonList.get(l);
				guislider.mouseReleased(posX, posY);
			}
		}
	}

	/*
	 * Don't allow the held inventory to be moved from it's slot.
	 */
	@Override
	protected void keyTyped(char par1, int par2) {
		if (Char.char2int(par1) == (this.theInvItemSlot + 49))
			return;
		super.keyTyped(par1, par2);
	}
}
