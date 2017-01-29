package net.aeronica.mods.mcjammer.client.core;



import net.aeronica.mods.mcjammer.client.gui.GuiJamOverlay;
import net.aeronica.mods.mcjammer.client.mml.MMLManager;
import net.aeronica.mods.mcjammer.client.render.ItemRenderInstrument;
import net.aeronica.mods.mcjammer.client.render.PlacardRender;
import net.aeronica.mods.mcjammer.common.core.CommonProxy;
import net.aeronica.mods.mcjammer.common.entity.PlacardEntity;
import net.aeronica.mods.mcjammer.common.items.MusicItems;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {

	public void preInits() {
		ModLogger.logInfo("ClientProxy.preInits");
		MinecraftForge.EVENT_BUS.register(new CLEventHandler());
	}

	public void postInits() {
		ModLogger.logInfo("ClientProxy.postInits");
		MinecraftForge.EVENT_BUS.register(new GuiJamOverlay(Minecraft
				.getMinecraft()));
	}

	public void registerEntities() {
		ModLogger.logInfo("ClientProxy.registerEntities");
		RenderingRegistry.registerEntityRenderingHandler(PlacardEntity.class,
				new PlacardRender());
	}

	@Override
	public void registerRenderers() {
		ModLogger.logInfo("ClientProxy.registerRenderers");
		MinecraftForgeClient.registerItemRenderer(MusicItems.instruments, new ItemRenderInstrument() );
	}
	
	@Override
	public void registerKeyBinding() {
		ModLogger.logInfo("ClientProxy.registerKeyBinding");
		FMLCommonHandler.instance().bus().register(new KeyHandler());
	}


	@Override
	public void registerSounds() {
		ModLogger.logInfo("ClientProxy.registerSounds");
		MMLManager.getMMLManager().mmlInit();
	}
}
