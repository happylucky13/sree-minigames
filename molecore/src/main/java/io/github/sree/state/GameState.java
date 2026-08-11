package io.github.sree.state;

import io.github.sree.enums.Objective;
import io.github.sree.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private final Set<UUID> alivePlayers = new HashSet<>();
    private final Map<UUID, Role> roleMap = new HashMap<>();

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
        return roleMap.entrySet().stream()
                .anyMatch(entry ->
                        entry.getValue() == role &&
                        alivePlayers.contains(entry.getKey())
                );
    }

    public Map<UUID, Role> getRoleMap() {
        return Collections.unmodifiableMap(roleMap);
    }

    public void addPlayerToRoleMap(UUID id, Role role) {
        roleMap.put(id, role);
    }

    public void resetGame() {
        roleMap.clear();
        alivePlayers.clear();
    }

    public Role getRole(Player player) {
        return roleMap.get(player.getUniqueId());
    }

    public Set<Player> getPlayersWithRole(Role role) {
        return roleMap.entrySet().stream()
                .filter(entry -> entry.getValue() == role)
                .map(entry -> Bukkit.getPlayer(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
