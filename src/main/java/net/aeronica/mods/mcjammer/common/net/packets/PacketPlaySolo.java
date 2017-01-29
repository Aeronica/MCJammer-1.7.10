package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.gui.GuiPlayingChat;
import net.aeronica.mods.mcjammer.client.mml.MMLManager;
import net.aeronica.mods.mcjammer.common.groups.PlayManager;
import net.aeronica.mods.mcjammer.common.items.InstrumentEnum;
import net.aeronica.mods.mcjammer.common.libs.config.Refs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PacketPlaySolo extends CLLPacket {
	String pktScrollTitle;
	String pktScrollText;
	String pktPlayerName;

	public PacketPlaySolo() {
	}

	public PacketPlaySolo(String playerName) {
		this.pktScrollTitle = "";
		this.pktScrollText = "";
		this.pktPlayerName = playerName;
	}

	public PacketPlaySolo(String title, String scrolltext, String playerName) {
		this.pktScrollTitle = title;
		this.pktScrollText = scrolltext;
		this.pktPlayerName = playerName;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(pktScrollTitle);
		buffer.writeUTF(pktScrollText);
		buffer.writeUTF(pktPlayerName);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		pktScrollTitle = buffer.readUTF();
		pktScrollText = buffer.readUTF();
		pktPlayerName = buffer.readUTF();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		/*
		 * Solo play format "<playerName|groupID>=MML@...;"
		 * 
		 * Jam play format just appends with a space between each player=MML
		 * sequence
		 * "<playername1>=MML@...abcd; <playername2>=MML@...efgh; <playername2>=MML@...efgh;"
		 */
		String mml = new String(pktPlayerName + "=" + pktScrollText);

		// if (GROUPS.getGroupID(pktPlayerName) == null) {
		MMLManager.getMMLManager().mmlPlay(mml, pktPlayerName);

		/*
		 * Only open the playing gui for the player who is playing
		 */
		if (player.getDisplayName().equalsIgnoreCase(pktPlayerName)) {
			player.openGui(MCJammer.instance, GuiPlayingChat.GUI_ID,
					player.worldObj, (int) player.posX, (int) player.posY,
					(int) player.posZ);
		}
		// }
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		/*
		 * Server side send MML to all players
		 */
		int instrumentType;
		System.out.println("+++ Server PacketPlayMusic doAction by "
				+ player.getDisplayName() + " +++");
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
				System.out.println("+++ MML Title: " + title);
				System.out.println("+++ MML = "
						+ mml.substring(0,
								(mml.length() >= 25 ? 25 : mml.length())));

				PacketPlaySolo packetPlaySolo = new PacketPlaySolo(title, mml,
						pktPlayerName);
				/*
				 * TODO: figure out a sane distance to use. Configuration*
				 * option* perhaps.
				 */
				MCJammer.proxy.packetCLL_sendToAllAround(packetPlaySolo,
						player.dimension, player.posX, player.posY,
						player.posZ, Refs.RANGE_ALL_AROUND);

				PlayManager.getPlayManager().setPlaying(pktPlayerName);
				PlayManager.getPlayManager().syncStatus();
			}
		}
	}
}
