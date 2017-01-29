package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.mml.MMLManager;
import net.aeronica.mods.mcjammer.common.groups.PlayManager;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class PacketPlayStop extends CLLPacket {

	private String playID;

	public PacketPlayStop() {
	}

	public PacketPlayStop(String playID) {
		this.playID = playID;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(playID);

	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		playID = buffer.readUTF();

	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
		MMLManager.getMMLManager().mmlKill(playID);
		if (Minecraft.getMinecraft().thePlayer.getDisplayName().equalsIgnoreCase(playID)) {
			ModLogger.logInfo("PacketPlayStop: try to close Gui for " + playID);
			Minecraft mc = Minecraft.getMinecraft();
			// close the play gui
			mc.displayGuiScreen((GuiScreen) null);
			mc.setIngameFocus();
		}
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
		PlayManager.getPlayManager().dequeueMember(playID);
		PacketPlayStop packetPlayStop = new PacketPlayStop(playID);
		MCJammer.proxy.packetCLL_sendToAll(packetPlayStop);
	}

}
