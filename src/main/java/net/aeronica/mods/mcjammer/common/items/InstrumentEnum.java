package net.aeronica.mods.mcjammer.common.items;

import net.aeronica.mods.mcjammer.common.libs.ModInfo;



public enum InstrumentEnum {
	
	MANDOLIN ( 0, 25, "Mandolin",								// subclass ID, midiPatch#, unlocalized
			new Object[] { "mandox", 							// RenderBlockstart, filenames OBJ & png
				new float[] {0f, -79f, 84f},				// gl rotation Equipped rot1
				new float[] {-1.24f, -.62f, -.28f, .55F},				// gl Trans for Equip render
				new float[] {-0.2f, -0.4f, 0.0f, 0.3f},			// gl rotation Inv
			}
			),
	GUITAR ( 1, 24,	"Guitar",									// subclass ID, midiPatch#
			new Object[] { "guitarx", 							// RenderBlockstart, filenames OBJ & png
				new float[] {-41f, -74,  43f},				// gl rotation Equipped rot1
				new float[] {-1.07f, -.65f, -.31f, .75f},			// gl Trans for Equip render
				new float[] {-0.2f, -0.4f, 0.0f, 0.4f}			// gl rotation Inv
			}
			),
	FLUTE ( 2, 73,	"Flute",									// subclass ID, midiPatch#
			new Object[] { "flute", 							// RenderBlockstart, filenames OBJ & png
				new float[] {-13f, -74f,  56f},				// gl rotation Equipped rot1
				new float[] {-1.15f, -.90f, -.48f, 1f},				// gl Trans for Equip render
				new float[] {0.0f, -0.5f, 0.0f, 0.6f}			// gl rotation Inv
			}
			),
	BASS ( 3, 34,	"Bass",									// subclass ID, midiPatch#
			new Object[] { "bassx", 						// RenderBlockstart, filenames OBJ & png
			new float[] {-30f, -74f,  53f},				// gl rotation Equipped rot1
			new float[] {-1.04f, -.65f, -.34f, .70f},			// gl Trans for Equip render
			new float[] {-0.2f, -0.5f, 0.0f, 0.32f}				// gl rotation Inv
			}
			),
	SAX ( 4, 66,	"Sax",									// subclass ID, midiPatch#
			new Object[] { "saxx", 							// RenderBlockstart, filenames OBJ & png
			new float[] {0f, 0f,  68f},				// gl rotation Equipped rot1
			new float[] {-0.73f, -.82f, 0f, 0.8f},			// gl Trans for Equip render
			new float[] {-0.2f, -0.4f, 0.0f, 0.5f}				// gl rotation Inv
			}
			),
	CLARINET ( 5, 71,	"Clarinet",								// subclass ID, midiPatch#
			new Object[] { "clarinetx", 					// RenderBlockstart, filenames OBJ & png
			new float[] {90f, -20f, 90f},				// gl rotation Equipped rot1
			new float[] {-1.21f, -.48f, -.03f, 0.7f},			// gl Trans for Equip render
			new float[] {0.0f, 0.0f, 0.0f, 0.4f}				// gl rotation Inv
			}
			)
	;
	
//-----------------------------------------------------------------------------------
	
	private InstrumentEnum (int par1, int par2, String unloc, Object[] rendData ){
		String fnam			= (String)  rendData[0];
		
		this.SubID 			= par1;
		this.MidiPatchID	= par2;
		this.NameHi			= unloc;
		this.RenderOBJ		= ModInfo.ID.toLowerCase()+":models/" + fnam + ".obj";
		this.RenderTEX		= ModInfo.ID.toLowerCase()+":textures/models/"+fnam+".png";
		this.rendEQUIProt1	= (float[]) rendData[1];
		this.rendEQUIPtrans	= (float[]) rendData[2];
		this.rendINVtrans	= (float[]) rendData[3];
		
	}
		
	
	public int 			SubID;
	public int 			MidiPatchID;
	public String		NameHi;
	public String		RenderOBJ;
	public String		RenderTEX;
	public float[]		rendEQUIProt1;
	public float[]		rendEQUIPtrans;
	public float[]		rendINVtrans;
}