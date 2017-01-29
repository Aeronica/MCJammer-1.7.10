package net.aeronica.mods.mcjammer.client.core;

import net.aeronica.mods.mcjammer.client.render.PlacardRender;
import net.aeronica.mods.mcjammer.common.entity.PlacardEntity;
import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.player.ExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CLEventHandler {

	static int count0, count1, count2, count3;
	Minecraft mc = Minecraft.getMinecraft();

	// @SubscribeEvent
	public void onRenderPlayerEventPre(RenderPlayerEvent.Pre event) {
		// make all players invisible
		if (event.isCancelable())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onRenderPlayerEvent(RenderPlayerEvent.Post event) {

		renderPlacards(event.entityPlayer, event.partialRenderTick);
	}

	/**
	 * 
	 * @param player
	 * @param renderTick
	 */
	private void renderPlacards(EntityPlayer player, float renderTick) {
		ExtendedPlayer props = ExtendedPlayer.get(mc.thePlayer);

		if (GROUPS.clientMembers != null && !(props.getInInventory() && mc.thePlayer.equals(player))
				&& GROUPS.clientMembers.containsKey(player.getDisplayName())
				&& !(mc.gameSettings.thirdPersonView == 0 && mc.thePlayer.equals(player))) {
			
			PlacardEntity pE = new PlacardEntity(player.worldObj, player, renderTick);
			pE.setPlacard(GROUPS.getIndex(player.getDisplayName()));
			PlacardRender render = new PlacardRender();
			render.rm.renderEntitySimple(pE, renderTick);
		}
	}
}
