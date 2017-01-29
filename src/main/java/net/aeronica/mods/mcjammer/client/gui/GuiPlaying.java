package net.aeronica.mods.mcjammer.client.gui;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlayStop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

/*
 * TODO: Replace with a modified GuiChat
 */
public class GuiPlaying extends GuiScreen {
	public static final int GUI_ID = 3;
	private static final String TITLE = "Playing";
	private Minecraft mc;
	private FontRenderer fontRenderer = null;
	private EntityPlayer player;

	/** The X size of the group window in pixels. */
	protected int xSize = 512;

	/** The Y size of the group window in pixels. */
	protected int ySize = 256;

	/**
	 * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiLeft;

	/**
	 * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiTop;

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(false);

		this.mc = Minecraft.getMinecraft();
		this.fontRenderer = mc.fontRenderer;
		this.player = mc.thePlayer;

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		// clear control list
		buttonList.clear();
	}

	@Override
	public void drawScreen(int i, int j, float f) {

		// draw transparent background
		// drawDefaultBackground();

		// draw GUI background
		drawGuiBackground();

		// draw "TITLE"
		int posX = guiLeft + xSize/2 - fontRenderer.getStringWidth(TITLE)/2;
		int posY = guiTop + ySize/4;
		fontRenderer.getStringWidth(TITLE);
		fontRenderer.drawStringWithShadow(TITLE, posX, posY, 16777215);
		
		super.drawScreen(i, j, f);
	}
	
	protected void drawGuiBackground() {
		// draw texture
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat
	 * events, etc
	 */
	@Override
	public void onGuiClosed() {
		sendStop();
		ModLogger.logInfo("GuiPlaying.onGuiClosed");
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void sendStop() {
		PacketPlayStop packetPlayStop = new PacketPlayStop(player.getDisplayName());
		MCJammer.proxy.packetCLL_sendToServer(packetPlayStop);
	}
}
