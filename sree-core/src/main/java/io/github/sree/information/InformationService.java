package io.github.sree.information;

import org.bukkit.entity.Player;

import java.util.*;

public class InformationService {

    private final Map<UUID, EnumSet<InformationChannel>> playerInfo = new HashMap<>();

    private void ensureRegistered(Player player) {
        playerInfo.computeIfAbsent(player.getUniqueId(), ignored -> EnumSet.allOf(InformationChannel.class));
    }

    public boolean allows(Player player, InformationChannel permission) {
        ensureRegistered(player);
        return playerInfo.get(player.getUniqueId()).contains(permission);
    }

    public void set(Player player, EnumSet<InformationChannel> permissions) {
        ensureRegistered(player);
        playerInfo.put(player.getUniqueId(), EnumSet.copyOf(permissions));
    }

    public void set(Collection<Player> players, EnumSet<InformationChannel> permissions) {
        players.forEach(player -> set(player, permissions));
    }

    public void reset(Player player) {
        ensureRegistered(player);
        playerInfo.put(player.getUniqueId(), EnumSet.allOf(InformationChannel.class));
    }

    public void reset(Collection<Player> players) {
        players.forEach(this::reset);
    }

    public void allow(Player player, InformationChannel permission) {
        ensureRegistered(player);
        playerInfo.get(player.getUniqueId()).add(permission);
    }

    public void allow(Collection<Player> players, InformationChannel permission) {
        players.forEach(player -> allow(player, permission));
    }

    public void deny(Player player, InformationChannel permission) {
        ensureRegistered(player);
        playerInfo.get(player.getUniqueId()).remove(permission);
    }

    public void deny(Collection<Player> players, InformationChannel permission) {
        players.forEach(player -> deny(player, permission));
    }
}
