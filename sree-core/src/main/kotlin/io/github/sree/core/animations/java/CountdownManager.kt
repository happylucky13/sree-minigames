package io.github.sree.core.animations.java

import io.github.sree.core.animations.Countdown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.CompletableFuture

class CountdownManager(private val scope: CoroutineScope) {
    fun runAsync(
        countdown: Countdown,
        playerIds: Collection<UUID>
    ): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        scope.launch {
            try {
                future.complete(countdown.run(playerIds))
            } catch (e: Throwable) {
                future.completeExceptionally(e)
            }
        }

        return future
    }
}