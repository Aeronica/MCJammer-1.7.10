package net.aeronica.mods.mcjammer.common.items;

import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.aeronica.mods.mcjammer.common.libs.config.Refs;
import net.minecraft.item.Item;

public class MusicItems {

	public static Item instruments;
	public static Item sheetmusic;

	public static void initItems() {
		ModLogger.logInfo("initItems");		
		/*
		 * Bimbie's Instruments
		 */
		instruments	= new ItemInstrument(Refs.ITEM_INSTRUMENT);
		
		/*
		 * Music books, composition books, lead sheets.
		 * i.e. items that hold or allow entry or editing of MML
		 */

		// Put this sheet music on your instrument and play it.
		sheetmusic = new ItemSheetMusic(Refs.ITEM_MUSIC, Refs.NAME_SHEET_MUSIC);
	}
}
