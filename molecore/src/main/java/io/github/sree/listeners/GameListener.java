package io.github.sree.listeners;

import io.github.sree.state.GameManager;
import org.bukkit.event.Listener;

public abstract class GameListener implements Listener {
    protected final GameManager gameManager;

    protected GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
