package com.github.kill05.funnygame.connection;

public final class ConnectionConstants {

    public static final long KEEP_ALIVE_INTERVAL = 1000L;
    public static final long TIMEOUT_MILLIS = 30_000L;
    public static final long REQUEST_TIMEOUT_MILLIS = 10_000L;

    public static final int DEFAULT_PORT = 12210;

    public static final int PROTOCOL_VERSION = 1;

    public static final short PACKET_MAGIC_BYTES = (short) 0xF411;
    public static final int PACKET_HEADER_SIZE = 7; // 2 magic + 4 length + 1 id

}
