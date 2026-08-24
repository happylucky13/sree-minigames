package io.github.sree.soulswap.listeners

import io.github.sree.soulswap.GameManager
import io.github.sree.soulswap.state.GameState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

internal class PlayerDeathListener(
    val gameManager: GameManager,
    val gameState: GameState
) : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (gameState.gameStarted) gameManager.handlePlayerDeath(event)
    }
}