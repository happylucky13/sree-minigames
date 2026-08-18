package io.github.sree.soulswap

import org.bukkit.entity.Player
import java.util.UUID

internal class GameState {
    private val alivePlayers = mutableSetOf<UUID>()
    private val playerStates = mutableMapOf<UUID, PlayerState>()

    val settings = GameSettings()

    fun addPlayers(players: Collection<Player>) {
        alivePlayers.addAll(players.map { it.uniqueId } )
        playerStates.putAll(players.associate { it.uniqueId to PlayerState(settings.livesLeft) } )
    }
}