package net.aeronica.mods.mcjammer.client.gui;

import java.util.Iterator;
import java.util.Set;

import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.inventory.IInstrument;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiJamOverlay extends Gui {

	private Minecraft mc = null;
	private FontRenderer fontRenderer = null;
	private static final ResourceLocation textureLocation = new ResourceLocation(
			ModInfo.ID.toLowerCase(), "textures/gui/manage_group.png");

	public GuiJamOverlay(Minecraft mc) {
		super();

		// We need this to invoke the render engine.
		this.mc = mc;
		this.fontRenderer = this.mc.fontRenderer;
	}

	private static final int STAT_ICON_SIZE = 18;

	private static final int STAT_ICON_BASE_U_OFFSET = 0;
	private static final int STAT_ICON_BASE_V_OFFSET = 165;
	private static final int STAT_ICONS_PER_ROW = 8;

	//
	// This event is called by GuiIngameForge during each frame by
	// GuiIngameForge.pre() and GuiIngameForce.post().
	//
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {

		if (mc.gameSettings.showDebugInfo)
			return;
		//
		// We draw after the ExperienceBar has drawn. The event raised by
		// GuiIngameForge.pre()
		// will return true from isCancelable. If you call
		// event.setCanceled(true) in
		// that case, the portion of rendering which this event represents will
		// be canceled.
		// We want to draw *after* the experience bar is drawn, so we make sure
		// isCancelable() returns
		// false and that the eventType represents the ExperienceBar event.
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}

		EntityClientPlayerMP player = this.mc.thePlayer;
		// Starting position for the status bar - 2 pixels from the top left
		// corner.
		int xPos = 2;
		int yPos = 2;
		// String s1 = "";
		// String s2 = "";
		// if (GROUPS.clientGroups != null || GROUPS.clientMembers != null) {
		// s1 = GROUPS.clientGroups.toString();
		// s2 = GROUPS.clientMembers.toString();
		// }
		// GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// GL11.glDisable(GL11.GL_LIGHTING);
		// GL11.glPushMatrix();
		// /*
		// * TODO: Remove debug strings
		// * TODO: Add Song title of music book in instrument inventory
		// */
		// fontRenderer.drawStringWithShadow(s1, 2, 22, 16777215);
		// fontRenderer.drawStringWithShadow(s2, 2, 32, 16777215);
		// GL11.glPopMatrix();
		this.mc.renderEngine.bindTexture(textureLocation);

		if ((player.getHeldItem() != null)
				&& (player.getHeldItem().getItem() instanceof IInstrument)) {

			int iconIndex = GROUPS.getIndex(player.getDisplayName());
			this.drawTexturedModalRect(xPos, yPos, STAT_ICON_BASE_U_OFFSET
					+ iconIndex % STAT_ICONS_PER_ROW * STAT_ICON_SIZE,
					STAT_ICON_BASE_V_OFFSET + iconIndex / STAT_ICONS_PER_ROW
							* STAT_ICON_SIZE, STAT_ICON_SIZE, STAT_ICON_SIZE);

		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderText(RenderGameOverlayEvent event) {

		if (mc.gameSettings.showDebugInfo)
			return;

		if (event.isCancelable() || event.type != ElementType.TEXT) {
			return;
		}

		EntityClientPlayerMP player = this.mc.thePlayer;

		drawMusicTitle(player);
		drawGroup(player);
	}

	private void drawMusicTitle(EntityPlayer player) {
		// draw the music title if any
		if ((player.getHeldItem() != null)
				&& (player.getHeldItem().getItem() instanceof IInstrument)) {

			ItemStack is = player.getHeldItem();
			fontRenderer.drawStringWithShadow(getMusicTitle(is), 22, 7,
					16777215);

		}
	}

	private String getMusicTitle(ItemStack is) {
		String result = EnumChatFormatting.YELLOW + "(empty)";
		if (is == null)
			return result;
		// Display the title of the contained music book.
		if (is.stackTagCompound != null) {
			NBTTagList items = is.stackTagCompound.getTagList("ItemInventory",
					10);
			if (items.tagCount() == 1) {
				NBTTagCompound item = (NBTTagCompound) items
						.getCompoundTagAt(0);
				ItemStack isMusicBook = ItemStack.loadItemStackFromNBT(item);
				NBTTagCompound contents = (NBTTagCompound) isMusicBook.stackTagCompound
						.getTag("MusicBook");
				if (contents != null) {
					result = EnumChatFormatting.GREEN
							+ isMusicBook.getDisplayName();
				}
			}
		}
		return result;
	}

	private void drawGroup(EntityPlayer player) {
		int posX = 2;
		int posY = 22;
		String groupID;
		String member;

		if (GROUPS.clientGroups != null || GROUPS.clientMembers != null) {
			groupID = GROUPS.getMembersGroupID(player.getDisplayName());
			// Only draw if player is a member of a group
			if (groupID != null) {
				// Always put the leader at the TOP of the list
				fontRenderer.drawStringWithShadow(EnumChatFormatting.YELLOW
						+ GROUPS.getLeaderOfGroup(groupID), posX, posY,
						16777215);
				posY += 10;
				// display the remaining members taking care to not print the
				// leader a 2nd time.
				Set<String> set = GROUPS.clientMembers.keySet();
				for (Iterator<String> im = set.iterator(); im.hasNext();) {
					member = im.next();
					if (groupID.equalsIgnoreCase(GROUPS
							.getMembersGroupID(member))
							&& !member.equalsIgnoreCase(GROUPS
									.getLeaderOfGroup(groupID))) {
						fontRenderer.drawStringWithShadow(member, posX, posY,
								16777215);
						posY += 10;
					}
				}
			}
		}
	}
}
