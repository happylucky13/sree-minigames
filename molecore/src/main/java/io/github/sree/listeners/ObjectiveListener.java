package io.github.sree.listeners;

import io.github.sree.state.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ObjectiveListener extends GameListener {

    public ObjectiveListener(GameManager gameManager) {
        super(gameManager);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!gameManager.isGameStarted()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            gameManager.checkObjectiveCompletion(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!gameManager.isGameStarted()) {
            return;
        }

        if (event.getWhoClicked() instanceof Player player) {
            gameManager.checkObjectiveCompletion(player);
        }
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        if (!gameManager.isGameStarted()) {
            return;
        }

        if (event.getWhoClicked() instanceof Player player) {
            gameManager.checkObjectiveCompletion(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!gameManager.isGameStarted()) {
            return;
        }

        if (event.getPlayer() instanceof Player player) {
            gameManager.checkObjectiveCompletion(player);
        }
    }
}
