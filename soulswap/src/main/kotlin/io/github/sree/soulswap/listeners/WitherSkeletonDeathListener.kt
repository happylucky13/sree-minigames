package io.github.sree.soulswap.listeners

import io.github.sree.soulswap.state.GameState
import org.bukkit.Material
import org.bukkit.entity.WitherSkeleton
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

internal class WitherSkeletonDeathListener(val gameState: GameState) : Listener {

    @EventHandler
    fun onWitherSkeletonDeath(event: EntityDeathEvent) {
        if (!gameState.gameStarted || event.entity !is WitherSkeleton) return

        if (Math.random() <= 0.1) event.drops.add(ItemStack(Material.WITHER_SKELETON_SKULL))
    }
}