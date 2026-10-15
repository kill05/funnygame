package com.github.kill05.funnygame;

import java.util.Random;
import java.util.stream.Collectors;

public final class StringUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateAlphanumeric(int length) {
        return new Random().ints(length, 0, CHARS.length())
                .mapToObj(CHARS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
