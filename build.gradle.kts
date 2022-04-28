plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "fr.uiytt"
version = "0.2"

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://jitpack.io")
    maven(url = "https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.4")
    implementation("fr.minuskube.inv:smart-invs:1.2.7")

    implementation("com.sk89q.worldedit:worldedit-core:7.2.6")
    implementation("com.sk89q.worldedit:worldedit-bukkit:7.2.6")
}

tasks.shadowJar {
    dependencies {
        include(dependency("fr.minuskube.inv:smart-invs:1.2.7"))
        include(dependency("com.github.simplix-softworks:simplixstorage:3.2.4"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.6.21"))
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version) )
    }
}