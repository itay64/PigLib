package gg.pignet.piglib.data.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import gg.pignet.piglib.config.ConfigManager.CONFIG
import gg.pignet.piglib.scheduler.async
import gg.pignet.piglib.scheduler.sync
import org.bson.UuidRepresentation
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.KMongo
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.replaceOneById
import kotlin.reflect.KClass


object MongoDB {
    private lateinit var client: MongoClient
    lateinit var database: MongoDatabase

    fun init(uri: String = CONFIG.mongo.uri, db: String = CONFIG.mongo.database) {
        client = KMongo.createClient(
            MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(ConnectionString(uri))
                .build())
        database = client.getDatabase(db)
    }

    interface MongoDocument {
        private val id: Any get() = this@MongoDocument::class.java.declaredFields.first { it.isAnnotationPresent(BsonId::class.java)}.get(this@MongoDocument)
        fun saveSync(){ collection(this@MongoDocument::class).replaceOneById(id, this@MongoDocument) }
        fun saveAsync(){ async { collection(this@MongoDocument::class).replaceOneById(id, this@MongoDocument) } }
        fun deleteSync(){ sync { collection(this@MongoDocument::class).deleteOneById(id) } }
        fun deleteASync(){ async { collection(this@MongoDocument::class).deleteOneById(id) } }
    }

    inline fun <reified T : MongoDocument> collection(
        name: String = T::class.simpleName!!.pluralize().formatCase(Case.CAMEL)
    ): MongoCollection<T> {
        return database.getCollection(name, T::class.java)
    }

    private fun collection(clazz: KClass<out MongoDocument>): MongoCollection<MongoDocument> = collection()

    data class PlayerData(@BsonId val uuid: String, val name: String): MongoDocument



}