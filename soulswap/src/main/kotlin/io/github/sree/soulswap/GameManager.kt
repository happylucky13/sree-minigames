package io.github.sree.soulswap

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.sree.core.SreeCorePlugin
import io.github.sree.core.animations.Countdown
import kotlinx.coroutines.future.await
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent

internal class GameManager(
    val gameState: GameState,
    val plugin: SoulSwapPlugin,
    val core: SreeCorePlugin
) {

    fun startGame(worldName: String) {
        val countdown = Countdown(
            3,
            NamedTextColor.GOLD,
            Sound.BLOCK_NOTE_BLOCK_PLING
        )

        val prepareDimensionSet = core.prepareDimensionSet()

        plugin.launch {
            val overworld = prepareDimensionSet
                .prepareDimensionSet(getWorldKey(worldName), plugin.logger)
                .await()

            gameState.addPlayers(Bukkit.getOnlinePlayers())
            countdown.run(gameState.alivePlayers)
            teleportPlayers(overworld)
            gameState.gameStarted = true
        }
    }

    fun handlePlayerDeath(event: PlayerDeathEvent) {
        val target = event.player
        val killer = core.combatTagManager().getKiller(event)

        killer?.handleKill()
        target.handleDeath()
    }

    fun Player.handleKill() {
        if (!gameState.alivePlayers.contains(this.uniqueId)) return

        val playerState = gameState.playerStates[this.uniqueId]
        playerState?.kills ++

        if (playerState?.team == Team.PURGATORY) {
            playerState.team = Team.SURVIVOR
            this.reviveAnimation()
        }
    }

    fun Player.handleDeath() {
        if (!gameState.alivePlayers.contains(this.uniqueId)) return

        val playerState = gameState.playerStates[this.uniqueId] ?: return
        if (playerState.team == Team.SURVIVOR) playerState.livesLeft --

        if (playerState.livesLeft == 0) {
            gameState.removePlayer(this)
            core.spectatorService().addSpectator(this)
            return
        }

        when (playerState.team) {
            Team.SURVIVOR -> {
                playerState.team = Team.PURGATORY
                this.startPurgatoryTimer()
            }

            Team.PURGATORY -> {

            }
        }
    }

    private fun teleportPlayers(overworld: World) {
        Bukkit.getOnlinePlayers().forEach { it.teleportAsync(overworld.spawnLocation) }
    }

    private fun getWorldKey(worldName: String): NamespacedKey {
        return NamespacedKey(plugin, worldName)
    }
}