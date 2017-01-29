package net.aeronica.mods.mcjammer.client.core;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.net.packets.PacketKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyHandler {

	private KeyBinding key_openGUI = new KeyBinding("key.openParty", Keyboard.KEY_J,
			ModInfo.ID);
	
	private KeyBinding key_playGUI = new KeyBinding("key.playInstrument", Keyboard.KEY_P,
			ModInfo.ID);


	public KeyHandler() {
		ClientRegistry.registerKeyBinding(key_openGUI);
		ClientRegistry.registerKeyBinding(key_playGUI);
		Minecraft.getMinecraft().gameSettings.loadOptions();
	}

	@SubscribeEvent
	public void tick(KeyInputEvent event) {

		if (key_openGUI.getIsKeyPressed()) {
			PacketKey packetKey = new PacketKey(key_openGUI.getKeyDescription());
			MCJammer.proxy.packetCLL_sendToServer(packetKey);
		} 
		if (key_playGUI.getIsKeyPressed()) {
			PacketKey packetKey = new PacketKey(key_playGUI.getKeyDescription());
			MCJammer.proxy.packetCLL_sendToServer(packetKey);
		}
	}
}
