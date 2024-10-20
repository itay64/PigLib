package gg.pignet.test

import gg.pignet.piglib.data.mongo.MongoDB
import gg.pignet.piglib.data.mongo.toUUID
import gg.pignet.piglib.data.service.NameCacheService

fun main(args: Array<String>) {
    MongoDB.init("no way lmao", "secret")
    NameCacheService.init()
    println("b876ec32-e396-476b-a115-8438d83c67d4's Name: ${NameCacheService.nameFromUUID("b876ec32-e396-476b-a115-8438d83c67d4".toUUID())}")
}