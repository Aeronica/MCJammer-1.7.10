package net.aeronica.mods.mcjammer.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Don't allow the open instrument to be removed from the Hotbar when it's open.
 * 
 * Handle the item type and meta data (damage). Sheet Music = 0, Instruments >
 * 0. On the hotbar player.inventory.currentItem ranges from 0 to 8 (left to
 * right).
 * 
 * TODO: I don't like the -28 magic number. It should be calculated. Do other
 * mods mess with the player inventory size? Player inventory size is 40 in
 * vanilla: HotBar = 9, Inventory = 27, Armor = 4
 * 
 * @author Aeronica
 * 
 */
public class SlotHotBar extends Slot {
	public SlotHotBar(IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
	}

	/**
	 * Return whether this slot's stack can be taken from this slot. TODO: Fix
	 * I'm not crazy about the magic number 28
	 */
	@Override
	public boolean canTakeStack(EntityPlayer player) {
		ItemStack is = player.getHeldItem();
		// System.out.println("+++ SlotHotBar slotNumber: " + this.slotNumber);
		// System.out.println("+++ SlotHotBar player.inventory.currentItem: " +
		// player.inventory.currentItem);
		// System.out.println("+++ SlotHotBar inventory.getSizeInventory(): " +
		// inventory.getSizeInventory());

		if (is != null) {

			if (is.getItem() instanceof IInstrument
					&& this.slotNumber - 28 == player.inventory.currentItem)
				return false;
		}
		return true;
	}

}