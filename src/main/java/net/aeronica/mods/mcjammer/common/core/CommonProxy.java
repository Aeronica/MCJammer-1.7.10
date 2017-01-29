package net.aeronica.mods.mcjammer.common.core;

import java.util.EnumMap;

import net.aeronica.mods.mcjammer.client.gui.GuiGroup;
import net.aeronica.mods.mcjammer.client.gui.GuiGroupJoin;
import net.aeronica.mods.mcjammer.client.gui.GuiInstInvAdjustRotations;
import net.aeronica.mods.mcjammer.client.gui.GuiInstrumentInventory;
import net.aeronica.mods.mcjammer.client.gui.GuiMusicBook;
import net.aeronica.mods.mcjammer.client.gui.GuiPlaying;
import net.aeronica.mods.mcjammer.client.gui.GuiPlayingChat;
import net.aeronica.mods.mcjammer.common.inventory.ContainerInstrument;
import net.aeronica.mods.mcjammer.common.inventory.InventoryInstrument;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.aeronica.mods.mcjammer.common.net.CLLPacketHandler;
import net.aeronica.mods.mcjammer.common.net.packets.CLLPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		switch (ID) {
		case GuiInstrumentInventory.GUI_ID:
			// Use the player's held item to create the inventory
			return new ContainerInstrument(player, player.inventory,
					new InventoryInstrument(player.getHeldItem()));
			
		case GuiInstInvAdjustRotations.GUI_ID:
			// Use the player's held item to create the inventory
			return new ContainerInstrument(player, player.inventory,
					new InventoryInstrument(player.getHeldItem()));

		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		switch (ID) {
		case GuiMusicBook.GUI_ID:
			return new GuiMusicBook();

		case GuiInstrumentInventory.GUI_ID:
			return new GuiInstrumentInventory(
					(ContainerInstrument) new ContainerInstrument(player,
							player.inventory, new InventoryInstrument(
									player.getHeldItem())));
			
		case GuiInstInvAdjustRotations.GUI_ID:
			return new GuiInstInvAdjustRotations(
					(ContainerInstrument) new ContainerInstrument(player,
							player.inventory, new InventoryInstrument(
									player.getHeldItem())));


		case GuiGroup.GUI_ID:
			return new GuiGroup();

		case GuiPlaying.GUI_ID:
			return new GuiPlaying();
			
		case GuiPlayingChat.GUI_ID:
			return new GuiPlayingChat();
			
		case GuiGroupJoin.GUI_ID:
			return new GuiGroupJoin();

		default:
			return null;
		}
	}

	public void preInits() {
		ModLogger.logInfo("CommonProxy.preInits");
	}

	public void postInits() {
		ModLogger.logInfo("CommonProxy.postInits");
	}

	private static EnumMap<Side, FMLEmbeddedChannel> cllChannel;

	/**
	 * Adapted from <a href=
	 * 'http://www.minecraftforge.net/wiki/Netty_Packet_Handling'>Minecraft
	 * Forge Wiki</a>
	 */

	public void initializePacketHandling() {
		ModLogger.logInfo("CommonProxy.initializePacketHandling");
		cllChannel = NetworkRegistry.INSTANCE.newChannel(ModInfo.CHANNEL,
				new CLLPacketHandler());
		CLLPacketHandler.registerPackets();
	}

	/**
	 * Adapted from <a href=
	 * 'http://www.minecraftforge.net/wiki/Netty_Packet_Handling'>Minecraft
	 * Forge Wiki</a>
	 */

	public void packetCLL_sendToAll(CLLPacket packet) {
		cllChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.ALL);
		cllChannel.get(Side.SERVER).writeAndFlush(packet);
	}

	/**
	 * Adapted from <a href=
	 * 'http://www.minecraftforge.net/wiki/Netty_Packet_Handling'>Minecraft
	 * Forge Wiki</a>
	 */

	public void packetCLL_sendToAllAround(CLLPacket packet, int dimension,
			double x, double y, double z, double range) {
		TargetPoint target = new TargetPoint(dimension, x, y, z, range);
		cllChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		cllChannel.get(Side.SERVER)
				.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(target);
		cllChannel.get(Side.SERVER).writeAndFlush(packet);
	}

	/**
	 * Adapted from <a href=
	 * 'http://www.minecraftforge.net/wiki/Netty_Packet_Handling'>Minecraft
	 * Forge Wiki</a>
	 */

	public void packetCLL_sendToPlayer(CLLPacket packet, EntityPlayerMP player) {
		cllChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.PLAYER);
		cllChannel.get(Side.SERVER)
				.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		cllChannel.get(Side.SERVER).writeAndFlush(packet);
	}

	/**
	 * Adapted from <a href=
	 * 'http://www.minecraftforge.net/wiki/Netty_Packet_Handling'>Minecraft
	 * Forge Wiki</a>
	 */

	public void packetCLL_sendToServer(CLLPacket packet) {
		cllChannel.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET)
				.set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		cllChannel.get(Side.CLIENT).writeAndFlush(packet);
	}

	public void registerRenderers() {
		ModLogger.logInfo("CommonProxy.registerRenderers");
	}

	public void registerKeyBinding() {
		ModLogger.logInfo("CommonProxy.registerKeyBinding");
	}

	public void registerEntities() {
		ModLogger.logInfo("CommonProxy.registerEntities");
	}

	public void registerSounds() {
		ModLogger.logInfo("CommonProxy.registerSounds");
	}
}
