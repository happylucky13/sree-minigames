package io.github.sree.soulswap

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.sree.core.SreeCorePlugin
import io.github.sree.core.animations.Countdown
import io.github.sree.core.combat_tag.CombatTagSettings
import io.github.sree.core.combat_tag.TaggingMethod
import io.github.sree.core.information.InformationChannel
import io.github.sree.soulswap.state.GameState
import io.github.sree.soulswap.state.Team
import io.github.sree.soulswap.state.purgatorySpeed
import io.github.sree.soulswap.state.updateScoreboard
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
        core.combatTagManager().setCombatTagSettings(CombatTagSettings(15, TaggingMethod.LAST_HIT))
        core.informationService().reset(Bukkit.getOnlinePlayers())

        plugin.launch {
            val overworld = prepareDimensionSet
                .prepareDimensionSet(getWorldKey(worldName))
                .await()

            gameState.addPlayers(Bukkit.getOnlinePlayers())
            countdown.run(gameState.alivePlayers)
            teleportPlayers(overworld)
            gameState.gameStarted = true

            gameState.alivePlayers.forEach { uuid ->
                val player = Bukkit.getPlayer(uuid)
                player?.updateScoreboard(gameState)
                player?.purgatorySpeed(false)
            }
        }
    }

    fun handlePlayerDeath(event: PlayerDeathEvent) {
        val target = event.player
        val killer = core.combatTagManager().getKiller(event)

        killer?.handleKill()
        target.handleDeath()

        if (gameState.alivePlayers.isEmpty()) endGame()
    }

    fun Player.handleKill() {
        if (!gameState.alivePlayers.contains(this.uniqueId)) return

        val playerState = gameState.playerStates[this.uniqueId] ?: return
        playerState.kills ++

        if (playerState.team == Team.PURGATORY) {
            playerState.team = Team.SURVIVOR
            this.reviveAnimation()
            core.informationService().allow(this, InformationChannel.LOCATOR_BAR_TRANSMIT)
            this.purgatorySpeed(false)
        }

        this.updateScoreboard(gameState)
    }

    fun Player.handleDeath() {
        val playerId = this.uniqueId

        if (!gameState.alivePlayers.contains(playerId)) return
        val playerState = gameState.playerStates[playerId] ?: return

        when (playerState.team) {
            Team.SURVIVOR -> {
                playerState.livesLeft --
                if (playerState.livesLeft <= 0) {
                    this.finalDeathAnimation()
                    gameState.removePlayer(this)
                    core.spectatorService().addSpectator(this)
                    this.updateScoreboard(gameState)
                    return
                }

                playerState.team = Team.PURGATORY
                this.purgatorySpeed(true)
                core.informationService().deny(this, InformationChannel.LOCATOR_BAR_TRANSMIT)

                plugin.launch {
                    val completed = playerState.purgatoryTimer.run(setOf(playerId)) {
                        playerState.team == Team.SURVIVOR || !gameState.gameStarted
                    }

                    if (completed) {
                        gameState.removePlayer(this@handleDeath)
                        this@handleDeath.finalDeathAnimation()
                        core.spectatorService().addSpectator(this@handleDeath)
                    }
                }
            }

            Team.PURGATORY -> {
                this.playSound(this.location, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f)
                playerState.purgatoryTimer.reduce(gameState.settings.purgatoryDeathReduction)
            }
        }

        this.updateScoreboard(gameState)
    }

    private fun teleportPlayers(overworld: World) {
        Bukkit.getOnlinePlayers().forEach { it.teleportAsync(overworld.spawnLocation) }
    }

    fun getWorldKey(worldName: String): NamespacedKey {
        return NamespacedKey(plugin, worldName)
    }

    private fun endGame() {
        gameState.gameStarted = false
        core.spectatorService().removeAllSpectators()
        core.informationService().reset(Bukkit.getOnlinePlayers())
        plugin.clearScoreboard()
        Bukkit.getOnlinePlayers().forEach { it.purgatorySpeed(false) }
    }
}