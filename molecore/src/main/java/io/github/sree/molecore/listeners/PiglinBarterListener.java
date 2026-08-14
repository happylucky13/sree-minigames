package io.github.sree.molecore.listeners;

import io.github.sree.molecore.enums.Objective;
import io.github.sree.molecore.state.GameState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

public class PiglinBarterListener extends GameListener {

    public PiglinBarterListener(GameState gameState) {
        super(gameState);
    }

    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        if (gameState.getSettings().objective() != Objective.DRAGON) {
            return;
        }

        if (Math.random() < 0.25) {
            event.getOutcome().add(new ItemStack(Material.ENDER_PEARL, 2));
        }
    }
}
