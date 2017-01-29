package net.aeronica.mods.mcjammer.common.libs;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Copied from Combustible Lemon Launcher
 * @author Phil Julian (aka iBuilder99)
 */

public class ModLogger {
	
	private static Logger modLogger;
	
	public static void initLogging(){
		modLogger = LogManager.getLogger(ModInfo.ID);
	}
	
	public static void logInfo(String msg){
		modLogger.log(Level.INFO, msg);
	}
	
	public static void logWarning(String msg){
		modLogger.log(Level.WARN, msg);
	}
	
	public static void logError(String msg){
		modLogger.log(Level.ERROR, msg);
	}
	
	public static void debug(String msg){
		modLogger.log(Level.DEBUG, msg);
	}
}
