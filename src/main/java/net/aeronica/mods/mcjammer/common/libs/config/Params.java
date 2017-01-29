package net.aeronica.mods.mcjammer.common.libs.config;

import net.aeronica.mods.mcjammer.common.libs.ModLogger;

/*
 ; --------------------------------------------------------------------------------------------------
 ; Global Definitions
 ; --------------------------------------------------------------------------------------------------

 Name: MSXSPIRIT
 Description: 
 Vendor: HanStone
 Version: 65536.1
 ------------------------------------
 Soundbanks: MSXSPIRIT,  0,   0,   Lute
 Soundbanks: MSXSPIRIT,  0,   1,   Ukulele
 Soundbanks: MSXSPIRIT,  0,   2,   Mandolin
 Soundbanks: MSXSPIRIT,  0,   3,   Recorder
 Soundbanks: MSXSPIRIT,  0,   4,   Pipe
 Soundbanks: MSXSPIRIT,  0,   5,   Flute
 Soundbanks: MSXSPIRIT,  0,   6,   Oboe
 Soundbanks: MSXSPIRIT,  0,   7,   Bottle_C
 Soundbanks: MSXSPIRIT,  0,   8,   Bottle_D
 Soundbanks: MSXSPIRIT,  0,   9,   Bottle_E
 Soundbanks: MSXSPIRIT,  0,   10,   Bottle_F
 Soundbanks: MSXSPIRIT,  0,   11,   Bottle_G
 Soundbanks: MSXSPIRIT,  0,   12,   Bottle_B
 Soundbanks: MSXSPIRIT,  0,   13,   Bottle_A
 Soundbanks: MSXSPIRIT,  0,   14,   GreenFinch
 Soundbanks: MSXSPIRIT,  0,   15,   FairyPitta
 Soundbanks: MSXSPIRIT,  0,   16,   KingFisher
 Soundbanks: MSXSPIRIT,  0,   17,   GreenFinch_Short
 Soundbanks: MSXSPIRIT,  0,   18,   Tuba
 Soundbanks: MSXSPIRIT,  0,   19,   Lyra
 Soundbanks: MSXSPIRIT,  0,   20,   electricguitar
 Soundbanks: MSXSPIRIT,  0,   21,   Piano
 Soundbanks: MSXSPIRIT,  0,   22,   Viollin
 Soundbanks: MSXSPIRIT,  0,   23,   Cello
 Soundbanks: MSXSPIRIT,  0,   65,   snare_c3
 Soundbanks: MSXSPIRIT,  0,   66,   bassdrum
 Soundbanks: MSXSPIRIT,  0,   67,   snare_1
 Soundbanks: MSXSPIRIT,  0,   68,   cymbal
 Soundbanks: MSXSPIRIT,  0,   69,   Handbell_C3
 Soundbanks: MSXSPIRIT,  0,   70,   Handbell_D3
 Soundbanks: MSXSPIRIT,  0,   71,   Handbell_E3
 Soundbanks: MSXSPIRIT,  0,   72,   Handbell_F3
 Soundbanks: MSXSPIRIT,  0,   73,   Handbell_G3
 Soundbanks: MSXSPIRIT,  0,   74,   Handbell_A3
 Soundbanks: MSXSPIRIT,  0,   75,   Handbell_B3
 Soundbanks: MSXSPIRIT,  0,   76,   Handbell_C4
 Soundbanks: MSXSPIRIT,  0,   77,   Chime
 Soundbanks: MSXSPIRIT,  0,   80,   song
 Soundbanks: MSXSPIRIT,  0,   81,   song
 Soundbanks: MSXSPIRIT,  0,   82,   song
 Soundbanks: MSXSPIRIT,  0,   83,   song
 Soundbanks: MSXSPIRIT,  0,   84,   song
 Soundbanks: MSXSPIRIT,  0,   90,   song
 Soundbanks: MSXSPIRIT,  0,   91,   song
 Soundbanks: MSXSPIRIT,  0,   92,   song
 Soundbanks: MSXSPIRIT,  0,   93,   song
 Soundbanks: MSXSPIRIT,  0,   94,   song
 Soundbanks: MSXSPIRIT,  0,   100,   song
 Soundbanks: MSXSPIRIT,  0,   110,   song
 Soundbanks: MSXSPIRIT,  0,   120,   song
 Soundbanks: MSXSPIRIT,  0,   121,   song
 */
public class Params {
	public static final String mcjGMMaping_desc = "Lute,Ukulele,Mandolin,Recorder,Pipe,Flute,Oboe,Tuba,Lyra,Elec Guitar,Piano,Violin,Cello,Drum,Chimes";
	public static final String mcjGMMaping_default = "25,33,26,74,77,73,68,57,46,29,0,40,42,118,9";
	public static final String mcjMXSpirit_default = "0,1,2,3,4,5,6,18,19,20,21,22,23,65,77";
	public static String mcjGMMaping_actual;
	private static Integer intGMMap[] = new Integer[Refs.mcjMetaItem_names.length];

	public static float getMinMax(float min, float max, float value) {
		return (float) Math.max(Math.min(max, value), min);
	}

	public static byte getMinMax(byte min, byte max, byte value) {
		return (byte) Math.max(Math.min(max, value), min);
	}

	public static int getMinMax(int min, int max, int value) {
		return (int) Math.max(Math.min(max, value), min);
	}

	public static void initGMMap() {
		String sMaps[] = mcjGMMaping_actual.split(",");
		String sdMaps[] = mcjGMMaping_default.split(",");
		@SuppressWarnings("unused")
		String mxMaps[] = mcjMXSpirit_default.split(",");
		if (sMaps.length != sdMaps.length) {
			mcjGMMaping_actual = mcjGMMaping_default;
			sMaps = sdMaps;
		}

		for (int i = 0; i < sMaps.length; i++) {
			intGMMap[i] = Integer.valueOf(sMaps[i].trim());
			ModLogger.debug("GMMap index: " + i + " value: " + intGMMap[i]);
		}
	}

	public static int getGMMap(int instDamage) {
		return intGMMap[instDamage];
	}
}
