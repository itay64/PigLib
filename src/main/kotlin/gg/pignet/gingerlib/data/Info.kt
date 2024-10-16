package gg.pignet.event.data

import gg.pignet.gingerlib.data.mongo.Id
import gg.pignet.gingerlib.data.mongo.MongoSerializable
import java.util.UUID

data class Profile(
    @Id val uuid: UUID,
    var name: String,
    var discordId: Long = 0
) : MongoSerializable

data class OverallStats(
    @Id val uuid: UUID,
    var name: String,
    var totalPoints: Int = 0,
    var eventsPlayed: Int = 0,
    var eventsWon: Int = 0,
    var totalKills: Int = 0,
    var totalDeaths: Int = 0,
    var cookiesEaten: Int = 0
) : MongoSerializable

