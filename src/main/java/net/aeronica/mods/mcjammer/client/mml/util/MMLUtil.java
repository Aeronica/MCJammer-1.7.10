package net.aeronica.mods.mcjammer.client.mml.util;

public class MMLUtil {

    private static final int[] doerayme = { 50, 9, 11, 0, 2, 4, 5, 7 }; // int[0]
                                                                        // is
                                                                        // not
                                                                        // used

    public static int getMIDINote(int rawNote, int mmlOctave) {
        int doreNote = rawNote - 64; // A=1, G=7
        int octave = (mmlOctave * 12) + 12; // 12semitones for each octave

        return octave + doerayme[doreNote]; // add lookup number of
    }
    public static int getMIDINote(int rawNote, int mmlOctave, boolean rest) {
        int midiNote = getMIDINote (rawNote, mmlOctave);
        return midiNote + 128;
    }
}
