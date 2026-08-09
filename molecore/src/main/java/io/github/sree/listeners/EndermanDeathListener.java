package io.github.sree.listeners;

import io.github.sree.enums.Objective;
import io.github.sree.state.GameManager;
import io.github.sree.state.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EndermanDeathListener extends GameListener {

    public EndermanDeathListener(GameState gameState) {
        super(gameState);
    }

    @EventHandler
    public void onEndermanDeath(EntityDeathEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        if (gameState.getSettings().objective() != Objective.DRAGON) {
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