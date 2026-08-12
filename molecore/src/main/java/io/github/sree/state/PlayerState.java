package io.github.sree.state;

import io.github.sree.enums.LockedSlot;
import io.github.sree.enums.Role;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class PlayerState {
    private Role role;
    private int kills = 0;
    private EnumSet<LockedSlot> lockedSlots = EnumSet.noneOf(LockedSlot.class);
    private Map<Player, Double> damageDealtToPlayer;

    public Role getRole() {
        return role;
    }

    public Map<Player, Double> getCombatTag() {
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
