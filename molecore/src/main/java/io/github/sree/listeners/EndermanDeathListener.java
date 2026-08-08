package io.github.sree.listeners;

import io.github.sree.enums.Objective;
import io.github.sree.state.GameManager;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EndermanDeathListener extends GameListener {

    public EndermanDeathListener(GameManager gameManager) {
        super(gameManager);
    }

    @EventHandler
    public void onEndermanDeath(EntityDeathEvent event) {
        if (!gameManager.isGameStarted()) {
            return;
        }

        if (gameManager.getSettings().objective() != Objective.DRAGON) {
            return;
        }

        if (!(event.getEntity() instanceof Enderman)) {
            return;
        }

        if (Math.random() < 0.25) {
            event.getDrops().add(new ItemStack(Material.ENDER_PEARL));
        }
    }
}