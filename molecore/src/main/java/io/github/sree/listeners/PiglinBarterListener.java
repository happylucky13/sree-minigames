package io.github.sree.listeners;

import io.github.sree.enums.Objective;
import io.github.sree.state.GameManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

public class PiglinBarterListener extends GameListener {

    public PiglinBarterListener(GameManager gameManager) {
        super(gameManager);
    }

    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent event) {
        if (!gameManager.isGameStarted()) {
            return;
        }

        if (gameManager.getSettings().objective() != Objective.DRAGON) {
            return;
        }

        if (Math.random() < 0.25) {
            event.getOutcome().add(new ItemStack(Material.ENDER_PEARL, 2));
        }
    }
}
