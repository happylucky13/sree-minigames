package io.github.sree.listeners;

import io.github.sree.state.GameState;
import org.bukkit.event.Listener;

public abstract class GameListener implements Listener {
    protected final GameState gameState;

    protected GameListener(GameState gameState) {
        this.gameState = gameState;
    }
}
