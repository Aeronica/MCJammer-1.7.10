package net.aeronica.mods.mcjammer.common.inventory;

import net.aeronica.mods.mcjammer.common.player.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInstrument extends Container {
	/** The Item Inventory for this Container */
	public final InventoryInstrument inventory;
	/**
	 * Using these will make transferStackInSlot easier to understand and implement INV_START is the index of the first
	 * slot in the Player's Inventory, so our InventoryItem's number of slots (e.g. 5 slots is array indices 0-4, so
	 * start at 5) Notice how we don't have to remember how many slots we made? We can just use InventoryItem.INV_SIZE
	 * and if we ever change it, the Container updates automatically.
	 */
	private static final int INV_START = InventoryInstrument.INV_SIZE, INV_END = INV_START + 26,
			HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;

	// private static final int xSize = 184;
	// private static final int ySize = 184;

	@Override
	public void onContainerClosed(EntityPlayer player) {
		ExtendedPlayer props = ExtendedPlayer.get(player);
		props.setInInventory(false);
		super.onContainerClosed(player);
	}

	public ContainerInstrument(EntityPlayer player, InventoryPlayer invPlayer, InventoryInstrument invInstrument) {
		inventory = invInstrument;

		// add our (1x1) items from our instrument storage
		addSlotToContainer(new SlotInstrument(inventory, 0, 12, 8 + 2 * 18));

		// add (3x9) players inventory items to the GUI
		for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
			for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
				addSlotToContainer(new Slot(invPlayer, playerInvCol + playerInvRow * 9 + 9, 12 + playerInvCol * 18,
						84 + playerInvRow * 18));
			}
		}

		// add players (1x9) hot-bar items to the GUI
		// this.addSlotToContainer(new Slot(<inventory>, <id>, <x-pos>,
		// <y-pos>));

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			addSlotToContainer(new SlotHotBar(invPlayer, hotbarSlot, 12 + hotbarSlot * 18, 142));
		}

		ExtendedPlayer props = ExtendedPlayer.get(player);
		props.setInInventory(true);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	/**
	 * Called when a player shift-clicks on a slot.
	 */

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotPos) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotPos);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// If item is in our custom Inventory or armor slot
			if (slotPos < INV_START) {
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END + 1, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			// Item is in inventory / hotbar, try to place in custom inventory
			// or armor slots
			else {
				/*
				 * If your inventory only stores certain instances of Items, you can implement shift-clicking to your
				 * inventory like this:
				 * 
				 * // Check that the item is the right type if (itemstack1.getItem() instanceof ItemCustom) { // Try to
				 * merge into your custom inventory slots // We use 'InventoryItem.INV_SIZE' instead of INV_START just
				 * in case // you also add armor or other custom slots if (!this.mergeItemStack(itemstack1, 0,
				 * InventoryItem.INV_SIZE, false)) { return null; } } // If you added armor slots, check them here as
				 * well: // Item being shift-clicked is armor - try to put in armor slot if (itemstack1.getItem()
				 * instanceof ItemArmor) { int type = ((ItemArmor) itemstack1.getItem()).armorType; if
				 * (!this.mergeItemStack(itemstack1, ARMOR_START + type, ARMOR_START + type + 1, false)) { return null;
				 * } }
				 * 
				 * Otherwise, you have basically 2 choices: 1. shift-clicking between player inventory and custom
				 * inventory 2. shift-clicking between action bar and inventory
				 * 
				 * Be sure to choose only ONE of the following implementations!!!
				 */

				/**
				 * Only transfer a Written Music Item into Instrument Inventory Slot
				 */
				if ((itemstack1.getItem() instanceof IMusic) && itemstack1.hasTagCompound()) { // place in custom inventory
					if (!this.mergeItemStack(itemstack1, 0, InventoryInstrument.INV_SIZE, false)) {
						return null;
					}
				}

			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(entityPlayer, itemstack1);
		}

		return itemstack;
	}
}
