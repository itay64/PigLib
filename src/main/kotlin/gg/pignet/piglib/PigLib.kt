package gg.pignet.piglib

import gg.pignet.piglib.config.ConfigManager
import gg.pignet.piglib.data.mongo.MongoDB
import gg.pignet.piglib.data.service.NameCacheService
import gg.pignet.piglib.gui.UIListener
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class PigLib(javaPlugin: JavaPlugin) {

    init {
        plugin = javaPlugin
        miniMessage = MiniMessage.miniMessage()
        ConfigManager.init()
        javaPlugin.server.pluginManager.registerEvents(UIListener(), javaPlugin)
    }

    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var miniMessage: MiniMessage
    }

    fun mongo() = MongoDB.init()
    fun nameCache() = NameCacheService.init()

}



fun pigLib(plugin: JavaPlugin, init: PigLib.() -> Unit): PigLib = PigLib(plugin).apply(init)