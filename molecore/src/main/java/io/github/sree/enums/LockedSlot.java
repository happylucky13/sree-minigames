package io.github.sree.enums;

import org.bukkit.inventory.EquipmentSlot;

public enum LockedSlot {
    BOOTS(1, EquipmentSlot.FEET),
    LEGGINGS(2, EquipmentSlot.LEGS),
    CHESTPLATE(3, EquipmentSlot.CHEST),
    HELMET(4, EquipmentSlot.HEAD);

    private final int value;
    private final EquipmentSlot equipmentSlot;

    LockedSlot(int value, EquipmentSlot equipmentSlot) {
        this.value = value;
        this.equipmentSlot = equipmentSlot;
    }

    public int getValue() {
        return value;
    }

    public EquipmentSlot getSlot() {
        return equipmentSlot;
    }
}
