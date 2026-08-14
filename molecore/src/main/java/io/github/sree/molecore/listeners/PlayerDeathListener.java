package io.github.sree.molecore.listeners;

import io.github.sree.molecore.state.GameManager;

import io.github.sree.molecore.state.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends GameListener {

    private final GameManager gameManager;

    public PlayerDeathListener(GameState gameState, GameManager gameManager) {
        super(gameState);
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        gameManager.handlePlayerDeath(event);
    }
}