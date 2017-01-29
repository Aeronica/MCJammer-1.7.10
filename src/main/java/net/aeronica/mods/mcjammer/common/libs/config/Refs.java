package net.aeronica.mods.mcjammer.common.libs.config;

import net.aeronica.mods.mcjammer.common.libs.ModInfo;

public class Refs {
	
	public static final String ITEMS_RESOURCE_LOCATION = ModInfo.ID.toLowerCase() + ":";
	
	public static final double RANGE_ALL_AROUND = 24f;
	
	/*
	 * Legacy Instruments
	 */
	public static final String[] mcjMetaItem_names = {
		"Lute",
		"Ukulele",
		"Mandolin",
		"Recorder",
		"Pipe",
		"Flute",
		"Oboe",
		"Tuba",
		"Lyra",
		"Electric_Guitar",
		"Piano",
		"Violin",
		"Cello",
		"Drum_Set",
		"Music_Book" // depricated
	};
	public static final String mcjMetaItem_name = "Music Meta Items";
	public static final String mcjMetaItem_uloc = ModInfo.ID + "_MetaItems"; //MCJammer_MetaItems
	
	/*
	 * Bimbie's Instruments
	 */
	public static final String ITEM_INSTRUMENT = ModInfo.ID.toLowerCase() + ".instrument";
	
	/*
	 * Music books, lead sheets, etc.
	 */
	public static final String ITEM_MUSIC = ModInfo.ID.toLowerCase() + ".music";
	public static final String NAME_MUSIC_PAPER = "musicpaper";
	public static final String NAME_SHEET_MUSIC = "sheetmusic";
	
}
