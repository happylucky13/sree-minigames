package io.github.sree.soulswap




internal class PlayerState(
    var livesLeft: Int,
) {
    var team = Team.SURVIVOR
    var kills = 0
}