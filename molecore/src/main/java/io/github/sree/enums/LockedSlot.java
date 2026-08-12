package io.github.sree.enums;

public enum LockedSlot {
    BOOTS(1),
    LEGGINGS(2),
    CHESTPLATE(3),
    HELMET(4);

    private final int value;

    LockedSlot(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
