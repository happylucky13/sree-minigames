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

    public void setAlivePlayers(Set<UUID> players) {
        alivePlayers.addAll(players);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
    public boolean isGracePeriod() {
        return gracePeriod;
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

    public Set<Player> getAttackedPlayers(Player player) {
        return playersMap.get(player.getUniqueId()).getCombatTag().keySet();
    }

    public void setAttackedPlayer(Player attacker, Player target, double damageDealt) {
        playersMap.get(attacker.getUniqueId()).getCombatTag().put(target, damageDealt);
    }

    public void removeAttackedPlayer(Player attacker, Player target) {
        playersMap.get(attacker.getUniqueId()).getCombatTag().remove(target);
    }

    public void resetGame() {
        playersMap.clear();
        alivePlayers.clear();
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

    public void lockSlots(Player player) {
        PlayerState playerState = playersMap.get(player.getUniqueId());

        for (LockedSlot slot : EnumSet.allOf(LockedSlot.class)) {
            if (playerState.getKills() >= slot.getValue()) {
                playerState.getLockedSlots().add(slot);
            }
        }
    }
}