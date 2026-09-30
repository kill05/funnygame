package com.github.kill05.funnygame.packet;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public record KeepAlivePacket() implements Packet {

    public KeepAlivePacket(@NotNull PacketInputStream in) {
        this();
    }

    @Override
    public void serialize(@NotNull PacketOutputStream out) throws IOException {

    }
}
