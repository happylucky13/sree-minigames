package io.github.sree.soulswap

import io.github.sree.core.animations.Timer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor


internal class PlayerState(
    var livesLeft: Int,
    settings: GameSettings
) {
    var team = Team.SURVIVOR
    var kills = 0

    val purgatoryTimer = Timer(
        name = Component.text("Time remaining: ", NamedTextColor.RED),
        totalSeconds = settings.purgatoryTimerLength,
        location = Timer.Location.ACTION_BAR
    )
}