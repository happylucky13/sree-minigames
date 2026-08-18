package io.github.sree.core.animations

import kotlinx.coroutines.delay
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.time.Duration
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class Timer(
    val name: Component,
    val totalSeconds: Long,
    val location: Location,
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

        if (seconds < 10) buffer.append('0')
        buffer.append(seconds)

        return buffer
    }

    private val timerBar: BossBar = BossBar.bossBar(
        name.append { Component.text(formatSeconds(totalSeconds).toString()) },
        1.0f,
        BossBar.Color.GREEN,
        BossBar.Overlay.PROGRESS
    )

    suspend fun run(
        playerIds: Collection<UUID>,
        shouldStop: () -> Boolean = { false }
    ): Boolean {
        val startTime = System.nanoTime()

        try {
            while(true) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000_000.0
                val remaining = totalSeconds - elapsed

                if (remaining <= 0) {
                    displayTimer(playerIds, 0)
                    return true
                }

                if (shouldStop()) return false

                displayTimer(playerIds, remaining.toLong())

                delay(50.milliseconds)
            }
        } finally {
            playerIds.forEach { uuid ->
                val player = Bukkit.getPlayer(uuid)
                player?.hideBossBar(timerBar)
                player?.sendActionBar { Component.empty() }
            }
        }
    }

    private fun displayTimer(playerIds: Collection<UUID>, secondsRemaining: Long) {
        when(location) {
            Location.BOSS_BAR -> {
                timerBar.progress(secondsRemaining.toFloat() / totalSeconds)
                timerBar.name { name.append { Component.text(formatSeconds(secondsRemaining).toString()) } }
                playerIds.forEach { uuid ->
                    Bukkit.getPlayer(uuid)?.showBossBar(timerBar)
                }
            }

            Location.ACTION_BAR -> {
                val message = name.append { Component.text(formatSeconds(secondsRemaining).toString()) }
                playerIds.forEach { uuid ->
                    Bukkit.getPlayer(uuid)?.sendActionBar { message }
                }
            }
        }
    }
}