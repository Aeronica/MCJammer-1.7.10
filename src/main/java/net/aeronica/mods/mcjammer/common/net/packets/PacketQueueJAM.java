package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.gui.GuiPlayingChat;
import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.groups.GroupManager;
import net.aeronica.mods.mcjammer.common.groups.PlayManager;
import net.aeronica.mods.mcjammer.common.items.InstrumentEnum;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.aeronica.mods.mcjammer.common.libs.config.Refs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PacketQueueJAM extends CLLPacket {
	String pktScrollTitle;
	String pktScrollText;
	GroupManager GM = GroupManager.getGroupManager();

	public PacketQueueJAM() {
		this.pktScrollTitle = "";
		this.pktScrollText = "";
	}

	public PacketQueueJAM(String title, String scrolltext) {
		this.pktScrollTitle = title;
		this.pktScrollText = scrolltext;

	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(pktScrollTitle);
		buffer.writeUTF(pktScrollText);

	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		pktScrollTitle = buffer.readUTF();
		pktScrollText = buffer.readUTF();

	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		/*
		 * Client side - Open GuiPlaying for JAM Members
		 */
		EntityClientPlayerMP thePlayer = Minecraft.getMinecraft().thePlayer;

		/*
		 * Open the Play GUI on for the JAMMER who either queued his part or the
		 * leader who kicks off the JAM
		 */
		if (GROUPS.getMembersGroupID(player.getDisplayName()) != null) {

			if (thePlayer.getDisplayName().equalsIgnoreCase(
					player.getDisplayName())) {
				ModLogger
						.logInfo("PacketQueueJAM Client Side Packet - Open GuiPLaying!");
				thePlayer.openGui(MCJammer.instance, GuiPlayingChat.GUI_ID,
						thePlayer.worldObj, (int) thePlayer.posX,
						(int) thePlayer.posY, (int) thePlayer.posZ);
			}
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		/*
		 * Server side - Queue JAM and Send it to ALL Players in Range
		 */
		int instrumentType;
		ModLogger.logInfo("PacketQueueJAM: " + player.getDisplayName());
		NBTTagList items = player.getCurrentEquippedItem().stackTagCompound
				.getTagList("ItemInventory", 10);
		if (items.tagCount() == 1) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(0);
			ItemStack isMusicBook = ItemStack.loadItemStackFromNBT(item);
			NBTTagCompound contents = (NBTTagCompound) isMusicBook.stackTagCompound
					.getTag("MusicBook");
			if (contents != null) {
				String title = isMusicBook.getDisplayName();
				String mml = contents.getString("MML");

				InstrumentEnum InsEn = InstrumentEnum.values()[player
						.getCurrentEquippedItem().getItemDamage()];
				instrumentType = InsEn.MidiPatchID + 1;

				mml = mml.replace("MML@", "MML@I" + instrumentType);
				ModLogger.logInfo("JAM Title: " + title);
				ModLogger.logInfo("JAM MML = "
						+ mml.substring(0,
								(mml.length() >= 25 ? 25 : mml.length())));

				String groupID = GM.getMembersGroupID(player.getDisplayName());
				/*
				 * Queue members parts
				 */
				PlayManager.getPlayManager().queue(groupID,
						player.getDisplayName(), mml);
				PacketQueueJAM packetPlayJAM = new PacketQueueJAM("queue",
						"only");
				MCJammer.proxy.packetCLL_sendToPlayer(packetPlayJAM,
						(EntityPlayerMP) player);

				/*
				 * Only send the groups MML when the leader starts the JAM
				 */
				if (GM.isLeader(player.getDisplayName())) {
					mml = PlayManager.getPlayManager().getMML(groupID);
					PacketPlayJam packetPlayJam = new PacketPlayJam(mml,
							groupID);
					/*
					 * TODO: figure out a sane distance to use. Configuration*
					 * option* perhaps.
					 */
					MCJammer.proxy.packetCLL_sendToAllAround(packetPlayJam,
							player.dimension, player.posX, player.posY,
							player.posZ, Refs.RANGE_ALL_AROUND);
					PlayManager.getPlayManager().syncStatus();
				}
			}
		}
	}
}
