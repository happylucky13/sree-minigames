package io.github.sree.core.animations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.CompletableFuture

class TimerManager(private val scope: CoroutineScope) {
    fun runAsync(
        timer: Timer,
        playerIds: Collection<UUID>,
        shouldStop: () -> Boolean = { false }
    ): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        scope.launch {
            try {
                future.complete(timer.run(playerIds, shouldStop))
            } catch (e: Throwable) {
                future.completeExceptionally(e)
            }
        }

        return future
    }
}