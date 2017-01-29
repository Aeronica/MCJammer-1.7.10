package net.aeronica.mods.mcjammer.client.mml;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.client.mml.util.MMLLexer;
import net.aeronica.mods.mcjammer.client.mml.util.MMLParser;
import net.aeronica.mods.mcjammer.client.mml.util.MMLToMIDI;
import net.aeronica.mods.mcjammer.common.groups.GROUPS;
import net.aeronica.mods.mcjammer.common.libs.ModLogger;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlayStop;

public class MMLPlayer implements MetaEventListener {
	private Sequencer sequencer = null;
	private Synthesizer synthesizer = null;
    private static byte[]               mmlBuf    = null;
    private InputStream is;
	private String playID = null;
	private Map<String, String> playerMML = new HashMap<String, String>();
	private Map<String, String> playerChannel = new HashMap<String, String>();
	private static final int masterTempo = 120;

//	private Soundbank sbJammer;
//	private static final ResourceLocation soundFont = new ResourceLocation(
//			ModInfo.ID.toLowerCase(), "synth/MCJammerLT.sf2");

	/**
	 * Solo play format "<playerName|groupID>=mml@...;"
	 * 
	 * Jam play format inserts with a space between each player=MML sequence
	 * "<playername1>=MML@...abcd; <playername2>=MML@...efgh; <playername2>=MML@...efgh;"
	 * 
	 * @param mmml
	 * @param playID
	 * @return
	 */
	public boolean mmlPlay(String mmml, String playID) {
		/*
		 * Only one playing instance per playID at a time.
		 */
		if (!MMLManager.getMMLManager().registerThread(this, playID))
			return false;
		this.playID = playID;
		this.playerMML = GROUPS.splitToHashMap(mmml);
		String mml = "";

		/*
		 * Map players to channels and append all the players MML. This mapping
		 * is also used to send packets to the close the gui of the musicians
		 */
		Set<String> keys = playerMML.keySet();
		Iterator<String> it = keys.iterator();
		Integer ch = 0;
		while (it.hasNext()) {
			String playerName = it.next();
			playerChannel.put(playerName, ch.toString());
			ch++;
			mml += playerMML.get(playerName);
		}

		
        try {
            mmlBuf = mml.getBytes( "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
        is = new java.io.ByteArrayInputStream(mmlBuf);

        // ANTLR4 MML Parser BEGIN
        MMLToMIDI mmlTrans = new MMLToMIDI(); 
        ANTLRInputStream input = null;

		try {
			input = new ANTLRInputStream(is);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        MMLLexer lexer = new MMLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MMLParser parser = new MMLParser(tokens);
        //parser.removeErrorListeners();
        //parser.addErrorListener(new UnderlineListener());
        parser.setBuildParseTree(true);
        ParseTree tree = parser.band();
        
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(mmlTrans, tree);
        // ANTLR4 MML Parser END
		
		ModLogger.logInfo("playerChannel: " + playerChannel);
		ModLogger.logInfo("Playing for:   " + playID);

		try {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			/*
			 * This is where we setup an alternate Soundbank e.g.
			 */
//			URL file = MCJammer.class.getResource("/assets/"
//					+ soundFont.getResourceDomain() + "/"
//					+ soundFont.getResourcePath());
//			ModLogger.logInfo("MMLPlayer.mmlPlay soundfont path: "
//					+ file);
//
//			try {
//				sbJammer = MidiSystem.getSoundbank(file);
//			} catch (InvalidMidiDataException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());

//			InstrumentEnum[] instrEnum = InstrumentEnum.values();
//
//			for (int i = 0; i < instrEnum.length; i++) {
//				instrument = sbJammer.getInstrument(new Patch(0,
//						instrEnum[i].MidiPatchID));
//				ModLogger.logInfo("Set Patches: " + instrEnum[i].NameHi + ", "
//						+ instrEnum[i].MidiPatchID + ", loaded: "
//						+ synthesizer.loadInstrument(instrument));
//			}

//			synthesizer.loadAllInstruments(sbJammer);
			
			sequencer = MidiSystem.getSequencer();
			sequencer.addMetaEventListener(this);
			sequencer.setMicrosecondPosition(0l);
			sequencer.setTempoInBPM((float) masterTempo);

			Sequence seq = mmlTrans.getSequence();
			
			sequencer.open();
			for (Transmitter t : sequencer.getTransmitters()) {
				t.setReceiver(synthesizer.getReceiver());
			}
			
//			sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
			
			sequencer.setSequence(seq);
			sequencer.start();

			return true;

		} catch (Exception ex) {
			MMLManager.getMMLManager().deregisterThread(playID);
			if (sequencer != null && sequencer.isOpen())
				sequencer.close();
			if (synthesizer != null && synthesizer.isOpen())
				synthesizer.close();
			ModLogger.logInfo("mlPlay failed midi TRY " + ex);
			return false;
		}
	}

	public synchronized void mmlKill(String ID) {

		if (playID == null)
			return;

		if (!playerChannel.isEmpty() && playerChannel.containsKey(ID)) {
			/*
			 * If this is a JAM, tell the JAMMERs they are done. If you're a
			 * member, but did not queue MML then your request to close will be
			 * ignored. Solo players force close themselves.
			 */
			ModLogger.logInfo("MusicLibCP.mmlKil: " + ID);
			if (sequencer != null && sequencer.isOpen()) {
				sequencer.stop();
				sequencer.setMicrosecondPosition(0L);
				sequencer.removeMetaEventListener(this);
			}
	
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
			if (sequencer != null && sequencer.isOpen())
				sequencer.close();
			if (synthesizer != null && synthesizer.isOpen())
				synthesizer.close();

			Set<String> keys = playerChannel.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String playerName = it.next();
				PacketPlayStop packetPlayStop = new PacketPlayStop(playerName);
				MCJammer.proxy.packetCLL_sendToServer(packetPlayStop);
			}

			playID = null;
			String resultID = GROUPS.getMembersGroupID(ID) != null ? GROUPS
					.getMembersGroupID(ID) : ID;
			MMLManager.getMMLManager().deregisterThread(resultID);
		}
	}

	@Override
	public void meta(MetaMessage event) {
		if (event.getType() == 47) { // end of stream
			sequencer.stop();
			sequencer.setMicrosecondPosition(0L);
			sequencer.removeMetaEventListener(this);
			ModLogger.logInfo("MetaMessage EOS event for: " + playID);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
			if (sequencer != null && sequencer.isOpen())
				sequencer.close();
			if (synthesizer != null && synthesizer.isOpen())
				synthesizer.close();

			/*
			 * If this is a JAM or Solo, tell the player(s) they are done
			 */
			MMLManager.getMMLManager().deregisterThread(playID);
			if (!playerChannel.isEmpty()) {

				Set<String> keys = playerChannel.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String playerName = it.next();
					PacketPlayStop packetPlayStop = new PacketPlayStop(
							playerName);
					MCJammer.proxy.packetCLL_sendToServer(packetPlayStop);
				}
			}
		}
	}
}
