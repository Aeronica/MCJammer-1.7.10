package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.minecraft.entity.player.EntityPlayer;

public class PacketGroupSync extends CLLPacket {

	private String groups, members;

	public PacketGroupSync() {
	}

	public PacketGroupSync(String cGroups, String cMembers) {
		this.groups = cGroups;
		this.members = cMembers;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(groups);
		buffer.writeUTF(members);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		groups = buffer.readUTF();
		members = buffer.readUTF();
	}

	public void handlePacket(EntityPlayer player) {
		// Client side - build - replace the groups and members hashes
		ModLogger.debug("+++Server PacketGroupSync doAction on behalf of "
				+ player.getDisplayName() + " +++");
		ModLogger.debug("  +++ Groups:  " + groups);
		ModLogger.debug("  +++ members: " + members);

		GROUPS.clientGroups = GROUPS.splitToHashMap(groups);
		GROUPS.clientMembers = GROUPS.splitToHashMap(members);
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
		handlePacket(playerSP);
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
	}
}
