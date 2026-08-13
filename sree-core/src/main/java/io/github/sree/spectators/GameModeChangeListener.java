package io.github.sree.spectators;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameModeChangeListener implements Listener {

    private final SpectatorService spectatorService;

    public GameModeChangeListener(SpectatorService spectatorService) {
        this.spectatorService = spectatorService;
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        spectatorService.handleGameModeChange(event);
    }
}
