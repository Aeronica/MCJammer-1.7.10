package net.aeronica.mods.mcjammer.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.aeronica.mods.mcjammer.common.net.packets.PacketGroupManage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiGroup extends GuiScreen {
	public static final int GUI_ID = 2;

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

	private String TITLE = "Jam Session";

	private GuiButton btn_create, btn_leave, btn_cancel;
	private List<MemberButtons> list_btn_members;

	private EntityPlayer player;

	/**
	 * Initializes the GUI elements.
	 */
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

		// create button for group creation and disable it at the beginning
		int posX = guiLeft + 169;
		int posY = guiTop + 92;
		btn_create = new GuiButton(0, posX, posY, 60, 20, "Create");

		// create button for leave and disable it at the beginning
		posX = guiLeft + 169;
		posY = guiTop + 112;
		btn_leave = new GuiButton(1, posX, posY, 60, 20, "Leave");

		// create button for cancel
		posX = guiLeft + 169;
		posY = guiTop + 132;
		btn_cancel = new GuiButton(2, posX, posY, 60, 20, "Cancel");

		// create member buttons for delete and promote
		initMembersButtons();

		// add buttons to control list
		buttonList.add(btn_create);
		buttonList.add(btn_leave);
		buttonList.add(btn_cancel);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat
	 * events, etc
	 */
	@Override
	public void onGuiClosed() {
		ModLogger.logInfo("GuiGroup.onGuiClosed");
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
		int posX = guiLeft + xSize - fontRenderer.getStringWidth(TITLE) - 12;
		int posY = guiTop + 12;
		fontRenderer.getStringWidth(TITLE);
		fontRenderer.drawString(TITLE, posX, posY, 0x000000);

		drawMembers();
		// Create and Leave buttons should always reflect group membership
		btn_create.enabled = GROUPS.getMembersGroupID(player.getDisplayName()) == null;
		btn_leave.enabled = GROUPS.getMembersGroupID(player.getDisplayName()) != null;

		// draw the things in the controlList (buttons)
		super.drawScreen(i, j, f);
	}

	private void initMembersButtons() {
		int posX = guiLeft + 104;
		int posY = guiTop + 12 + 10;
		int did = 10;
		int pid = 100;

		list_btn_members = new ArrayList<MemberButtons>();

		for (int i = 0; i < GROUPS.MAX_MEMBERS; i++) {
			list_btn_members.add(i, memberButtons(did, pid, posX, posY));
			posY += 10;
			did++;
			pid++;

		}
	}

	private void clearMembersButtons() {
		for (int i = 0; i < GROUPS.MAX_MEMBERS; i++) {
			list_btn_members.get(i).memberName = "";
			list_btn_members.get(i).btn_delete.enabled = false;
			list_btn_members.get(i).btn_delete.visible = false;
			list_btn_members.get(i).btn_promote.enabled = false;
			list_btn_members.get(i).btn_promote.visible = false;
		}
	}

	private String getMemberButton(int buttonID) {
		MemberButtons mb;
		String memberName = "UnFound_UnFound_Unfound";
		for (int i = 0; i < GROUPS.MAX_MEMBERS; i++) {
			mb = list_btn_members.get(i);
			if (mb.btn_delete.id == buttonID || mb.btn_promote.id == buttonID) {
				memberName = mb.memberName;
			}
		}
		return memberName;
	}

	private void drawMembers() {
		int posX = guiLeft + 12;
		int posY = guiTop + 12;
		String groupID;
		String member;
		int i = 0;

		clearMembersButtons();

		// TODO: use/add sensible methods to make readable - partially done...
		if (GROUPS.clientGroups != null || GROUPS.clientMembers != null) {
			groupID = GROUPS.getMembersGroupID(player.getDisplayName());
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
						list_btn_members.get(i).memberName = member;
						// Only Leaders get to remove and promote other members!
						if (player.getDisplayName().equalsIgnoreCase(
								GROUPS.getLeaderOfGroup(groupID))) {
							list_btn_members.get(i).btn_delete.enabled = true;
							list_btn_members.get(i).btn_delete.visible = true;
							list_btn_members.get(i).btn_promote.enabled = true;
							list_btn_members.get(i).btn_promote.visible = true;
						}
						posY += 10;
						i++;
					}
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		// if button is disabled ignore click
		if (!guibutton.enabled) {
			return;
		}

		if (guibutton.id >= 10 && guibutton.id < 100) {
			sendRequest(GROUPS.MEMBER_REMOVE, "", getMemberButton(guibutton.id));
			System.out
					.println("+++ Gui Remove Member: " + GROUPS.MEMBER_REMOVE);
			return;
		}
		if (guibutton.id >= 100) {
			sendRequest(GROUPS.MEMBER_PROMOTE, "",
					getMemberButton(guibutton.id));
			System.out.println("+++ Gui Promote Member: "
					+ GROUPS.MEMBER_PROMOTE);
			return;
		}

		// id 0 = create; 1 = leave; id 2 = cancel; 10-99 delete; 100+ promote;
		switch (guibutton.id) {
		case 0:
			// Create
			sendRequest(GROUPS.GROUP_ADD, "", player.getDisplayName());
			System.out.println("+++ Gui Create Group: " + GROUPS.GROUP_ADD);
			break;

		case 1:
			// Leave
			sendRequest(GROUPS.MEMBER_REMOVE, "", player.getDisplayName());
			System.out.println("+++ Gui Leave Group: " + GROUPS.MEMBER_REMOVE);
			break;

		case 2:
			// Cancel remove the GUI
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

	protected class MemberButtons {
		GuiButton btn_delete;
		GuiButton btn_promote;
		String memberName;
	}

	// yes I know hard coding those button sizes is uncool
	@SuppressWarnings("unchecked")
	protected MemberButtons memberButtons(int did, int pid, int xpos, int ypos) {
		MemberButtons btns_member = new MemberButtons();
		btns_member.btn_delete = new GuiButton(did, xpos, ypos, 10, 10, "D");
		btns_member.btn_delete.visible = false;
		btns_member.btn_delete.enabled = false;
		btns_member.btn_promote = new GuiButton(pid, xpos + 10, ypos, 10, 10,
				"L");
		btns_member.btn_promote.visible = false;
		btns_member.btn_promote.enabled = false;
		buttonList.add(btns_member.btn_delete);
		buttonList.add(btns_member.btn_promote);
		return btns_member;
	}
}
