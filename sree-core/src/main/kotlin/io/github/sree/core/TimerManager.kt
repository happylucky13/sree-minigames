package io.github.sree.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player

class TimerManager(private val scope: CoroutineScope) {
    fun runAsync(
        timer: Timer,
        players: Collection<Player>,
        shouldStop: () -> Boolean = { false },
        onComplete: (Boolean) -> Unit
    ) {
        scope.launch {
            val complete = timer.run(players, shouldStop)
            onComplete(complete)
        }
    }
}