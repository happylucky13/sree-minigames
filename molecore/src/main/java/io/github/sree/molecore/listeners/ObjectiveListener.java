package io.github.sree.molecore.listeners;

import io.github.sree.molecore.state.GameManager;
import io.github.sree.molecore.state.GameState;
import io.papermc.paper.event.block.BeaconActivatedEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class ObjectiveListener extends GameListener {

    private final GameManager gameManager;

    public ObjectiveListener(GameState gameState, GameManager gameManager) {
        super(gameState);
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onBeaconActivated(BeaconActivatedEvent event) {
        gameManager.handleObjectiveCompletion(event);
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        gameManager.handleObjectiveCompletion(event);
    }
}
