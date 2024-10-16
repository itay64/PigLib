plugins {
    kotlin("jvm") version "2.0.10"
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.3"
    id("java")
}

group = "gg.pignet"
version = "1.0.6"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "PigLib"

            version = project.version.toString()
            artifact(tasks.shadowJar){
                classifier = null
            }
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}



tasks {
    compileJava {
    }
    jar {
        archiveBaseName.set("PigLib")
        archiveVersion.set(project.version.toString())
    }

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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}