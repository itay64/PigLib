package gg.pignet.piglib.config

data class Config(
    val mongo: Mongo = Mongo()
) {
    data class Mongo(
        val uri: String = "uri",
        val database: String = "database",
    )


}