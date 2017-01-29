package net.aeronica.mods.mcjammer.client.gui;

import java.util.Iterator;
import java.util.Set;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.net.packets.PacketGroupManage;
import net.aeronica.mods.mcjammer.common.player.ExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiGroupJoin extends GuiScreen{
	public static final int GUI_ID = 5;
	
	private Minecraft mc;
	private FontRenderer fontRenderer = null;

	private static final ResourceLocation score_entryTexture = new ResourceLocation(
			ModInfo.ID.toLowerCase(), "textures/gui/manage_group.png");

	/** The X size of the group window in pixels. */
	protected int xSize = 239;

	/** The Y size of the group window in pixels. */
	protected int ySize = 164;

	/**
	 * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiLeft;

	/**
	 * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiTop;

	private GuiButton btn_yes, btn_no;
	private GuiSlider gTest;

	private EntityPlayer player;
	
	private String groupID;

	@SuppressWarnings("unchecked")
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

		// create button for Yes
		int posX = guiLeft + 169;
		int posY = guiTop + 92;
		btn_yes = new GuiButton(0, posX, posY, 60, 20, "Yes");

		// create button for No
		posX = guiLeft + 169;
		posY = guiTop + 112;
		btn_no = new GuiButton(1, posX, posY, 60, 20, "No");

		gTest = new GuiSlider(2, posX, posY-50, 60, 20, "Test", 10f, 1f, 20f, 1f);
		
		// add buttons to control list
		buttonList.add(btn_yes);
		buttonList.add(btn_no);
		buttonList.add(gTest);
		
		/*
		 * This is a back door way to pass parameters to the Gui.
		 */
		ExtendedPlayer props = ExtendedPlayer.get(player);
		this.groupID = props.getSParam1();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
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

		// draw "TITLE" at the top right
		String title = I18n.format("groupjoin.yes.no", new Object[0]);
		int posX = guiLeft + xSize - fontRenderer.getStringWidth(title) - 12;
		int posY = guiTop + 12;
		fontRenderer.getStringWidth(title);
		fontRenderer.drawString(title, posX, posY, 0x000000);

		drawMembers();

		// draw the things in the controlList (buttons)
		super.drawScreen(i, j, f);
	}

	private void drawMembers() {
		int posX = guiLeft + 12;
		int posY = guiTop + 12;
		String member;
		
		if (GROUPS.clientGroups != null || GROUPS.clientMembers != null) {
			if (groupID != null) {
				// Always put the leader at the TOP of the list
				fontRenderer.drawStringWithShadow(GROUPS.getLeaderOfGroup(groupID), posX,
						posY, 16777215);
				posY += 10;
				// display the remaining members taking care to not print the
				// leader a 2nd time.
				Set<String> set = GROUPS.clientMembers.keySet();
				for (Iterator<String> im = set.iterator(); im.hasNext();) {
					member = im.next();
					if (groupID.equalsIgnoreCase(GROUPS.getMembersGroupID(member))
							&& !member.equalsIgnoreCase(GROUPS.getLeaderOfGroup(groupID))) {
						fontRenderer.drawStringWithShadow(member, posX, posY,
								16777215);
			
						// Only Leaders get to remove and promote other members!
						if (player.getDisplayName().equalsIgnoreCase(
								GROUPS.getLeaderOfGroup(groupID))) {
						}
						posY += 10;
					}
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {

		// id 0 = yes; 1 = no;
		switch (guibutton.id) {
		case 2:
			return;
		case 0:
			// Yes
			sendRequest(GROUPS.MEMBER_ADD, groupID, player.getDisplayName());
			
		default:
		}
		mc.displayGuiScreen(null);
		mc.setIngameFocus();
	}

	/**
	 * Gets the image for the background and renders it in the middle of the
	 * screen.
	 */
	protected void drawGuiBackground() {
		// draw texture
		GL11.glColor4f(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(score_entryTexture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	protected void sendRequest(GROUPS operation, String groupID,
			String memberName) {
		PacketGroupManage packetGroupManage = new PacketGroupManage(
				operation.toString(), groupID, memberName);
		MCJammer.proxy.packetCLL_sendToServer(packetGroupManage);
	}
}
