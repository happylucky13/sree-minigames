package io.github.sree.listeners;

import io.github.sree.state.GameManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends GameListener {

    public PlayerDeathListener(GameManager gameManager) {
        super(gameManager);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!gameManager.isGameStarted()) {
            return;
        }

        gameManager.handlePlayerDeath(event);
    }
}