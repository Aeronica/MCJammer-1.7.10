package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.client.mml.MMLManager;
import net.minecraft.entity.player.EntityPlayer;

public class PacketPlayJam extends CLLPacket {
	String jamMML;
	String groupID;

	public PacketPlayJam() {
	}

	public PacketPlayJam(String jamMML, String groupID) {
		this.jamMML = jamMML;
		this.groupID = groupID;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(jamMML);
		buffer.writeUTF(groupID);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		jamMML = buffer.readUTF();
		groupID = buffer.readUTF();
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
		MMLManager.getMMLManager().mmlPlay(jamMML, groupID);
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
	}
}
