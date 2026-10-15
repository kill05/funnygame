package com.github.kill05.funnygame;

public final class CommandUtils {

    /**
     * Removes the slash before the command if present, else it returns the input.
     *
     * @param input the input
     * @return the input without the slash
     */
    public static String removeSlash(String input) {
        if (input.startsWith("/")) {
            return input.substring(1);
        }

        return input;
    }

    /**
     * Appends the slash before the command if absent, else it returns the input,
     *
     * @param input the input
     * @return the input with the slash
     */
    public static String appendSlash(String input) {
        if (!input.startsWith("/")) {
            return "/" + input;
        }

        return input;
    }




}
