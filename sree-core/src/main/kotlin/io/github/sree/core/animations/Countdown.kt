package io.github.sree.core.animations

import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

class Countdown(
    val totalSeconds: Int,
    val textColor: NamedTextColor,
    val sound: Sound = Sound.INTENTIONALLY_EMPTY
) {

    suspend fun run(playerIds: Collection<UUID>) {
        val startTime = System.nanoTime()
        var lastSecond = -1

        try {
            while(true) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000_000.0
                val remaining = totalSeconds - elapsed
                val second = remaining.toInt()

                if (remaining <= 0) return
                if (second == lastSecond) continue

                lastSecond = second

                val title = Title.title(
                    Component.text(remaining.roundToInt(), textColor),
                    Component.empty()
                )

                playerIds.forEach { uuid ->
                    val player = Bukkit.getPlayer(uuid)
                    player?.showTitle(title)
                    player?.playSound(player.location, sound, 1.0f, 1.0f)
                }

                delay(50.milliseconds)
            }
        } finally {
            playerIds.forEach { uuid ->
                val player = Bukkit.getPlayer(uuid)
                player?.playSound(player.location, sound, 1.0f, 2.0f)
            }
        }
    }
}