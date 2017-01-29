package net.aeronica.mods.mcjammer.common.core;

import net.aeronica.mods.mcjammer.common.groups.GroupManager;
import net.aeronica.mods.mcjammer.common.inventory.IInstrument;
import net.aeronica.mods.mcjammer.common.player.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SREventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		/*
		 * Be sure to check if the entity being constructed is the correct type
		 * for the extended properties you're about to add! The null check is
		 * used it to make sure properties are only registered once per entity
		 */
		if (event.entity instanceof EntityPlayer
				&& ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
			// This is how extended properties are registered using our
			// convenient method from earlier
			ExtendedPlayer.register((EntityPlayer) event.entity);
		}
		// That will call the constructor as well as cause the init() method
		// to be called automatically

		if (event.entity instanceof EntityPlayer
				&& event.entity
						.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME) == null) {
			event.entity.registerExtendedProperties(
					ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(
							(EntityPlayer) event.entity));
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		/*
		 * Only need to synchronize when the world is remote (i.e. we're on the
		 * server side) and only for player entities, as that's what we need for
		 * the GuiJamOverlay
		 */
		if (!event.entity.worldObj.isRemote
				&& event.entity instanceof EntityPlayer) {
			ExtendedPlayer.get((EntityPlayer) event.entity).clearAll();
			GroupManager.sync();
		}
	}

	// TODO: This can prevent a player from moving! hehe
	// @SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event) {

		if (event.isCancelable() && event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			if (player.getHeldItem().getItem() != null
					&& player.getHeldItem().getItem() instanceof IInstrument
					&& player.getHeldItem().getItem().getMaxDamage() > 0)
				event.setCanceled(true);
		}
	}
}