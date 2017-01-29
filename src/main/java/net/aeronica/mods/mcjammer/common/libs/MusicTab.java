package net.aeronica.mods.mcjammer.common.libs;

import net.aeronica.mods.mcjammer.common.items.MusicItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MusicTab extends CreativeTabs {

	public MusicTab(int id, String name) {
		super(id, name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return I18n.format("creativeTabs.musicTab.title", new Object[0]);
	};

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return new ItemStack(MusicItems.instruments, 1, 1);
	}

	@Override
	public Item getTabIconItem() {
		return null;
	}
}
