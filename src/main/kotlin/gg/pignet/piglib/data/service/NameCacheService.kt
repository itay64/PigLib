package gg.pignet.piglib.data.service

import com.google.gson.JsonParser
import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import gg.pignet.lib.data.mongo.toUUID
import gg.pignet.event.data.findKeyByValue
import gg.pignet.piglib.data.mongo.MongoDB
import org.bson.Document
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.regex.Pattern

object NameCacheService {

    private const val MOJANG_PROFILE_ENDPOINT = "https://sessionserver.mojang.com/session/minecraft/profile"
    private const val MOJANG_UUID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft"

    private val cache = mutableMapOf<UUID, String>()

    private lateinit var mongoCache: MongoCollection<Document>

    fun init() {
        mongoCache = MongoDB.collection("nameCache")
    }

    fun nameFromUUID(uuid: UUID): String {
        return cache[uuid] ?: queryMongoNameByUUID(uuid) ?: queryMojangNameByUUID(uuid)
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
        val client = HttpClient.newHttpClient()

        // Create the HTTP request
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$MOJANG_PROFILE_ENDPOINT/$uuid"))
            .GET()
            .build()

        // Send the request and get the response
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        // Parse the JSON response
        val name = JsonParser.parseString(response.body())
            .asJsonObject.get("name").asString

        // Cache the name in memory
        cache[uuid] = name

        // Insert the name into the MongoDB cache
        mongoCache.insertOne(
            Document(
                mapOf(
                    "_id" to uuid.toString(),
                    "name" to name
                )
            )
        )

        return name
    }

    fun uuidFromName(name: String): UUID {
        return cache.findKeyByValue(name) ?: queryMongoUUIDByName(name)  ?: queryMojangUUIDByName(name)
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
        val client = HttpClient.newHttpClient()

        // Create the HTTP request
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$MOJANG_UUID_ENDPOINT/$name"))
            .GET()
            .build()

        // Send the request and get the response
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        // Parse the JSON response and convert the UUID
        val uuid = JsonParser.parseString(response.body())
            .asJsonObject.get("id").asString.toUUID()

        // Cache the UUID and name in memory
        cache[uuid] = name

        // Insert the UUID and name into the MongoDB cache
        mongoCache.insertOne(
            Document(
                mapOf(
                    "_id" to uuid.toString(),
                    "name" to name
                )
            )
        )

        return uuid
    }



}