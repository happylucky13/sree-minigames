package io.github.sree.state;

import io.github.sree.enums.LockedSlot;
import io.github.sree.enums.Objective;
import io.github.sree.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private final Set<UUID> alivePlayers = new HashSet<>();
    private final Map<UUID, PlayerState> playersMap = new HashMap<>();

    private GameSettings settings = new GameSettings(2, Objective.BEACON, 900);

    private boolean gameStarted;
    private boolean gracePeriod;

    private boolean sabotageOnCooldown;

    public void setSettings(int moleCount, Objective objective, int gracePeriodTime) {
        settings = new GameSettings(moleCount, objective, gracePeriodTime);
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
    public void setGracePeriod(boolean gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
    public void setSabotageOnCooldown(boolean sabotageOnCooldown) {
        this.sabotageOnCooldown = sabotageOnCooldown;
    }

    public void setAlivePlayers(Set<UUID> players) {
        alivePlayers.addAll(players);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
    public boolean isGracePeriod() {
        return gracePeriod;
    }
    public boolean isSabotageOnCooldown() {
        return sabotageOnCooldown;
    }

    public void markDead(UUID uuid) {
        alivePlayers.remove(uuid);
    }

    public boolean hasAlivePlayersWithRole(Role role) {
        return playersMap.entrySet().stream()
                .anyMatch(entry ->
                        entry.getValue().getRole() == role &&
                        alivePlayers.contains(entry.getKey())
                );
    }

    public Map<UUID, PlayerState> getPlayersMap() {
        return Collections.unmodifiableMap(playersMap);
    }

    public void addPlayerToPlayersMap(UUID id, Role role) {
        PlayerState playerState = new PlayerState();
        playerState.setRole(role);
        playersMap.put(id, playerState);
    }

    public void incrementKills(Player player) {
        playersMap.get(player.getUniqueId()).incrementKills();
    }

    public void resetGame() {
        playersMap.clear();
        alivePlayers.clear();
        gameStarted = false;
        gracePeriod = false;
        sabotageOnCooldown = false;
    }

    public Role getRole(Player player) {
        return playersMap.get(player.getUniqueId()).getRole();
    }

    public Set<Player> getPlayersWithRole(Role role) {
        return playersMap.entrySet().stream()
                .filter(entry -> entry.getValue().getRole() == role)
                .map(entry -> Bukkit.getPlayer(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public EnumSet<LockedSlot> getLockedSlots(Player player) {
        return getPlayerState(player).getLockedSlots();
    }

    public void lockSlots(Player player) {
        PlayerState playerState = playersMap.get(player.getUniqueId());
        EnumSet<LockedSlot> lockedSlots = EnumSet.noneOf(LockedSlot.class);

        for (LockedSlot slot : EnumSet.allOf(LockedSlot.class)) {
            if (playerState.getKills() >= slot.getValue()) {
                lockedSlots.add(slot);
            }
        }

        playerState.setLockedSlots(lockedSlots);
    }

    public void unlockSlots(Player player) {
        PlayerState playerState = playersMap.get(player.getUniqueId());
        playerState.setLockedSlots(EnumSet.noneOf(LockedSlot.class));
    }

    private PlayerState getPlayerState (Player player) {
        return playersMap.computeIfAbsent(player.getUniqueId(), ignored -> new PlayerState());
    }
}