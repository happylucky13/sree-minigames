package io.github.sree.soulswap

import org.bukkit.entity.Player
import java.util.UUID

internal class GameState {
    val alivePlayers: Set<UUID>
        field = mutableSetOf<UUID>()

    val playerStates: Map<UUID, PlayerState>
        field = mutableMapOf<UUID, PlayerState>()

    val settings = GameSettings()
    var gameStarted = false

    fun addPlayers(players: Collection<Player>) {
        alivePlayers.addAll(players.map { it.uniqueId } )
        playerStates.putAll(players.associate { it.uniqueId to PlayerState(settings.livesLeft) } )
    }

    fun removePlayer(player: Player) {
        alivePlayers.remove(player.uniqueId)
    }
}