package io.github.sree.spectators;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Spectators {

    private final Set<UUID> spectators = new HashSet<>();

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void handleSpectatorDimensionChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (spectators.contains(player.getUniqueId())) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
}
