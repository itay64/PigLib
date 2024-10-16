package gg.pignet.piglib.data.service

import com.google.gson.JsonParser
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import gg.pignet.piglib.data.mongo.MongoDB
import gg.pignet.piglib.data.mongo.toUUID
import gg.pignet.piglib.extension.findKeyByValue
import org.bson.Document
import java.net.URL
import java.util.*
import java.util.regex.Pattern
import javax.net.ssl.HttpsURLConnection

object NameCacheService {

    private const val MOJANG_PROFILE_ENDPOINT = "https://sessionserver.mojang.com/session/minecraft/profile"
    private const val MOJANG_UUID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft"

    private val cache = mutableMapOf<UUID, String>()

    private var useMongoCache = true
    private lateinit var mongoCache: MongoCollection<Document>

    fun init() {
        mongoCache = MongoDB.collection("nameCache")
    }

    fun nameFromUUID(uuid: UUID): String {
        return cache[uuid] ?: useMongoCache.takeIf { it }?.let { queryMongoNameByUUID(uuid)  } ?: queryMojangNameByUUID(uuid)
    }

    private fun queryMongoNameByUUID(uuid: UUID): String? {
        mongoCache.let {
            mongoCache.find(Filters.eq("_id", uuid.toString())).first()
                .let {
                    val name =
                        it.getString("name") ?: throw MongoException("Document with '_id' '$uuid' has no field 'name'.")
                    cache[uuid] = name
                    return name
                }
        }
    }

    private fun queryMojangNameByUUID(uuid: UUID): String {
        val connection = URL("$MOJANG_PROFILE_ENDPOINT/$uuid").openConnection() as HttpsURLConnection
        connection.doOutput = true
        val name = JsonParser.parseReader(connection.inputStream.reader()).asJsonObject.get("name").asString
        connection.disconnect()
        cache[uuid] = name
        if (useMongoCache) {
            mongoCache.insertOne(
                Document(
                    mapOf(
                        "_id" to uuid.toString(),
                        "name" to name
                    )
                )
            )
        }
        return name
    }

    fun uuidFromName(name: String): UUID {
        return cache.findKeyByValue(name) ?: useMongoCache.takeIf { it }?.let {  queryMongoUUIDByName(name)  } ?: queryMojangUUIDByName(name)
    }

    private fun queryMongoUUIDByName(name: String): UUID? {
        mongoCache.let {
            mongoCache.find(
                Filters.regex(
                    "name",
                    Pattern.compile("^$name$", Pattern.CASE_INSENSITIVE)
                )
            ).first().let {
                val uuid = UUID.fromString(it.getString("_id"))
                    ?: throw MongoException("Document with 'name' '$name' has no valid UUID at '_id'.")
                cache[uuid] = name
                return uuid
            }
        }
    }

    private fun queryMojangUUIDByName(name: String): UUID {
        val connection =
            URL("$MOJANG_UUID_ENDPOINT/$name").openConnection() as HttpsURLConnection
        connection.doOutput = true
        val uuid = JsonParser.parseReader(connection.inputStream.reader()).asJsonObject.get("id").asString.toUUID()
        connection.disconnect()
        cache[uuid] = name
        if (useMongoCache) {
            mongoCache.insertOne(
                Document(
                    mapOf(
                        "_id" to uuid.toString(),
                        "name" to name
                    )
                )
            )
        }
        return uuid
    }



}