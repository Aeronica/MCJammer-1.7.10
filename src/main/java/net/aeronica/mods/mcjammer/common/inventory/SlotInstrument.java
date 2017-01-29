package net.aeronica.mods.mcjammer.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInstrument extends Slot {
	public SlotInstrument(IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
	}

	/**
	 * Check if the stack is a valid item for this slot. Always false beside for a Music Item.
	 */
	@Override
	public boolean isItemValid(ItemStack is) {

		return is != null && is.getItem() instanceof IMusic && is.hasTagCompound() ? true
				: false;
	}
}