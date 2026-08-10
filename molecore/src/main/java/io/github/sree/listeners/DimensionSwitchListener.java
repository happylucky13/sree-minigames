package io.github.sree.listeners;

import io.github.sree.state.GameManager;
import io.github.sree.state.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class DimensionSwitchListener extends GameListener {

    private final GameManager gameManager;

    public DimensionSwitchListener(GameState gameState, GameManager gameManager) {
        super(gameState);
        this.gameManager = gameManager;

    }

    @EventHandler
    public void dimensionSwitchEvent(PlayerChangedWorldEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }


    }
}
