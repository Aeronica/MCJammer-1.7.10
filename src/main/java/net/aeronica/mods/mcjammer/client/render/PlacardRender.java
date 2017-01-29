package net.aeronica.mods.mcjammer.client.render;

import net.aeronica.mods.mcjammer.common.entity.PlacardEntity;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class PlacardRender extends Render {

	private static final int PLAC_ICON_SIZE = 18;
	private static final int PLAC_ICON_BASE_U_OFFSET = 0;
	private static final int PLAC_ICON_BASE_V_OFFSET = 165;
	private static final int PLAC_ICONS_PER_ROW = 8;
	private static final int PLAC_TEXTURE_SIZE = 256;

/*	this.drawTexturedModalRect(xPos, yPos, 
 * PLAC_ICON_BASE_U_OFFSET + index % PLAC_ICONS_PER_ROW * PLAC_ICON_SIZE,
   PLAC_ICON_BASE_V_OFFSET + index / PLAC_ICONS_PER_ROW * PLAC_ICON_SIZE, PLAC_ICON_SIZE, PLAC_ICON_SIZE);*/
	
	private static final ResourceLocation placardTextures = new ResourceLocation(
			ModInfo.ID.toLowerCase() + ":" + "textures/gui/manage_group.png");

	public Minecraft mc;
	public RenderManager rm = RenderManager.instance;

	public PlacardRender() {
		this.mc = Minecraft.getMinecraft();
		this.shadowSize = 0F;
		this.shadowOpaque = 0F;
	}

	/**
	 * Renders the placard based on vanilla RenderXPOrb and renderLivingLabel
	 */
	public void renderThePlacard(PlacardEntity placardEnt, double x,
			double y, double z, float entityYaw, float partialTicks) {

		bindEntityTexture(placardEnt);
		int index = (int) placardEnt.getPlacardIndex();
		float f2 = (float) (PLAC_ICON_BASE_U_OFFSET + index % PLAC_ICONS_PER_ROW * PLAC_ICON_SIZE) / PLAC_TEXTURE_SIZE;
		float f3 = (float) (PLAC_ICON_BASE_U_OFFSET + index % PLAC_ICONS_PER_ROW * PLAC_ICON_SIZE + PLAC_ICON_SIZE) / PLAC_TEXTURE_SIZE;
		float f4 = (float) (PLAC_ICON_BASE_V_OFFSET + index / PLAC_ICONS_PER_ROW * PLAC_ICON_SIZE) / PLAC_TEXTURE_SIZE;
		float f5 = (float) (PLAC_ICON_BASE_V_OFFSET + index / PLAC_ICONS_PER_ROW * PLAC_ICON_SIZE + PLAC_ICON_SIZE) / PLAC_TEXTURE_SIZE;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

		GL11.glRotatef(180F - rm.playerViewY, 0.0F, 1.0F, 0.0F);
		float viewX = mc.gameSettings.thirdPersonView <= 1 ? -rm.playerViewX
				: rm.playerViewX;
		GL11.glRotatef(viewX, 1.0F, 0.0F, 0.0F);

		float f11 = 0.5F;
		GL11.glScalef(f11, f11, f11);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL); // XXX
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA(255, 255, 255, 255);
		tessellator.setNormal(0.0F, 1.0F, 0.0F);

		double w = placardEnt.width;

/*		// render facing player
		tessellator.addVertexWithUV(-w, 0, 0, 0, 1); // 1
		tessellator.addVertexWithUV(w, 0, 0, 1, 1); // 2
		tessellator.addVertexWithUV(w, 1, 0, 1, 0); // 3
		tessellator.addVertexWithUV(-w, 1, 0, 0, 0); // 4
*/
		// render facing player
		tessellator.addVertexWithUV(-w, 0, 0, (double)f2, (double)f5); // 1
		tessellator.addVertexWithUV(w, 0, 0,  (double)f3, (double)f5); // 2
		tessellator.addVertexWithUV(w, 1, 0,  (double)f3, (double)f4); // 3
		tessellator.addVertexWithUV(-w, 1, 0, (double)f2, (double)f4); // 4

		
		tessellator.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL); // XXX
		GL11.glDisable(GL11.GL_BLEND);
		// GL11.glDepthMask(true);
		// GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getPlacardEntityTextures(
			PlacardEntity par1EntityXPOrb) {
		return placardTextures;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getPlacardEntityTextures((PlacardEntity) par1Entity);
	}

	@Override
	protected void bindEntityTexture(Entity par1Entity) {
		this.bindTexture(this.getEntityTexture(par1Entity));
	}

	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1) {
		this.renderThePlacard((PlacardEntity) entity, d0, d1, d2, f, f1);
	}
}
