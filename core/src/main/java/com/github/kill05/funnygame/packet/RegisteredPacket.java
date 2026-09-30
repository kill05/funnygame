package com.github.kill05.funnygame.packet;

public record RegisteredPacket<T extends Packet>(
        int id,
        Class<T> clazz,
        PacketDeserializer<T> deserializer,
        PacketHandler<T> handler
) {

}