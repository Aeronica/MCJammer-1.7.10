package net.aeronica.mods.mcjammer.common.net.packets;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.gui.GuiGroup;
import net.aeronica.mods.mcjammer.common.inventory.IInstrument;
import net.aeronica.mods.mcjammer.common.items.ItemInstrument;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class PacketKey extends CLLPacket {

	public String keyBindingDesc;

	public PacketKey() {
	}

	public PacketKey(String kb) {
		keyBindingDesc = kb;
	}

	@Override
	public void writeDataTo(ByteBufOutputStream buffer) throws IOException {
		buffer.writeUTF(keyBindingDesc);
	}

	@Override
	public void readDataFrom(ByteBufInputStream buffer) throws IOException {
		keyBindingDesc = buffer.readUTF();
	}

	@Override
	public void handleClientSide(EntityPlayer playerSP) {
		ModLogger.logInfo("PacketGroupGUI.handlePacket keyBindingDesc: "
				+ keyBindingDesc);
		if (keyBindingDesc.equalsIgnoreCase("key.openParty")) {

			playerSP.openGui(MCJammer.instance, GuiGroup.GUI_ID,
					playerSP.worldObj, (int) playerSP.posX,
					(int) playerSP.posY, (int) playerSP.posZ);
		}
		if (keyBindingDesc.equalsIgnoreCase("key.playInstrument")) {
			ItemStack is = playerSP.getHeldItem();
			if (is != null && is.getItem() instanceof IInstrument) {
				ItemInstrument im = (ItemInstrument) is.getItem();
				im.onKeyPressed(keyBindingDesc, playerSP, is);
			}
		}
	}

	@Override
	public void handleServerSide(EntityPlayer playerMP) {
		PacketKey packetKey = new PacketKey(keyBindingDesc);
		MCJammer.proxy.packetCLL_sendToPlayer(packetKey,
				(EntityPlayerMP) playerMP);
	}
}