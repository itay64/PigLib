package gg.pignet.gingerlib.data.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoClientSettings.getDefaultCodecRegistry
import com.mongodb.client.*
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import gg.pignet.gingerlib.data.mongo.MongoDB.executor
import gg.pignet.gingerlib.data.service.NameCacheService
import gg.pignet.gingerlib.config.ConfigHandler.CONFIG
import org.bson.Document
import org.bson.UuidRepresentation
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.conversions.Bson
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

object MongoDB {

    private lateinit var client: MongoClient
    internal lateinit var database: MongoDatabase
    internal val executor: Executor = Executors.newCachedThreadPool()

    fun init() {
        client = MongoClients.create(
            MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(fromProviders(getDefaultCodecRegistry()))
                .applyConnectionString(ConnectionString(CONFIG.mongo.uri ?: throw NullPointerException("Mongo URI not found.")))
                .build()
        )
        database = client.getDatabase(CONFIG.mongo.database ?: throw NullPointerException("Mongo database not found."))
        NameCacheService.init()
    }

    fun collection(name: String): MongoCollection<Document> = database.getCollection(name)

    @Deprecated("Use collection(clazz: KClass<out MongoSerializable>)", ReplaceWith("collection(`class`)"))
    fun <T : Any> collection(name: String, `class`: Class<T>): MongoCollection<T> =
        database.getCollection(name, `class`)


    val collections = mutableMapOf<KClass<out MongoSerializable>, CustomMongoCollection<out MongoSerializable>>()

    inline fun <reified T : MongoSerializable> collection(
        name: String = T::class.simpleName!!.pluralize().formatCase(Case.CAMEL)
    ): CustomMongoCollection<T> {
        @Suppress("unchecked_cast")
        return collections.getOrPut(T::class) { CustomMongoCollection(T::class, name) } as CustomMongoCollection<T>
    }

    fun collection(clazz: KClass<out MongoSerializable>): CustomMongoCollection<out MongoSerializable> {
        return collections.getOrPut(clazz) {
            CustomMongoCollection(
                clazz,
                clazz.simpleName!!.pluralize().formatCase(Case.CAMEL)
            )
        }
    }

}

class CustomMongoCollection<T : MongoSerializable>(
    private val clazz: KClass<out MongoSerializable>,
    val name: String
) {

    val idField = IdField(clazz)
    val documents: MongoCollection<Document> = MongoDB.database.getCollection(name, Document::class.java).apply {
        createIndex(Indexes.ascending("uuid"))
    }

    fun saveSync(serializable: MongoSerializable): UpdateResult = with(serializable.toDocument()) {
        documents.replaceOne(
            eq(idField.name, this[idField.name]),
            this,
            ReplaceOptions().upsert(true)
        )
    }

    fun save(serializable: MongoSerializable): CompletableFuture<UpdateResult> =
        CompletableFuture.supplyAsync({
            saveSync(serializable)
        }, executor)

    fun findSync(filter: Bson? = null): MongoIterable<T> =
        (if (filter == null) documents.find() else documents.find(filter)).map {
            @Suppress("unchecked_cast")
            GSON.fromJson(it.toJson(), clazz.java) as T
        }

    fun find(filter: Bson? = null): CompletableFuture<MongoIterable<T>> =
        CompletableFuture.supplyAsync({ findSync(filter) }, executor)

    fun findByIdSync(id: Any): MongoIterable<T> = findSync(eq(idField.name, id))

    fun findById(id: Any): CompletableFuture<MongoIterable<T>> =
        CompletableFuture.supplyAsync({ findByIdSync(id) }, executor)

    fun deleteSync(filter: Bson): DeleteResult = documents.deleteMany(filter)


    fun delete(filter: Bson): CompletableFuture<DeleteResult> =
        CompletableFuture.supplyAsync({ deleteSync(filter)  }, executor)

    fun deleteByIdSync(id: Any): DeleteResult = deleteSync(eq(idField.name, id))

    fun deleteById(id: Any): CompletableFuture<DeleteResult> =
        CompletableFuture.supplyAsync({ deleteByIdSync(id) }, executor)

}

interface MongoSerializable {
    fun saveSync(): UpdateResult = MongoDB.collection(this@MongoSerializable::class).saveSync(this@MongoSerializable)

    fun save(): CompletableFuture<UpdateResult> = MongoDB.collection(this::class).save(this)

    fun deleteSync(): DeleteResult = with(MongoDB.collection(this::class)) {
        deleteSync(eq(idField.name, idField.value(this@MongoSerializable)))
    }

    fun delete(): CompletableFuture<DeleteResult> = with(MongoDB.collection(this::class)) {
        delete(eq(idField.name, idField.value(this@MongoSerializable)))
    }

    fun toDocument(): Document = Document.parse(toJson())
}

@Target(AnnotationTarget.FIELD)
annotation class Id

data class IdField(val clazz: KClass<out MongoSerializable>) {

    private val idField: KProperty1<out MongoSerializable, *>
    val name: String
    val type: KType

    init {
        val idFields = clazz.declaredMemberProperties.filter { it.javaField?.isAnnotationPresent(Id::class.java) == true }
        println(idFields)

        require(idFields.size == 1) {
            when (idFields.size) {
                0 -> "Class does not have a field annotated with @Id"
                else -> "Class must not have more than one field annotated with @Id"
            }
        }

        idField = idFields.first()

        name = idField.name
        type = idField.returnType
    }

    @Suppress("unchecked_cast")
    fun value(instance: MongoSerializable): Any = (idField as KProperty1<Any, *>).get(instance)
        ?: throw IllegalStateException("Field annotated with @Id must not be null")

}

fun Any.toDocument(): Document = Document.parse(toJson())

infix fun <V> KProperty<V>.eq(other: Any): Bson = eq(this.name, other)


