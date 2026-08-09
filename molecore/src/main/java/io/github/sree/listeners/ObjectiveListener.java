package io.github.sree.listeners;

import io.github.sree.state.GameManager;
import io.github.sree.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ObjectiveListener extends GameListener {

    private final GameManager gameManager;

    public ObjectiveListener(GameState gameState, GameManager gameManager) {
        super(gameState);
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            gameManager.checkObjective(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            gameManager.checkObjective(player);
        }
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            gameManager.checkObjective(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            gameManager.checkObjective(player);
        }
    }
}
