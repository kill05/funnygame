package com.github.kill05.funnygame;

import com.github.kill05.funnygame.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static void main() {
        Client client;

        try {
            client = new Client();
        } catch (IOException e) {
            log.error("Failed to initialize client.", e);
            return;
        }

        client.start();
    }
}