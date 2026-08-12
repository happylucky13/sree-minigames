package io.github.sree.state;

import io.github.sree.enums.LockedSlot;
import io.github.sree.enums.Role;

import java.util.EnumSet;

public class PlayerState {
    private Role role;
    private int kills = 0;
    private EnumSet<LockedSlot> lockedSlots = EnumSet.noneOf(LockedSlot.class);
    private boolean didDamage = false;

    public Role getRole() {
        return role;
    }

    public int getKills() {
        return kills;
    }

    public EnumSet<LockedSlot> getLockedSlots() {
        return lockedSlots;
    }

    public boolean didDamage() {
        return didDamage;
    }

    public void setDidDamage(boolean didDamage) {
        this.didDamage = didDamage;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLockedSlots(EnumSet<LockedSlot> lockedSlots) {
        this.lockedSlots = lockedSlots;
    }

    public void incrementKills() {
        kills ++;
    }
}
