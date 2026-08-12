package io.github.sree.listeners;

import io.github.sree.state.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener extends GameListener {
    public InventoryClickListener(GameState gameState) {
        super(gameState);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

    }
}
