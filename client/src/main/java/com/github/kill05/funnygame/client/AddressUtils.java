package com.github.kill05.funnygame.client;

import com.github.kill05.funnygame.connection.ConnectionConstants;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public class AddressUtils {

    @NotNull
    public static InetSocketAddress addressFromString(@NotNull String address) throws IllegalArgumentException {
        String hostname;
        int port;

        String[] split = address.split(":");

        if (split.length > 2) {
            throw new IllegalArgumentException("Invalid address.");
        } else if (split.length == 2) {
            int readPort;

            try {
                readPort = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid port.");
            }

            hostname = split[0];
            port = readPort;
        } else {
            hostname = address;
            port = ConnectionConstants.DEFAULT_PORT;
        }

        return new InetSocketAddress(hostname, port);
    }

}
