package io.github.sree.soulswap

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class GameSettings(
    val livesLeft: Int = 2,
    val purgatoryDuration: Duration = 30.minutes
)
