plugins {
    kotlin("jvm") version "2.0.10"
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

group = "gg.pignet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.mongodb:mongodb-driver-sync:5.1.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC.2")
    implementation(kotlin("reflect"))
    implementation("io.github.revxrsal:lamp.common:4.0.0-beta.17")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-beta.17")
}


kotlin {
    jvmToolchain(21)
}