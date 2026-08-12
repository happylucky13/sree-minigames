package io.github.sree.information;

import org.bukkit.entity.Player;

import java.util.*;

public class InformationService {

    private final Map<UUID, EnumSet<InformationChannel>> playerInfo = new HashMap<>();
    private final List<InformationChangeListener> listeners = new ArrayList<>();

    public void addListener(InformationChangeListener listener) {
        listeners.add(listener);
    }

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

        notifyListeners(player);
    }

    public void set(Collection<Player> players, EnumSet<InformationChannel> channels) {
        players.forEach(player -> set(player, channels));
    }

    public void reset(Player player) {
        ensureRegistered(player);

        playerInfo.put(player.getUniqueId(), EnumSet.allOf(InformationChannel.class));

        notifyListeners(player);
    }

    public void reset(Collection<Player> players) {
        players.forEach(this::reset);
    }

    public void allow(Player player, InformationChannel channel) {
        ensureRegistered(player);

        EnumSet<InformationChannel> playerPermissions = playerInfo.get(player.getUniqueId());

        if (playerPermissions.add(channel)) {
            notifyListeners(player);
        }
    }

    public void allow(Collection<Player> players, InformationChannel channel) {
        players.forEach(player -> allow(player, channel));
    }

    public void deny(Player player, InformationChannel channel) {
        ensureRegistered(player);

        EnumSet<InformationChannel> playerPermissions = playerInfo.get(player.getUniqueId());

        if (playerPermissions.remove(channel)) {
            notifyListeners(player);
        }
    }

    public void deny(Collection<Player> players, InformationChannel channel) {
        players.forEach(player -> deny(player, channel));
    }

    private void notifyListeners(Player player) {
        EnumSet<InformationChannel> permissions = EnumSet.copyOf(playerInfo.get(player.getUniqueId()));

        InformationChangedEvent event = new InformationChangedEvent(player, permissions);

        listeners.forEach(listener -> listener.onInformationChanged(event));
    }
}
