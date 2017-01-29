package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PacketMusicScroll extends CLLPacket {

	String scrollTitle;
	String scrollText;

	public PacketMusicScroll() {
	}

	public PacketMusicScroll(String title, String mmlText) {
		scrollTitle = title;
		scrollText = mmlText;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(scrollTitle);
		buffer.writeUTF(scrollText);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		scrollTitle = buffer.readUTF();
		scrollText = buffer.readUTF();
	}
	
	public void handlepacket(EntityPlayer player) {
		String mml = new String(scrollText).trim().toUpperCase();

		if (player.getCurrentEquippedItem() != null) {
			ItemStack musicbook = player.getCurrentEquippedItem();
			if (!musicbook.hasDisplayName()) {
				System.out.println("+++ Write Music book NBT +++");
				musicbook.setStackDisplayName("-" + scrollTitle + "-");
				NBTTagCompound compound = musicbook.getTagCompound();
				if (compound != null) {
					NBTTagCompound contents = new NBTTagCompound();
					contents.setString("MML", mml);
					compound.setTag("MusicBook", contents);
				}
			}
		}
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
		handlepacket(playerMP);
	}
}
