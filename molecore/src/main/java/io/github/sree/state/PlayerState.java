package io.github.sree.state;

import io.github.sree.enums.LockedSlot;
import io.github.sree.enums.Role;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerState {
    private Role role;
    private int kills = 0;
    private EnumSet<LockedSlot> lockedSlots = EnumSet.noneOf(LockedSlot.class);
    private final Map<UUID, Double> damageDealtToPlayer = new HashMap<>();

    public Role getRole() {
        return role;
    }

    public Map<UUID, Double> getCombatTag() {
        return damageDealtToPlayer;
    }

    public int getKills() {
        return kills;
    }

    public EnumSet<LockedSlot> getLockedSlots() {
        return lockedSlots;
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
