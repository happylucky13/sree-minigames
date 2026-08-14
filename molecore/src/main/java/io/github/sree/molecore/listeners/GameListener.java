package io.github.sree.molecore.listeners;

import io.github.sree.molecore.state.GameState;
import org.bukkit.event.Listener;

public abstract class GameListener implements Listener {
    protected final GameState gameState;

    protected GameListener(GameState gameState) {
        this.gameState = gameState;
    }
}
