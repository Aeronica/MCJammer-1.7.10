package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.groups.GroupManager;
import net.minecraft.entity.player.EntityPlayer;

public class PacketGroupManage extends CLLPacket {
	String operation;
	String groupID;
	String memberName;

	public PacketGroupManage() {}

	/*
	 * PacketGroupManage packet = new PacketGroupManage(operation, groupID,
	 * memberName);
	 * PacketDispatcher.sendPacketToServer(PacketUtil.buildPacket(packet));
	 * 
	 * @param operation
	 * 
	 * @param groupID
	 * 
	 * @param memberName
	 */
	public PacketGroupManage(String operation, String groupID, String memberName) {
		this.operation = operation;
		this.groupID = groupID;
		this.memberName = memberName;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(operation);
		buffer.writeUTF(groupID);
		buffer.writeUTF(memberName);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		operation = buffer.readUTF();
		groupID = buffer.readUTF();
		memberName = buffer.readUTF();
	}

	public void handlePacket(EntityPlayer player) {
		System.out.println("+++ GroupManage doAction: " + operation);
		switch (GROUPS.valueOf(operation)) {
		case GROUP_ADD:
			GroupManager.addGroup(memberName);
			break;
		case MEMBER_ADD:
			GroupManager.addMember(groupID, memberName);
			break;
		case MEMBER_REMOVE:
			GroupManager.removeMember(memberName);
			break;
		case MEMBER_PROMOTE:
			GroupManager.setLeader(memberName);
			break;
		default:
		}
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
	}
	
	@Override
	public void handleServerSide(EntityPlayer playerMP) {
		handlePacket(playerMP);
	}
}
