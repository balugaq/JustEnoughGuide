/*
 * Copyright (c) 2024-2026 balugaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

plugins {
    java
    id("com.gradleup.shadow") version "9.0.0"
    id("maven-publish")
}

group = "io.github.balugaq"
version = "2.1.33"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.tcoded.com/releases")
    maven("https://repo.jeff-media.com/public")
    maven("https://mvn.wesjd.net/")
    maven("https://maven.norain.city/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
    maven("https://repo.alessiodp.com/releases/")
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.SlimefunGuguProject:Slimefun4:2025.1.2")

    // Libraries to be packaged (shadow)
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    implementation("com.github.balugaq:AnvilGUI:ca2ef9e187")
    implementation("com.tcoded:FoliaLib:0.5.1")
    implementation("net.byteflux:libby-bukkit:1.3.1")
    implementation("org.jetbrains:annotations:26.1.0")
    implementation("org.jspecify:jspecify:1.0.0")

    // Provided dependencies (not packaged)
    compileOnly("com.google.code.findbugs:annotations:3.0.1u2")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    compileOnly("com.github.houbb:pinyin:0.4.0")
    compileOnly("com.github.houbb:opencc4j:1.14.0")
    compileOnly("com.github.houbb:heaven:0.13.0")
    compileOnly("com.github.houbb:nlp-common:0.0.5")
    compileOnly("net.guizhanss:GuizhanLibPlugin:2.5.0")
    compileOnly("net.guizhanss:SlimefunTranslation:e6da231617")
    compileOnly("me.clip:placeholderapi:2.11.6")

    // Provided for optional integrations
    compileOnly("com.github.ytdd9527:NetworksExpansion:0cfc607e89")
    compileOnly("com.github.balugaq:SlimeAE:40ff388e88")
    compileOnly("com.github.Zrips:CMILib:1.4.7.4")
    compileOnly("com.github.TimetownDev:GuguSlimefunLib:3f1830a50b")
    compileOnly("com.github.balugaq:EMCTech:d6e4b43d23")
    compileOnly("com.github.balugaq:SlimeHUD:ad7a52fead")
    compileOnly("com.github.balugaq:SlimeFunRecipe:ef222864d0")

    // System-scoped local JARs
    compileOnly(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.compilerArgs.add("-Xlint:-removal")
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveBaseName.set("JustEnoughGuide")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")

        // Relocations
        relocate("net.Zrips.CMILib", "com.balugaq.jeg.libraries.cmilib")
        relocate("com.tcoded.folialib", "com.balugaq.jeg.libraries.folialib")
        relocate("net.byteflux.libby", "com.balugaq.jeg.libraries.libby")
        relocate("com.jeff_media", "com.balugaq.jeg.libraries.jeff_media")
        relocate("org.bstats", "com.balugaq.jeg.libraries.bstats")
        relocate("net.wesjd.anvilgui", "com.balugaq.jeg.libraries.anvilgui")

        // Exclude unwanted files
        exclude("META-INF/*")
        exclude("META-INF/maven/**")
        exclude("META-INF/versions/**")

        mergeServiceFiles()
    }

    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filesMatching("**/*.properties") {
            expand(project.properties)
        }
    }

    build {
        dependsOn(shadowJar)
    }
}

// Publishing configuration for Maven Central
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.shadowJar)
        }
    }
    repositories {
        maven {
            name = "central"
            url = uri("https://central.sonatype.com/api/v1/publisher/")
            credentials {
                username = project.findProperty("centralUsername") as String? ?: ""
                password = project.findProperty("centralPassword") as String? ?: ""
            }
        }
    }
}