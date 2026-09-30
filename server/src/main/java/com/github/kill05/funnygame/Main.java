package com.github.kill05.funnygame;

import com.github.kill05.funnygame.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static void main() {
        Server server;

        try {
            server = new Server();
        } catch (IOException e) {
            log.error("Failed to initialize server.", e);
            return;
        }

        server.start();
    }
}
