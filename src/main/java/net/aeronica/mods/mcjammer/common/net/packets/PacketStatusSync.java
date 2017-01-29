package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.minecraft.entity.player.EntityPlayer;

public class PacketStatusSync extends CLLPacket {
	String status;

	public PacketStatusSync() {
	}

	public PacketStatusSync(String status) {
		this.status = status;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(status);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		status = buffer.readUTF();
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
		GROUPS.clientPlayStatuses = GROUPS.splitToHashMap(status);
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
	}
}
