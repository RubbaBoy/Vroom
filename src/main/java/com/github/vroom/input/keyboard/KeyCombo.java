package com.github.vroom.input.keyboard;

import java.util.Objects;

public final class KeyCombo {

    private final int key;

    private final int modifiers;

    public KeyCombo(int key) {
        this(key, 0);
    }

    public KeyCombo(int key, int modifiers) {
        this.key = key;
        this.modifiers = modifiers;
    }

    public int getKey() {
        return key;
    }

    public int getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyCombo keyCombo = (KeyCombo) o;

        return key == keyCombo.key && modifiers == keyCombo.modifiers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, modifiers);
    }

    @Override
    public String toString() {
        return "KeyCombo{key=" + key + ", modifiers=" + modifiers + '}';
    }
}
