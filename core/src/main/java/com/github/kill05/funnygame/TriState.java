package com.github.kill05.funnygame;

public enum TriState {

    TRUE(true),
    FALSE(false),
    NOT_SET(false);

    private final boolean value;

    TriState(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }
}
