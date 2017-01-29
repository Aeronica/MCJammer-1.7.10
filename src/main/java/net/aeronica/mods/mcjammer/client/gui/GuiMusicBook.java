package net.aeronica.mods.mcjammer.client.gui;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.net.packets.PacketMusicScroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

public class GuiMusicBook extends GuiScreen {
	public static final int GUI_ID = 0;

	private Minecraft mc;
	private FontRenderer fontRenderer = null;

	private static final ResourceLocation score_entryTexture = new ResourceLocation(
			ModInfo.ID.toLowerCase(), "textures/gui/score_entry.png");
	private String TITLE = "MML Clipboard Paste Dialog";

	private GuiTextField txt_mmlTitle;
	private GuiTextField txt_mmlPaste;
	private GuiButton btn_ok, btn_cancel;

	public GuiMusicBook() {
	}

	@Override
	public void updateScreen() {
		txt_mmlTitle.updateCursorCounter();
		txt_mmlPaste.updateCursorCounter();
	}

	/**
	 * Initializes the GUI elements.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		this.mc = Minecraft.getMinecraft();
		this.fontRenderer = mc.fontRenderer;

		Keyboard.enableRepeatEvents(true);
		// clear control list
		buttonList.clear();

		// create button for OK and disable it at the beginning
		int posX = width / 2 + 100 - 80;
		int posY = height / 2 + 50 - 24 + 25;
		btn_ok = new GuiButton(0, posX, posY, 60, 20, "OK");
		btn_ok.enabled = false;

		// create button for cancel
		posX = width / 2 - 100 + 20;
		posY = height / 2 + 50 - 24 + 25;
		btn_cancel = new GuiButton(1, posX, posY, 60, 20, "Cancel");

		// add buttons to control list
		buttonList.add(btn_ok);
		buttonList.add(btn_cancel);

		// create text field
		posX = width / 2 - 100;
		posY = height / 2 - 50 + 47;
		txt_mmlTitle = new GuiTextField(fontRenderer, posX, posY, 200, 20);
		txt_mmlTitle.setFocused(true);
		txt_mmlTitle.setCanLoseFocus(true);
		txt_mmlTitle.setMaxStringLength(25);

		// create text field
		posX = width / 2 - 100;
		posY = height / 2 - 50 + 47 + 25;
		txt_mmlPaste = new GuiTextField(fontRenderer, posX, posY, 200, 20);
		txt_mmlPaste.setFocused(false);
		txt_mmlPaste.setCanLoseFocus(true);
		txt_mmlPaste.setMaxStringLength(10000);

	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int i, int j, float f) {
		// draw transparent background
		drawDefaultBackground();

		// draw GUI background
		drawGuiBackground();

		// draw "TITLE" at the top in the middle
		int posX = width / 2 - fontRenderer.getStringWidth(TITLE) / 2;
		int posY = height / 2 - 50 + 20;
		fontRenderer.drawString(TITLE, posX, posY, 0x000000);

		// draw "Field name:" at the left site above the GuiTextField
		posX = width / 2 - 100;
		posY = height / 2 - 50 + 35;
		fontRenderer.drawString("Title / MML@:", posX, posY, 0x404040);

		// draw the GuiTextField
		txt_mmlTitle.drawTextBox();
		txt_mmlPaste.drawTextBox();

		// draw the things in the controlList (buttons)
		super.drawScreen(i, j, f);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		// if button is disabled ignore click
		if (!guibutton.enabled) {
			return;
		}

		// id 0 = ok; id 1 = cancel
		switch (guibutton.id) {
		case 0:
			String musictext = txt_mmlPaste.getText().toUpperCase().trim();
			String musictitle = txt_mmlTitle.getText().trim();

			// save the mml
			System.out.println("+++ Gui send packet to server +++");
			sendNewNameToServer(musictitle, musictext);

		case 1:
			// remove the GUI
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
			break;
		default:
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char c, int i) {
		// add char to GuiTextField
		txt_mmlTitle.textboxKeyTyped(c, i);
		txt_mmlPaste.textboxKeyTyped(c, i);
		// if (txt_mmlTitle.isFocused()) txt_mmlTitle.textboxKeyTyped(c, i);
		// if (txt_mmlPaste.isFocused()) txt_mmlPaste.textboxKeyTyped(c, i);

		// enable ok button when GuiTextField content is greater than 0 chars
		((GuiButton) buttonList.get(0)).enabled = txt_mmlPaste.getText().trim()
				.length() > 0;
		// perform click event on ok button when Enter is pressed
		if (c == '\n' || c == '\r') {
			actionPerformed((GuiButton) buttonList.get(0));
		}
		// perform click event on cancel button when Esc is pressed
		if (Integer.valueOf(c) == 27) {
			actionPerformed((GuiButton) buttonList.get(1));
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		// move cursor to clicked position in GuiTextField
		txt_mmlTitle.mouseClicked(i, j, k);
		txt_mmlPaste.mouseClicked(i, j, k);
	}

	/**
	 * Gets the image for the background and renders it in the middle of the
	 * screen.
	 */
	protected void drawGuiBackground() {
		mc.renderEngine.bindTexture(score_entryTexture);
		// calculate position and draw texture
		int j = (width - 100) / 2;
		int k = (height - 50) / 2;
		drawTexturedModalRect(j - 100 + 30, k - 50 + 30 + 5, 0, 0, 240, 120);
	}

	protected void sendNewNameToServer(String title, String data) {
		PacketMusicScroll packetMusicScroll = new PacketMusicScroll(title, data);
		MCJammer.proxy.packetCLL_sendToServer(packetMusicScroll);
	}
}
