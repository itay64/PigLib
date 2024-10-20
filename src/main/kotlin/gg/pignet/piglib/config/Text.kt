package gg.pignet.piglib.config

import gg.pignet.piglib.PigLib.Companion.miniMessage
import net.kyori.adventure.text.Component

class Text(private var miniMessageText: String){
    var text: Component
        get() = miniMessage.deserialize(miniMessageText)
        set(value){ miniMessageText = miniMessage.serialize(value) }
}

fun text(text: Component) = Text(miniMessage.serialize(text))