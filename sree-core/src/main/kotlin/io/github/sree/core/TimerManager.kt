package io.github.sree.core

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.players
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class TimerManager(private val scope: CoroutineScope) {
    fun runAsync(
        timer: Timer,
        players: Collection<Player>,
        shouldStop: () -> Boolean = { false }
    ): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        scope.launch {
            try {
                future.complete(timer.run(players, shouldStop))
            } catch (e: Throwable) {
                future.completeExceptionally(e)
            }
        }

        return future
    }
}