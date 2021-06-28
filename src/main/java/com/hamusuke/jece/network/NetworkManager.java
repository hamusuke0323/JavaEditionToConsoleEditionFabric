package com.hamusuke.jece.network;

import com.hamusuke.jece.JECE;
import net.minecraft.util.Identifier;

public class NetworkManager {
    public static final Identifier AUTO_SAVE_WILL_START_PACKET_ID = new Identifier(JECE.MOD_ID, "auto_save_will_start_packet");
    public static final Identifier AUTO_SAVE_PACKET_ID = new Identifier(JECE.MOD_ID, "auto_save_packet");
    public static final Identifier AUTO_SAVE_END_PACKET_ID = new Identifier(JECE.MOD_ID, "auto_save_end_packet");
}
