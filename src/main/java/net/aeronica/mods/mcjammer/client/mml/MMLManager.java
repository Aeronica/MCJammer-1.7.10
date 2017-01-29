package net.aeronica.mods.mcjammer.client.mml;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;

/**
 * Creates a thread pool of 1 to keep the midi system initializations and mml
 * parsing out of the game loop to keep graphic updates as smooth as silk.
 * 
 * @author Aeronica
 * 
 */
public class MMLManager {
	private final int NTHREDS = 1;
	private static ExecutorService executor;
	private static volatile Map<String, MMLPlayer> mmlThreads = new HashMap<String, MMLPlayer>();

	private Minecraft mc;

	/*
	 * Counted mute status Since there can be multiple playing instances we only
	 * want to mute sounds once. New play instances increment the count. As each
	 * instance completes or is cancelled the count is decremented.
	 */
	private static volatile int muted = 0;

	/*
	 * A temporary storage for saved sound category levels
	 */
	private Map<SoundCategory, String> savedSoundLevels = new HashMap<SoundCategory, String>();

	// Don't allow any other class to instantiate the MusicLib
	private MMLManager() {
		executor = Executors.newFixedThreadPool(NTHREDS);
		mc = Minecraft.getMinecraft();
	}

	/**
	 * this Class is loaded on the first execution of Class.getClass() or the
	 * first access to ClassHolder.INSTANCE, not before. ref:
	 * http://en.wikipedia.org/wiki/Singleton_pattern
	 */
	private static class MMLManagerHolder {
		public static final MMLManager INSTANCE = new MMLManager();
	}

	public static MMLManager getMMLManager() {
		return MMLManagerHolder.INSTANCE;
	}

	public void mmlPlay(String mml, String groupID) {
		Runnable worker = new ThreadedmmlPlay(mml, groupID);
		executor.execute(worker);
	}

	/*
	 * TODO: Make a real initialization
	 */
	public void mmlInit() {
		// Client side
		this.mmlPlay("-Init-=MML@t240v0l64crrr,,;", "-Init-");
	}

	private void muteSounds() {
		if (muted++ > 0) {
			ModLogger.logInfo("Mute count: " + muted);
			return;
		} else {
			ModLogger.logInfo("Mute sounds");
			saveLevelsAndMute();
		}
	}

	private void unMuteSounds() {
		--muted;
		if (muted > 0) {
			ModLogger.logInfo("Un-mute count: " + muted);
		} else {
			muted = 0;
			ModLogger.logInfo("Un-mute sounds");
			loadLevelsAndUnMute();
		}
	}

	private void saveLevelsAndMute() {

		SoundCategory[] asoundcategory = SoundCategory.values();
		int i = asoundcategory.length;
		Float v;
		for (int j = 0; j < i; ++j) {
			SoundCategory soundcategory = asoundcategory[j];
			v = mc.gameSettings.getSoundLevel(soundcategory);
			savedSoundLevels.put(soundcategory, v.toString());
			mc.gameSettings.setSoundLevel(soundcategory, muteLevel(soundcategory, v));
			mc.gameSettings.setSoundLevel(soundcategory, muteLevel(soundcategory, v));
			mc.gameSettings.saveOptions();
		}
	}

	private void loadLevelsAndUnMute() {
		SoundCategory[] asoundcategory = SoundCategory.values();
		int i = asoundcategory.length;
		Float v;
		for (int j = 0; j < i; ++j) {
			SoundCategory soundcategory = asoundcategory[j];
			v = Float.valueOf(savedSoundLevels.get(soundcategory));
			restoreLevel(soundcategory, v);
			mc.gameSettings.saveOptions();
		}
	}
	
	private float muteLevel(SoundCategory sc, float level) {
		switch (sc) {
		case MASTER:
			return level;

		case MUSIC:
			return 0f;

		case RECORDS:
			return 0f;

		case BLOCKS:
			return 0f;

		case MOBS:
			return 0.1f;

		case ANIMALS:
			return 0.0f;

		case PLAYERS:
			return 0.1f;

		case AMBIENT:
			return 0f;
			
		case WEATHER:
			return 0f;

		default:
			return 0.1f;
		}

	}

	private void restoreLevel(SoundCategory sc, float level) {
		mc.gameSettings.setSoundLevel(sc, level);
		mc.gameSettings.setSoundLevel(sc, level);
	}

	/**
	 * 
	 * @param mmlPlayer
	 * @param ID
	 * @return false if ID is already registered, or true is successfully
	 *         registered
	 */
	public boolean registerThread(MMLPlayer mmlPlayer, String ID) {
		boolean result = false;
		try {
			if (mmlThreads != null && !mmlThreads.containsKey(ID)) {
				mmlThreads.put(ID, mmlPlayer);
				muteSounds();
				result = true;
			}
		} catch (IllegalArgumentException e) {
			ModLogger.logInfo(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (Exception e) {
			ModLogger.logInfo(e.getLocalizedMessage());
			e.printStackTrace();
		}
		ModLogger.logInfo("registerThread: " + ID + " " + result);
		return result;
	}

	/**
	 * de-register the playing thread.
	 * 
	 * @param ID
	 */
	public void deregisterThread(String ID) {
		if (mmlThreads != null && mmlThreads.containsKey(ID)) {
			try {
				mmlThreads.remove(ID);
			} catch (IllegalArgumentException e) {
				ModLogger.logInfo(e.getLocalizedMessage());
				e.printStackTrace();
			} catch (Exception e) {
				ModLogger.logInfo(e.getLocalizedMessage());
				e.printStackTrace();
			}
			unMuteSounds();
			ModLogger.logInfo("deregisterThread: " + ID);
		}
	}

	public void mmlKill(String playID) {
		/*
		 * Quite a mess, but here we get the group ID if there is one associated
		 * with the player. We use that to get the instance.
		 */
		String resultID = GROUPS.getMembersGroupID(playID) != null ? GROUPS
				.getMembersGroupID(playID) : playID;
		if (mmlThreads != null && mmlThreads.containsKey(resultID)) {
			try {
				MMLPlayer pInstance = mmlThreads.get(resultID);
				if (pInstance != null) {
					Runnable worker = new ThreadedmmlKill(pInstance, playID);
					executor.execute(worker);
				}
			} catch (IllegalArgumentException e) {
				ModLogger.logInfo(e.getLocalizedMessage());
				e.printStackTrace();
			} catch (Exception e) {
				ModLogger.logInfo(e.getLocalizedMessage());
				e.printStackTrace();
			}
			ModLogger.logInfo("MMLManager.mmlKill(): " + resultID);
		}
	}

	private class ThreadedmmlPlay implements Runnable {
		private final String mml;
		private final String groupID;

		public ThreadedmmlPlay(String mml, String groupID) {
			this.mml = mml;
			this.groupID = groupID;
		}

		@Override
		public void run() {
			MMLPlayer mp = new MMLPlayer();
			mp.mmlPlay(mml, groupID);
		}
	}

	private class ThreadedmmlKill implements Runnable {
		private final MMLPlayer pinstance;
		private final String ID;

		public ThreadedmmlKill(MMLPlayer pInstance, String ID) {
			this.pinstance = pInstance;
			this.ID = ID;
		}

		@Override
		public void run() {
			pinstance.mmlKill(ID);
		}
	}
}
