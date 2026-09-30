package com.github.kill05.funnygame.packet;

import com.github.kill05.funnygame.packet.Packet;
import com.github.kill05.funnygame.packet.PacketInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@FunctionalInterface
public interface PacketDeserializer<T extends Packet> {

    T deserialize(@NotNull PacketInputStream in) throws IOException;

}