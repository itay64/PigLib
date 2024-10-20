package gg.pignet.piglib.data

import java.util.UUID

data class Profile(
    val uuid: UUID,
    var name: String,
    var discordId: Long = 0
)

data class OverallStats(
    val uuid: UUID,
    var name: String,
    var totalPoints: Int = 0,
    var eventsPlayed: Int = 0,
    var eventsWon: Int = 0,
    var totalKills: Int = 0,
    var totalDeaths: Int = 0,
    var cookiesEaten: Int = 0
)

