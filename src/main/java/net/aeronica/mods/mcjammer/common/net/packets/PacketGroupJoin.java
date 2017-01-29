package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.gui.GuiGroupJoin;
import net.minecraft.entity.player.EntityPlayer;

public class PacketGroupJoin extends CLLPacket {

	String groupID;

	public PacketGroupJoin() {
	}

	public PacketGroupJoin(String groupID) {
		this.groupID = groupID;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(groupID);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		groupID = buffer.readUTF();
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
		playerSP.openGui(MCJammer.instance, GuiGroupJoin.GUI_ID,
				playerSP.worldObj, (int) playerSP.posX, (int) playerSP.posY,
				(int) playerSP.posZ);
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
	}
}
