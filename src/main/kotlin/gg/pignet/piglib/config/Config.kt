package gg.pignet.piglib.config

import net.kyori.adventure.text.Component

data class Config(
    val mongo: Mongo = Mongo()
) {
    data class Mongo(
        val uri: String = "uri",
        val database: String = "database",
    )
    data class Messages(
        val welcomeMessage: Text = text(Component.text("Hello!"))
    )
}