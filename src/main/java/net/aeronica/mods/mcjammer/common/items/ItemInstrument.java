package net.aeronica.mods.mcjammer.common.items;

import java.util.List;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.core.IKeyListener;
import net.aeronica.mods.mcjammer.client.gui.GuiInstInvAdjustRotations;
import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.inventory.IInstrument;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlaySolo;
import net.aeronica.mods.mcjammer.common.net.packets.PacketQueueJAM;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInstrument extends Item implements IInstrument, IKeyListener {

	public ItemInstrument(String unloc) {
		super();
		setHasSubtypes(true);
		setCreativeTab(MCJammer.TAB_MUSIC);
		setUnlocalizedName(unloc);
		setFull3D();
		setMaxStackSize(1);
		setMaxDamage(0);
		GameRegistry.registerItem(this, unloc);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = MathHelper.clamp_int(stack.getItemDamage(), 0,
				InstrumentEnum.values().length);
		String out = InstrumentEnum.values()[i].NameHi;
		return super.getUnlocalizedName() + "." + out;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item thing, CreativeTabs tab, List list) {
		for (int i = 0; i < InstrumentEnum.values().length; i++) {
			ItemStack itemstack = new ItemStack(thing, 1, i);
			list.add(itemstack);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world,
			EntityPlayer player) {

		if (!world.isRemote) {
			// Server Side - Open the instrument inventory GuiInstInvAdjustRotations
			if (player.isSneaking()) {
				player.openGui(MCJammer.instance,
						GuiInstInvAdjustRotations.GUI_ID, world,
						(int) player.posX, (int) player.posY, (int) player.posZ);
			}
		} else {
			// Client Side - play the instrument
			if (!player.isSneaking() && is.stackTagCompound != null)
				playMusic(player, is);
		}
		return is;
	}

	@Override
	public void onKeyPressed(String key, EntityPlayer player, ItemStack is) {
		// Client Side - play the instrument
		if (key.equalsIgnoreCase("key.playInstrument"))
			playMusic(player, is);
	}
	
	/**
	 * Don't let vanilla try to register any icons! We are using a custom
	 * renderer!
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
	}

	/**
	 * This is where we decide how our item interacts with other entities
	 */
	@Override
	public boolean itemInteractionForEntity(ItemStack itemStack,
			EntityPlayer player, EntityLivingBase target) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List list,
			boolean par4) {

		if (is == null)
			return;
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
					list.add(EnumChatFormatting.GREEN + "Title: "
							+ isMusicBook.getDisplayName());
				}
			}
		}
	}

	private void playMusic(EntityPlayer player, ItemStack is) {
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
					// String mml = contents.getString("MML");
					// System.out.println("+++ MML = " +
					// mml.substring(0,
					// (mml.length()>=25?25:mml.length())));

					/**
					 * The packet itself notifies the server to grab the NBT
					 * item from the itemstack and distribute it to all clients.
					 */
					if (GROUPS.getMembersGroupID(player.getDisplayName()) == null) {
						// Solo
						PacketPlaySolo packetPlaySolo = new PacketPlaySolo(
								player.getDisplayName());
						MCJammer.proxy.packetCLL_sendToServer(packetPlaySolo);
					} else {
						// Jam
						PacketQueueJAM packetPlayJAM = new PacketQueueJAM();
						MCJammer.proxy.packetCLL_sendToServer(packetPlayJAM);
					}
					System.out.println("+++ queue play request +++");
				}
			}
		}
	}
}
