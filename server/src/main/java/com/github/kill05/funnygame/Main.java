package com.github.kill05.funnygame;

import com.github.kill05.funnygame.server.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static void main() {
        GameServer server;

        try {
            server = new GameServer();
        } catch (IOException e) {
            log.error("Failed to initialize server.", e);
            return;
        }

        server.start();
    }
}
