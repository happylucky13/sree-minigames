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

    public void set(Player player, EnumSet<InformationChannel> newPermissions) {
        ensureRegistered(player);

        update(player, EnumSet.copyOf(newPermissions));
    }

    public void set(Collection<Player> players, EnumSet<InformationChannel> channels) {
        players.forEach(player -> set(player, channels));
    }

    public void reset(Player player) {
        ensureRegistered(player);

        update(player, EnumSet.allOf(InformationChannel.class));
    }

    public void reset(Collection<Player> players) {
        players.forEach(this::reset);
    }

    public void allow(Player player, InformationChannel channel) {
        ensureRegistered(player);

        EnumSet<InformationChannel> newPermissions = EnumSet.copyOf(playerInfo.get(player.getUniqueId()));
        newPermissions.add(channel);

        update(player, newPermissions);
    }

    public void allow(Collection<Player> players, InformationChannel channel) {
        players.forEach(player -> allow(player, channel));
    }

    public void deny(Player player, InformationChannel channel) {
        ensureRegistered(player);

        EnumSet<InformationChannel> newPermissions = EnumSet.copyOf(playerInfo.get(player.getUniqueId()));
        newPermissions.remove(channel);

        update(player, newPermissions);
    }

    public void deny(Collection<Player> players, InformationChannel channel) {
        players.forEach(player -> deny(player, channel));
    }

    private void update(Player player, EnumSet<InformationChannel> newPermissions) {
        UUID uuid = player.getUniqueId();

        EnumSet<InformationChannel> oldPermissions = playerInfo.get(uuid);

        EnumSet<InformationChannel> changed = EnumSet.copyOf(oldPermissions);
        changed.removeAll(newPermissions);

        EnumSet<InformationChannel> newlyAllowed = EnumSet.copyOf(newPermissions);
        newlyAllowed.removeAll(oldPermissions);

        changed.addAll(newlyAllowed);

        playerInfo.put(uuid, newPermissions);

        if (!changed.isEmpty()) {
            notifyListeners(player, changed);
        }
    }

    private void notifyListeners(Player player, EnumSet<InformationChannel> changedChannels) {
        InformationChangedEvent event = new InformationChangedEvent(player, EnumSet.copyOf(changedChannels));

        listeners.forEach(listener -> listener.onInformationChanged(event));
    }
}
