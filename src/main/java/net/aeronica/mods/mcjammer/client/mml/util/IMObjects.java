package net.aeronica.mods.mcjammer.client.mml.util;

public interface IMObjects {
    public static enum Type {INST_BEGIN, TEMPO, INST, PART, NOTE, REST, INST_END, DONE};
    public Type getType();
}