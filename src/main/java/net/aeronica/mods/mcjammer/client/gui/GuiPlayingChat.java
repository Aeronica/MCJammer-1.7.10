package net.aeronica.mods.mcjammer.client.gui;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlayStop;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlayingChat extends GuiChat {
	public static final int GUI_ID = 4;
	
	public GuiPlayingChat() {
	}

	public GuiPlayingChat(String par1Str) {
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat
	 * events
	 */
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		this.mc.ingameGUI.getChatGUI().resetScroll();
		sendStop();
	}


	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 == Keyboard.KEY_TAB) {
			this.func_146404_p_();
		} else {
		}

		/*
		 * We will ESCAPE to quit, not ENTER
		 */
		if (par2 == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen((GuiScreen) null);
		} else if (par2 != 28 && par2 != 156) {
			if (par2 == 200) {
				this.getSentHistory(-1);
			} else if (par2 == 208) {
				this.getSentHistory(1);
			} else if (par2 == 201) {
				this.mc.ingameGUI.getChatGUI().scroll(
						this.mc.ingameGUI.getChatGUI().func_146232_i() - 1);
			} else if (par2 == 209) {
				this.mc.ingameGUI.getChatGUI().scroll(
						-this.mc.ingameGUI.getChatGUI().func_146232_i() + 1);
			} else {
				this.inputField.textboxKeyTyped(par1, par2);
			}
		} else {
			String s = this.inputField.getText().trim();

			if (s.length() > 0) {
				this.func_146403_a(s);
				/*
				 * Clear the last sent text.
				 */
				this.inputField.setText("");
			}
			/*
			 * REMOVED since we don't want to exit until we ESCAPE or the tune ends
			 * this.mc.displayGuiScreen((GuiScreen) null);
			 */
		}
	}

	protected void sendStop() {
		PacketPlayStop packetPlayStop = new PacketPlayStop(
				this.mc.thePlayer.getDisplayName());
		MCJammer.proxy.packetCLL_sendToServer(packetPlayStop);
	}
}