package com.github.kill05.funnygame.packet;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Interface used to represent a data packet
 * that can be sent either by the client or by the server.
 *
 * When a packet is sent, the following data is appended before the packet data:
 * 2 bytes: magic bytes
 * 4 bytes: packet length
 * 1 byte: packet id
 * 0+ bytes: packet data
 */
public interface Packet {

    void serialize(@NotNull PacketOutputStream out) throws IOException;

}
