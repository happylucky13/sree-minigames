package io.github.sree.core.animations

import kotlinx.coroutines.delay
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import kotlin.time.Duration
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class Timer(
    val name: Component,
    val totalDuration: Duration,
    val location: Location,
    bossBarColor: BossBar.Color = BossBar.Color.GREEN
) {
    enum class Location {
        BOSS_BAR,
        ACTION_BAR
    }

    private var endTime: TimeMark? = null

    private fun formatSeconds(totalDuration: Duration): CharSequence {
        val buffer = StringBuilder(8)

        return totalDuration.toComponents { hours, minutes, seconds, _ ->
            if (hours > 0) {
                buffer.append(hours).append(':')
                if (minutes < 10) buffer.append('0')
            }

            buffer.append(minutes).append(':')

            if (seconds < 10) buffer.append('0')
            buffer.append(seconds)

            buffer
        }
    }

    private val timerBar: BossBar = BossBar.bossBar(
        name.append { Component.text(formatSeconds(totalDuration).toString()) },
        1.0f,
        bossBarColor,
        BossBar.Overlay.PROGRESS
    )

    suspend fun run(
        playerIds: Collection<UUID>,
        shouldStop: () -> Boolean = { false }
    ): Boolean {
        endTime = TimeSource.Monotonic.markNow() + totalDuration

        try {
            while(true) {
                val remaining = -endTime!!.elapsedNow()

                if (remaining <= Duration.ZERO) {
                    displayTimer(playerIds, remaining)
                    return true
                }

                if (shouldStop()) return false

                displayTimer(playerIds, remaining)

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

    fun reduce(duration: Duration) {
        endTime?.let {
            endTime = it - duration
        }
    }

    private fun displayTimer(playerIds: Collection<UUID>, remaining: Duration) {
        when(location) {
            Location.BOSS_BAR -> {
                val progress: Float = (remaining / totalDuration).toFloat()
                timerBar.progress(progress)
                timerBar.name { name.append { Component.text(formatSeconds(remaining).toString()) } }
                playerIds.forEach { uuid ->
                    Bukkit.getPlayer(uuid)?.showBossBar(timerBar)
                }
            }

            Location.ACTION_BAR -> {
                val message = name.append { Component.text(formatSeconds(remaining).toString()) }
                playerIds.forEach { uuid ->
                    Bukkit.getPlayer(uuid)?.sendActionBar { message }
                }
            }
        }
    }
}