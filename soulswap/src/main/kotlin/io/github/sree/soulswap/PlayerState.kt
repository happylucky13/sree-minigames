package io.github.sree.soulswap

import io.github.sree.core.animations.Timer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor


internal class PlayerState(
    settings: GameSettings
) {
    var team = Team.SURVIVOR
    var kills = 0
    var livesLeft: Int = settings.livesLeft

    val purgatoryTimer = Timer(
        name = Component.text("Time remaining: ", NamedTextColor.RED),
        totalDuration = settings.purgatoryDuration,
        location = Timer.Location.ACTION_BAR
    )
}