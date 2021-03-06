package net.aeronica.mods.mcjammer.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.ArrayList;
import java.util.List;

import net.aeronica.mods.mcjammer.common.libs.ModInfo;
import net.aeronica.mods.mcjammer.common.net.packets.CLLPacket;
import net.aeronica.mods.mcjammer.common.net.packets.PacketGroupJoin;
import net.aeronica.mods.mcjammer.common.net.packets.PacketGroupManage;
import net.aeronica.mods.mcjammer.common.net.packets.PacketGroupSync;
import net.aeronica.mods.mcjammer.common.net.packets.PacketKey;
import net.aeronica.mods.mcjammer.common.net.packets.PacketMusicScroll;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlayJam;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlaySolo;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlayStop;
import net.aeronica.mods.mcjammer.common.net.packets.PacketPlayerProps;
import net.aeronica.mods.mcjammer.common.net.packets.PacketQueueJAM;
import net.aeronica.mods.mcjammer.common.net.packets.PacketStatusSync;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Combustible Lemon Launcher
 * @author Phil Julian (aka iBuilder99)<br>
 * Adapted from <a href='http://www.minecraftforge.net/wiki/Netty_Packet_Handling'>Minecraft Forge Wiki</a>
 */

@ChannelHandler.Sharable
public class CLLPacketHandler extends MessageToMessageCodec<FMLProxyPacket, CLLPacket>{

	private static ArrayList<Class<? extends CLLPacket>> registeredPackets = new ArrayList<Class<? extends CLLPacket>>();
	
	public static void registerPacket(Class <? extends CLLPacket> packetClass){
		if(!registeredPackets.contains(packetClass)){
			registeredPackets.add(packetClass);
		}
	}
	
	public static void registerPackets(){
		registerPacket(PacketGroupManage.class);
		registerPacket(PacketGroupSync.class);
		registerPacket(PacketKey.class);
		registerPacket(PacketMusicScroll.class);
		registerPacket(PacketPlayerProps.class);
		registerPacket(PacketQueueJAM.class);
		registerPacket(PacketPlayJam.class);
		registerPacket(PacketPlaySolo.class);
		registerPacket(PacketPlayStop.class);
		registerPacket(PacketStatusSync.class);
		registerPacket(PacketGroupJoin.class);
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, CLLPacket msg, List<Object> out) throws Exception {
		Class<? extends CLLPacket> packetClass = msg.getClass(); 								   // Retrieve packets class
		ByteBuf writeBuffer = Unpooled.buffer();												   // Create a Netty buffer
		ByteBufOutputStream bbos = new ByteBufOutputStream(writeBuffer);						   // Create Netty output stream buffer (wrapper for ByteBuf)
		if(registeredPackets.contains(packetClass)){ 											   // Is this class registered?
			writeBuffer.writeByte(registeredPackets.indexOf(packetClass)); 						   // Write the classes index to determine the class in later decoding
			msg.writeDataTo(bbos); 												   		   		   // Let the CLLPacket instance write to the Netty buffer
			FMLProxyPacket packet = new FMLProxyPacket(writeBuffer.copy(), ModInfo.CHANNEL); // Instantiate a FMLProxyPacket with the written data
			bbos.close();																		   // Close the Netty output stream buffer
			out.add(packet); 																	   // Add it to the 'out'-list
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void packetCLLHandleClientSide(CLLPacket packet){
		packet.handleClientSide(Minecraft.getMinecraft().thePlayer);
	}
	
	private void packetCLLHandleServerSide(CLLPacket packet, ChannelHandlerContext context){
		INetHandler netHandler = context.channel().attr(NetworkRegistry.NET_HANDLER).get();
		packet.handleServerSide(((NetHandlerPlayServer) netHandler).playerEntity);
	}

	@Override
	protected void decode(ChannelHandlerContext context, FMLProxyPacket msg, List<Object> out) throws Exception {
		ByteBuf packetPayload = msg.payload();
		byte indexOfClass = packetPayload.readByte();
		ByteBufInputStream bbis = new ByteBufInputStream(packetPayload.slice());
		Class<? extends CLLPacket> packetClass = registeredPackets.get(indexOfClass);
		if(packetClass != null){
			CLLPacket packetCLL = packetClass.newInstance();
			packetCLL.readDataFrom(bbis);
			switch(FMLCommonHandler.instance().getEffectiveSide()){
			case CLIENT:				
				packetCLLHandleClientSide(packetCLL);
				break;
			case SERVER:
				packetCLLHandleServerSide(packetCLL, context);
				break;
			}
			bbis.close();
			out.add(packetCLL);
		}
	}
}
