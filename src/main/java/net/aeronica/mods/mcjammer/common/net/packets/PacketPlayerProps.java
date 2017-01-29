package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.common.player.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class PacketPlayerProps extends CLLPacket {
	private boolean isLeader, inJam, isPlaying, inInventory;
	private String sParam1, sParam2, sParam3;

	public PacketPlayerProps() {
	}

	public PacketPlayerProps(boolean inJam, boolean isLeader, boolean isPlaying, boolean inInventory ,String sParam1, String sParam2, String sParam3) {
		this.inJam = inJam;
		this.isLeader = isLeader;
		this.isPlaying = isPlaying;
		this.inInventory = inInventory;
		this.sParam1 = sParam1;
		this.sParam2 = sParam2;
		this.sParam3 = sParam3;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeBoolean(inJam);
		buffer.writeBoolean(isLeader);
		buffer.writeBoolean(isPlaying);
		buffer.writeBoolean(inInventory);
		buffer.writeUTF(sParam1);
		buffer.writeUTF(sParam2);
		buffer.writeUTF(sParam3);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		inJam = buffer.readBoolean();
		isLeader = buffer.readBoolean();
		isPlaying = buffer.readBoolean();
		inInventory = buffer.readBoolean();
		sParam1 = buffer.readUTF();
		sParam2 = buffer.readUTF();
		sParam3 = buffer.readUTF();
	}

	public void handlePacket(EntityPlayer player) {

		if (player.worldObj.isRemote) {
			// We only want the receive on client side
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) player);
			if (props != null) {
				props.setJam(this.inJam);
				props.setLeader(this.isLeader);
				props.setPlaying(this.isPlaying);
				props.setInInventory(inInventory);
				props.setSParams(this.sParam1, this.sParam2, this.sParam3);
			}
		}
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
		handlePacket(playerSP);
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
	}
}
