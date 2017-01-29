package net.aeronica.mods.mcjammer.client.render;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;
import net.aeronica.mods.mcjammer.common.items.InstrumentEnum;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;
import cpw.mods.fml.client.FMLClientHandler;

public class ItemRenderInstrument implements IItemRenderer {

	static private IModelCustom[] model = new IModelCustom[InstrumentEnum.values().length];
	static private ResourceLocation[] ModelTexture = new ResourceLocation[InstrumentEnum.values().length];
	static private ResourceLocation[] ModelOBJwf = new ResourceLocation[InstrumentEnum.values().length];
	private int subtype;

	public ItemRenderInstrument() {
		for (int i = 0; i < InstrumentEnum.values().length; i++) {
			InstrumentEnum InsEn = InstrumentEnum.values()[i];
			ModelTexture[i] = new ResourceLocation(InsEn.RenderTEX);
			ModelOBJwf[i] = new ResourceLocation(InsEn.RenderOBJ);
			model[i] = ModelInstrument(ModelOBJwf[i]);
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		this.subtype = item.getItemDamage();
		InstrumentEnum instrEnum = InstrumentEnum.values()[this.subtype];
		float[] rots1;
		float[] trans;
		switch (type) {
		case ENTITY:
			glPushMatrix();
			renderInstrument(0.0, 0.1, 0.0, 0.2F);
			break;
		case EQUIPPED:
			glPushMatrix();
			rots1 = instrEnum.rendEQUIProt1;
			trans = instrEnum.rendEQUIPtrans;
			glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
			glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
			glTranslated(trans[0], trans[1], trans[2]);
			glScalef(trans[3], trans[3], trans[3]);

			glRotatef(rots1[0], 1f, 0f, 0f);
			glRotatef(rots1[1], 0f, 1f, 0f);
			glRotatef(rots1[2], 0f, 0f, 1f);
			renderInstrument(0f, 0f, 0f, 1f);

			break;
		case EQUIPPED_FIRST_PERSON:
			glPushMatrix();
			glRotatef(50.0F, 0.0F, 1.0F, 0.0F);

			renderInstrument(-0.5, 0.8, 0.8, 0.4F);
			break;
		case INVENTORY:
			trans = instrEnum.rendINVtrans;
			glPushMatrix();
			glTranslated(trans[0], trans[1], trans[2]);
			glScalef(trans[3], trans[3], trans[3]);
			glRotatef(-60.0F, 1.0F, 0.0F, 0.0F);
			glRotatef(-100.0F, 0.0F, 1.0F, 0.0F);

			renderInstrument(0f, 0f, 0f, 1f);
			// renderInstrument(-0.5, -0.5, -0.5, 0.35F);
			break;
		default:
			break;
		}
	}

	private void renderInstrument(double x, double y, double z, float scale) {
		if (ModelTexture[this.subtype] != null) {
			glTranslated(x, y, z);
			glScalef(scale, scale, scale);
			FMLClientHandler.instance().getClient().renderEngine
					.bindTexture(ModelTexture[this.subtype]);
			model[this.subtype].renderAll();
			glPopMatrix();
		}

	}

	private IModelCustom ModelInstrument(ResourceLocation rloc) {
		IModelCustom model = null;
		ModLogger.logInfo("ItemRenderInstrument.ModelInstrument creation TRY: "
				+ rloc.getResourceDomain() + " * " + rloc.getResourcePath());
		try {
			model = AdvancedModelLoader.loadModel(rloc);
		} catch (IllegalArgumentException e) {
			System.out.println("Model Illegal Argument");
		} catch (ModelFormatException e) {
			System.out.println("Model Format Exception");
		}
		return model;
	}

}
