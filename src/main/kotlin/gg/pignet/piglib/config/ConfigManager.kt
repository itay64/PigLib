package gg.pignet.piglib.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gg.pignet.piglib.PigLib
import java.io.File

object ConfigManager {

    lateinit var GSON: Gson
    val files: MutableMap<Any, File> = mutableMapOf()

    lateinit var CONFIG: Config

    fun init() {
        GSON = GsonBuilder().setPrettyPrinting().create()
        CONFIG = loadConfig("config.json", Config())
    }

    inline fun <reified T : Any> loadConfig(fileName: String, default: T): T {
        val file = File(PigLib.plugin.dataFolder, fileName)

        if (!file.exists() || file.readText().isBlank()) {
            file.writeText(GSON.toJson(default))
            return default
        }

        val content = file.readText()

        // Parse the JSON from the file into the Config data class
        val config = GSON.fromJson(content, T::class.java)
        files[config] = file
        return config
    }

    inline fun <reified T> saveConfig(config: T){
        val file: File = files.getOrPut(config!!){File(PigLib.plugin.dataFolder,"${T::class.simpleName}.json")}

        val content = GSON.toJson(config)

        file.writeText(content)
    }

}