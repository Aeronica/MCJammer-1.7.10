package net.aeronica.mods.mcjammer;

import net.aeronica.mods.mcjammer.common.core.CommonProxy;
import net.aeronica.mods.mcjammer.common.core.SREventHandler;
import net.aeronica.mods.mcjammer.common.groups.GroupManager;
import net.aeronica.mods.mcjammer.common.groups.PlayManager;
import net.aeronica.mods.mcjammer.common.items.MusicItems;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.aeronica.mods.mcjammer.common.libs.MusicTab;
import net.aeronica.mods.mcjammer.common.libs.config.ConfigHandler;
import net.aeronica.mods.mcjammer.common.net.CLLPacketHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(
	modid = ModInfo.ID,
	name = ModInfo.NAME,
	version = ModInfo.VERSION
	)

public class MCJammer {
	@Instance(ModInfo.ID)
	public static MCJammer instance;
	public static final GroupManager GM = GroupManager.getGroupManager();
	public static final PlayManager PM = PlayManager.getPlayManager();

	@SidedProxy(
			clientSide = ModInfo.CLIENT_PROXY + "ClientProxy",
			serverSide = ModInfo.COMMON_PROXY + "CommonProxy"
			)
	
	public static CommonProxy proxy;
	public static final CLLPacketHandler PACKET_HANDLER = new CLLPacketHandler();
	public static final CreativeTabs TAB_MUSIC = new MusicTab(CreativeTabs.getNextID(), ModInfo.NAME);

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		ModLogger.initLogging();
		ModLogger.logInfo("preInit");
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		MusicItems.initItems();
		
		MinecraftForge.EVENT_BUS.register(GM); // GroupManager instance
		MinecraftForge.EVENT_BUS.register(new SREventHandler());

		FMLCommonHandler.instance().bus().register(GM); // GroupManager instance
		FMLCommonHandler.instance().bus().register(new SREventHandler());
		proxy.preInits();
	}

	@EventHandler
	public static void init(FMLInitializationEvent event) {
		ModLogger.logInfo("init");
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.initializePacketHandling();
		proxy.registerRenderers();
		proxy.registerKeyBinding();
		proxy.registerEntities();
		proxy.registerSounds();	
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		ModLogger.logInfo("postInit");
		proxy.postInits();
	}
	
	// What can I do with this?  Hmmm, deal with items/blocks I removed/changed on purpose.  Okay for dev, not production.
// 	@EventHandler
//	public void onFMLMissingMappingsEvent(FMLMissingMappingsEvent event) {
//		List<MissingMapping> lmm = event.get();
//		for (MissingMapping mm : lmm ) {
//			ModLogger.logInfo("MissingMapping: " + mm.name + ", Type: " + mm.type);
//			mm.setAction(Action.WARN);
//		}
//	}
}
