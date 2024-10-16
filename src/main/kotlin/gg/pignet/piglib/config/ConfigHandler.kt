package gg.pignet.piglib.config

import com.google.gson.GsonBuilder
import gg.pignet.piglib.PigLib
import java.io.File

object ConfigHandler {
    lateinit var CONFIG: Config

    fun init() {
        CONFIG = loadConfig("config.json", Config())
    }

    inline fun <reified T : Any> loadConfig(fileName: String, default: T): T {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val file = File(PigLib.plugin.dataFolder, fileName)

        if (!file.exists() || file.readText().isBlank()) {
            file.writeText(gson.toJson(default))
            return default
        }

        val content = file.readText()

        // Parse the JSON from the file into the Config data class
        return gson.fromJson(content, T::class.java)
    }
}