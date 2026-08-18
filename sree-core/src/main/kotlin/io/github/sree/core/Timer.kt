package io.github.sree.core

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.players
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.time.Duration
import org.bukkit.entity.Player

class Timer(
    val name: String,
    val totalSeconds: Long,
    val location: Location,
    val color: NamedTextColor = NamedTextColor.WHITE
) {
    enum class Location {
        BOSS_BAR,
        ACTION_BAR
    }

    private fun formatSeconds(totalSeconds: Long): CharSequence {
        val duration = Duration.ofSeconds(totalSeconds)
        val buffer = StringBuilder(8)

        val hours = duration.toHours()
        val minutes = duration.toMinutes()
        val seconds = duration.toSecondsPart()

        buffer.setLength(0)

        if (hours > 0) {
            buffer.append(hours)
            buffer.append(':')
        }


        if (minutes < 10) buffer.append('0')
        buffer.append(minutes)

        buffer.append(':')

        if (totalSeconds < 10) buffer.append('0')
        buffer.append(seconds)

        return buffer
    }

    private val timerBar: BossBar = BossBar.bossBar(
        Component.text(name + formatSeconds(totalSeconds), color),
        1.0f,
        BossBar.Color.GREEN,
        BossBar.Overlay.PROGRESS
    )

    suspend fun run(
        players: Collection<Player>,
        shouldStop: () -> Boolean = { false }
    ): Boolean {
        val startTime = System.nanoTime()

        try {
            while(true) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000_000.0
                val remaining = totalSeconds - elapsed

                if (remaining <= 0) {
                    displayTimer(players, 0)
                    return true
                }

                if (shouldStop()) return false

                displayTimer(players, remaining.toLong())

                delay(50)
            }
        } finally {
            players.forEach { it.hideBossBar(timerBar) }
            players.forEach { it.sendActionBar { Component.empty() } }
        }
    }

    private fun displayTimer(players: Collection<Player>, secondsRemaining: Long) {
        when(location) {
            Location.BOSS_BAR -> {
                timerBar.progress(secondsRemaining.toFloat() / totalSeconds)
                timerBar.name { Component.text(name + formatSeconds(secondsRemaining), color) }
                players.forEach { it.showBossBar(timerBar) }
            }

            Location.ACTION_BAR -> {
                val message = Component.text(name + formatSeconds(secondsRemaining))
                players.forEach { it.sendActionBar { message } }
            }
        }
    }
}