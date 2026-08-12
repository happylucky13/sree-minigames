package io.github.sree.spectators;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final SpectatorService spectatorService;

    public PlayerJoinListener(SpectatorService spectatorService) {
        this.spectatorService = spectatorService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        spectatorService.handlePlayerJoin(event);
    }
}
