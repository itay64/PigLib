package gg.pignet.piglib.data.service

import com.google.gson.JsonParser
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import gg.pignet.event.data.findKeyByValue
import gg.pignet.piglib.data.mongo.MongoDB
import gg.pignet.piglib.data.mongo.toUUID
import org.bson.codecs.pojo.annotations.BsonId
import java.net.URL
import java.util.*
import java.util.regex.Pattern
import javax.net.ssl.HttpsURLConnection

object NameCacheService {

    private const val MOJANG_PROFILE_ENDPOINT = "https://sessionserver.mojang.com/session/minecraft/profile"
    private const val MOJANG_UUID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft"

    private val cache = mutableMapOf<UUID, String>()

    private lateinit var mongoCache: MongoCollection<NameCache>

    data class NameCache(@BsonId val id: String, val name: String) : MongoDB.MongoDocument

    fun init() {
        mongoCache = MongoDB.collection<NameCache>()
    }

    fun nameFromUUID(uuid: UUID): String {
        return cache[uuid] ?: queryMongoNameByUUID(uuid) ?: queryMojangNameByUUID(uuid)
    }

    private fun queryMongoNameByUUID(uuid: UUID): String? {
        mongoCache.let {
            mongoCache.find(Filters.eq("_id", uuid.toString())).first()?.let {
                val name =
                    it.name ?: throw MongoException("Document with '_id' '$uuid' has no field 'name'.")
                cache[uuid] = name
                return name
            }
        }
        return null
    }

    private fun queryMojangNameByUUID(uuid: UUID): String {
        val connection = URL("$MOJANG_PROFILE_ENDPOINT/$uuid").openConnection() as HttpsURLConnection
        connection.doOutput = true
        val name = JsonParser.parseReader(connection.inputStream.reader()).asJsonObject.get("name").asString
        connection.disconnect()
        cache[uuid] = name
        mongoCache.insertOne(NameCache(uuid.toString(), name))
        return name
    }

    fun uuidFromName(name: String): UUID {
        return cache.findKeyByValue(name) ?: queryMongoUUIDByName(name) ?: queryMojangUUIDByName(name)
    }

    private fun queryMongoUUIDByName(name: String): UUID? {
        mongoCache.let {
            mongoCache.find(
                Filters.regex(
                    "name",
                    Pattern.compile("^$name$", Pattern.CASE_INSENSITIVE)
                )
            ).first()?.let {
                val uuid = it.id.toUUID()
                    ?: throw MongoException("Document with 'name' '$name' has no valid UUID at '_id'.")
                cache[uuid] = name
                return uuid
            }
        }
        return null
    }

    private fun queryMojangUUIDByName(name: String): UUID {
        val connection =
            URL("$MOJANG_UUID_ENDPOINT/$name").openConnection() as HttpsURLConnection
        connection.doOutput = true
        val uuid = JsonParser.parseReader(connection.inputStream.reader()).asJsonObject.get("id").asString.toUUID()
        connection.disconnect()
        cache[uuid] = name
        mongoCache.insertOne(NameCache(uuid.toString(), name))
        return uuid
    }



}