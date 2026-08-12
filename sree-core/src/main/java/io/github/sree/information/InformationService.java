package io.github.sree.information;

import org.bukkit.entity.Player;

import java.util.*;

public class InformationService {

    private final Map<UUID, EnumSet<InformationChannel>> playerInfo = new HashMap<>();

    private void ensureRegistered(Player player) {
        playerInfo.computeIfAbsent(player.getUniqueId(), ignored -> EnumSet.allOf(InformationChannel.class));
    }

    public boolean allows(Player player, InformationChannel channel) {
        ensureRegistered(player);
        return playerInfo.get(player.getUniqueId()).contains(channel);
    }

    public void set(Player player, EnumSet<InformationChannel> channels) {
        ensureRegistered(player);
        playerInfo.put(player.getUniqueId(), EnumSet.copyOf(channels));
    }

    public void set(Collection<Player> players, EnumSet<InformationChannel> channels) {
        players.forEach(player -> set(player, channels));
    }

    public void reset(Player player) {
        ensureRegistered(player);
        playerInfo.put(player.getUniqueId(), EnumSet.allOf(InformationChannel.class));
    }

    public void reset(Collection<Player> players) {
        players.forEach(this::reset);
    }

    public void allow(Player player, InformationChannel channel) {
        ensureRegistered(player);
        playerInfo.get(player.getUniqueId()).add(channel);
    }

    public void allow(Collection<Player> players, InformationChannel channel) {
        players.forEach(player -> allow(player, channel));
    }

    public void deny(Player player, InformationChannel channel) {
        ensureRegistered(player);
        playerInfo.get(player.getUniqueId()).remove(channel);
    }

    public void deny(Collection<Player> players, InformationChannel channel) {
        players.forEach(player -> deny(player, channel));
    }
}
