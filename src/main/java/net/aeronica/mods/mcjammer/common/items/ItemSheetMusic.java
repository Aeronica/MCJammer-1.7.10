package net.aeronica.mods.mcjammer.common.items;

import java.util.List;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.gui.GuiMusicBook;
import net.aeronica.mods.mcjammer.common.inventory.IMusic;
import net.aeronica.mods.mcjammer.common.libs.config.Refs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemSheetMusic extends Item implements IMusic{

	public ItemSheetMusic(String type, String name) {
		super();
		setHasSubtypes(false);
		setCreativeTab(MCJammer.TAB_MUSIC);
		setTextureName(Refs.ITEMS_RESOURCE_LOCATION + name);
		setUnlocalizedName(type + "." + name);
		setMaxStackSize(64);
		GameRegistry.registerItem(this, type + "." + name);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world,
			EntityPlayer player) {

		if (world.isRemote) {
			// Client side
			if (player.isSneaking() && !is.hasDisplayName()) {
				player.openGui(MCJammer.instance, GuiMusicBook.GUI_ID, world,
						(int) player.posX, (int) player.posY, (int) player.posZ);
			}
		}
		return is;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack is, EntityPlayer player, List list,
			boolean par4) {

		if (is == null)
			return;
		switch (this.getDamage(is)) {
		case 0:
			break;
		default:
			// Display the title of the contained music book.
			if (is.stackTagCompound != null) {
				NBTTagList items = is.stackTagCompound.getTagList(
						"ItemInventory", 10);
				if (items.tagCount() == 1) {
					NBTTagCompound item = (NBTTagCompound) items
							.getCompoundTagAt(0);
					ItemStack isMusicBook = ItemStack
							.loadItemStackFromNBT(item);
					NBTTagCompound contents = (NBTTagCompound) isMusicBook.stackTagCompound
							.getTag("MusicBook");
					if (contents != null) {
						list.add(EnumChatFormatting.GREEN + "Enclosed Title: "
								+ isMusicBook.getDisplayName());
					}
				}
			}
		}
	}

	
	/**
	 * If this function returns true (or the item is damageable), the
	 * ItemStack's NBT tag will be sent to the client.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	/**
	 * NOTE: If you want to open your gui on right click and your ItemStore, you
	 * MUST override getMaxItemUseDuration to return a value of at least 1,
	 * otherwise you won't be able to open the Gui. That's just how it works.
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 1;
	}
}
