package net.aeronica.mods.mcjammer.client.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Combustible Lemon Launcher
 * @author Phil Julian (aka iBuilder99)
 */

public interface IKeyListener {

	public void onKeyPressed(String key, EntityPlayer player, ItemStack itemstack);
	
}
