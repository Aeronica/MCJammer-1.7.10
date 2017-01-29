package net.aeronica.mods.mcjammer.common.player;

import java.util.Collection;
import java.util.HashMap;

import net.aeronica.mods.mcjammer.MCJammer;
import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlayerProps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	/**
	 * Note that a single entity can have multiple extended properties, so each
	 * property should have a unique name.
	 */
	public final static String EXT_PROP_NAME = ModInfo.ID + "_"
			+ "PlayerJamProps";

	private final EntityPlayer player;
	/**
	 * The players extended properties for playing music and jamming.
	 */
	private boolean isLeader, inJam, isPlaying, inInventory;
	/*
	 * Generic string for passing params from server to client
	 */
	private String sParam1, sParam2, sParam3;

	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;
		this.isLeader = this.inJam = this.isPlaying = false;
		this.inInventory = false;
		this.sParam1 = this.sParam2 = this.sParam3 = "";
	}

	/**
	 * Used to register these extended properties for the player during
	 * EntityConstructing event
	 */
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME,
				new ExtendedPlayer(player));
	}

	/**
	 * Returns PlayerJamProps properties for player
	 */
	public static final ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
	}

	/**
	 * Saves the extended properties
	 */
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		// We need to create a new tag compound that will save everything for
		// our Extended Properties
		NBTTagCompound properties = new NBTTagCompound();

		// save our properties
		properties.setBoolean("inJam", this.inJam);
		properties.setBoolean("isLeader", this.isLeader);
		properties.setBoolean("isPlaying", this.isPlaying);
		properties.setBoolean("inInventory", this.inInventory);		
		properties.setString("sParam1", this.sParam1);
		properties.setString("sParam2", this.sParam2);
		properties.setString("sParam3", this.sParam3);

		/**
		 * Now add our custom tag to the player's tag with a unique name (our
		 * property's name). This will allows the saving of multiple types of
		 * properties and distinguishing between them. It is needed to avoid
		 * conflicts between our tag names and vanilla tag names, or tags added
		 * by other mods.
		 */
		compound.setTag(EXT_PROP_NAME, properties);
	}

	/**
	 * Loads the extended properties
	 */
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		// Fetch the unique tag compound we set for this class of Extended
		// Properties
		NBTTagCompound properties = (NBTTagCompound) compound
				.getTag(EXT_PROP_NAME);

		// If the compound does not exist create it.
		if (properties == null)
			this.saveNBTData(compound);

		// load our properties
		this.inJam = properties.getBoolean("inJam");
		this.isLeader = properties.getBoolean("isLeader");
		this.isPlaying = properties.getBoolean("isPlaying");
		this.inInventory = properties.getBoolean("inInventory");

		System.out
				.println("+++ PlayerJamProps loadNBTData: JAM/Leader/Playing "
						+ inJam + "/" + isLeader + "/" + isPlaying + "/" + inInventory);
	}

	@Override
	public void init(Entity entity, World world) {
	}

	public void clearAll() {
		this.isLeader = this.inJam = this.isPlaying = this.inInventory = false;
		this.sParam1 = this.sParam2 = this.sParam3 = "";
		this.sync();
	}

	public void setJam(boolean value) {
		this.inJam = value;
		this.sync();
	}

	public boolean getJam() {
		return this.inJam;
	}

	public void setLeader(boolean value) {
		this.isLeader = value;
		this.sync();
	}

	public boolean getLeader() {
		return this.isLeader;
	}

	public void setPlaying(boolean value) {
		this.isPlaying = value;
		this.sync();
	}

	public boolean getPlaying() {
		return this.isPlaying;
	}

	public void setInInventory(boolean value) {
		this.inInventory = value;
		this.sync();
	}
	
	public boolean getInInventory() {
		return this.inInventory;
	}
	
	public void setSParams(String sParam1, String sParam2, String sParam3) {
		this.sParam1 = sParam1;
		this.sParam2 = sParam2;
		this.sParam3 = sParam3;
		this.sync();
	}

	public String getSParam1() {
		return sParam1;
	}

	public String getSParam2() {
		return sParam2;
	}

	public String getSParam3() {
		return sParam3;
	}

	/**
	 * The values returned are an index to icons stored in the
	 * instrument_inventory.png texture.
	 * 
	 * @return A collection of the active properties. that is those that are
	 *         "true".
	 */
	public Collection<Integer> getActiveProps() {
		HashMap<String, Integer> activeProps = new HashMap<String, Integer>();
		if (this.inJam)
			activeProps.put("inJam", 2);
		if (this.isLeader)
			activeProps.put("isLeader", 3);
		if (this.isPlaying)
			activeProps.put("isPlaying", 1);
		if (activeProps.isEmpty())
			activeProps.put("inactive", 0);
		return activeProps.values();
	}

	public final void sync() {
		if (!player.worldObj.isRemote) { // We only want to send on server side
			PacketPlayerProps packetPlayerProps = new PacketPlayerProps(
					this.inJam, this.isLeader, this.isPlaying, this.inInventory ,this.sParam1,
					this.sParam2, this.sParam3);
			MCJammer.proxy.packetCLL_sendToPlayer(packetPlayerProps,
					(EntityPlayerMP) player);
		}
	}
}
