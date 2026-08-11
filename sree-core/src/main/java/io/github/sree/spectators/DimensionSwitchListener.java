package io.github.sree.spectators;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class DimensionSwitchListener implements Listener {

    private final SpectatorService spectatorService;

    public DimensionSwitchListener(SpectatorService spectatorService) {
        this.spectatorService = spectatorService;
    }

    @EventHandler
    public void onDimensionChange(PlayerChangedWorldEvent event) {
        spectatorService.handleSpectatorDimensionChange(event);
    }
}
