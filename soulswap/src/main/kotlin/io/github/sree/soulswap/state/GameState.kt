package io.github.sree.soulswap.state

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import io.github.sree.soulswap.SoulSwapPlugin
import io.papermc.paper.scoreboard.numbers.NumberFormat
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class GameState {
    val alivePlayers: Set<UUID>
        field = mutableSetOf<UUID>()

    val playerStates: Map<UUID, PlayerState>
        field = mutableMapOf<UUID, PlayerState>()

    val settings = GameSettings()
    var gameStarted = false

    fun addPlayers(players: Collection<Player>) {
        alivePlayers.addAll(players.map { it.uniqueId } )
        playerStates.putAll(players.associate { it.uniqueId to PlayerState(settings) } )
    }

    fun removePlayer(player: Player) {
        alivePlayers.remove(player.uniqueId)
    }
}

internal fun Player.updateScoreboard(gameState: GameState) {
    val playerState = gameState.playerStates[this.uniqueId] ?: return
    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard

    val survivorTeam = scoreboard.getTeam("Survivors") ?: scoreboard.registerNewTeam("Survivors").apply {
        color(NamedTextColor.GREEN)
        setAllowFriendlyFire(false)
    }

    val purgatoryTeam = scoreboard.getTeam("Purgatory") ?: scoreboard.registerNewTeam("Purgatory").apply {
        color(NamedTextColor.RED)
        setAllowFriendlyFire(false)
    }

    val livesLeft: Objective = scoreboard.getObjective("lives_left") ?:
        scoreboard.registerNewObjective(
            "lives_left",
            Criteria.DUMMY,
            Component.text("Lives Left")
        ).apply {
            displaySlot = DisplaySlot.BELOW_NAME
        }

    livesLeft.displayName(Component.text("Lives Left", NamedTextColor.GOLD))

    survivorTeam.removeEntry(this.name)
    purgatoryTeam.removeEntry(this.name)

    when (playerState.team) {
        Team.SURVIVOR -> survivorTeam.addEntry(this.name)
        Team.PURGATORY -> purgatoryTeam.addEntry(this.name)
    }

    livesLeft.getScore(this.name).apply {
        score = playerState.livesLeft
        numberFormat(NumberFormat.styled(Style.style(NamedTextColor.GOLD, TextDecoration.BOLD)))
    }
}

internal fun Player.purgatorySpeed(toggle: Boolean) {
    val attribute = this.getAttribute(Attribute.MOVEMENT_SPEED) ?: return

    attribute.removeModifier(PURGATORY_SPEED_MODIFIER)

    if (toggle) {
        attribute.addModifier(PURGATORY_SPEED_MODIFIER)
        return
    }
}

internal val PURGATORY_SPEED_MODIFIER = AttributeModifier(
    NamespacedKey("soulswap", "purgatory_speed"),
    0.2,
    AttributeModifier.Operation.ADD_SCALAR
)