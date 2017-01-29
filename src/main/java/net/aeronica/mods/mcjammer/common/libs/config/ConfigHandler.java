package net.aeronica.mods.mcjammer.common.libs.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	@SuppressWarnings("static-access")
	public static void init(File configFile) {
		Configuration config = new Configuration(configFile);

		config.addCustomCategoryComment(config.CATEGORY_GENERAL, "MCJammer Config");
		config.load();

		Params.mcjGMMaping_actual = config.get(config.CATEGORY_GENERAL, Params.mcjGMMaping_desc,
				Params.mcjGMMaping_default).getString();

		Params.initGMMap();

		config.save();
	}
}
