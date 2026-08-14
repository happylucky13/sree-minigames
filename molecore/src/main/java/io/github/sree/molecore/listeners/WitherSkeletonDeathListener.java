package io.github.sree.molecore.listeners;

import io.github.sree.molecore.enums.Objective;
import io.github.sree.molecore.state.GameState;
import org.bukkit.Material;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class WitherSkeletonDeathListener extends GameListener {

    public WitherSkeletonDeathListener(GameState gameState) {
        super(gameState);
    }

    @EventHandler
    public void onWitherSkeletonDeath(EntityDeathEvent event) {
        if (!gameState.isGameStarted()) {
            return;
        }

        if (gameState.getSettings().objective() != Objective.BEACON) {
            return;
        }

        if (!(event.getEntity() instanceof WitherSkeleton)) {
            return;
        }

        if (Math.random() < 0.25) {
            event.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
        }
    }
}
