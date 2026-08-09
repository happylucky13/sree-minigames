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

    private GameSettings settings = new GameSettings(2, Objective.WITHER);

    private boolean gameStarted;

    public GameState() {

    }

    public void setSettings(int moleCount, Objective objective) {
        settings = new GameSettings(moleCount, objective);
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public Set<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    public Map<UUID, Role> getRoleMap() {
        return roleMap;
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

    public void assignRoles() {
        List<Player> shuffledPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(shuffledPlayers);

        roleMap.clear();
        alivePlayers.clear();

        for(int i = 0; i < shuffledPlayers.size(); i++) {
            UUID id = shuffledPlayers.get(i).getUniqueId();

            if(i < settings.moleCount()) {
                roleMap.put(shuffledPlayers.get(i).getUniqueId(), Role.MOLE);
                continue;
            }

            roleMap.put(shuffledPlayers.get(i).getUniqueId(), Role.SURVIVOR);
        }

        alivePlayers.addAll(roleMap.keySet());
    }
}
