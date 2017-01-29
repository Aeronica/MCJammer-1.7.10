package net.aeronica.mods.mcjammer.common.player;

import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * Tracks all audience members and musicians and adds appropriate meta data to
 * aid in the management of of their listening experience.
 * 
 * Provides:
 * <ul>
 * <li>Packet wrappers to limit packet spam.</li>
 * <li>Respect individual user listening preferences.</li>
 * <li>Set and control audio levels v.s. distance.</li>
 * <li>Manage the starting and stopping of the client MML Player.</li>
 * </ul>
 * 
 * @author Aeronica
 * 
 */
public class AudienceManager {

	/*
	 * FML Gaming Events
	 */
	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
		event.player.addChatMessage(new ChatComponentText(
				"[AudienceManager] Hi There!"));
		ModLogger.logInfo("AudienceManager.PlayerLoggedInEvent: "
				+ event.player.getDisplayName());

	}

	@SubscribeEvent
	public void onPlayerLoggedOutEvent(PlayerLoggedOutEvent event) {
		event.player.addChatMessage(new ChatComponentText(
				"[AudienceManager] Goodbye"));
		ModLogger.logInfo("AudienceManager.PlayerLoggedOutEvent: "
				+ event.player.getDisplayName());
	}

	@SubscribeEvent
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		ModLogger.logInfo("AudienceManager.PlayerRespawnEvent: "
				+ event.player.getDisplayName());
	}

	@SubscribeEvent
	public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
		ModLogger.logInfo("AudienceManager.PlayerChangedDimensionEvent: "
				+ event.player.getDisplayName());
	}

	static long count = 0;

	// @SubscribeEvent
	public void onPlayerTickEvent(PlayerTickEvent event) {

		PlayerTickEvent e = event;
		while ((count++ > 99) && (e.side.isServer()) && (e.phase.equals(TickEvent.Phase.START))) {
			count = 0;
			ModLogger.logInfo("AudienceManager.onPlayerTickEvent: "
					+ e.player.getDisplayName());
			ModLogger.logInfo("   side." + e.side + ", phase."
					+ e.phase + ", type." + e.type);
			ModLogger.logInfo("   posX=" + event.player.posX + ", posY="
					+ e.player.posY + ", posZ=" + e.player.posZ);
		}
	}
}
