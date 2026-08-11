@file:Suppress("VulnerableLibrariesLocal", "UnstableApiUsage")

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
    alias(libs.plugins.shadow.jar)
}

group = "io.github.balugaq"
version = "2.1.45"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.tcoded.com/releases")
    maven("https://mvn.wesjd.net/")
    maven("https://maven.norain.city/snapshots")
    maven("https://repo.alessiodp.com/releases")
    maven("https://repo.jeff-media.com/public")
    exclusiveContent {
        forRepository {
            maven("https://repo.extendedclip.com/releases")
        }
        filter {
            includeGroup("me.clip")
        }
    }
}

dependencies {
    // Paper & Slimefun 编译仅依赖
    compileOnly(libs.paper.api)
    compileOnly(libs.slimefun4)

    // 需内嵌打包的依赖
    implementation(libs.bstats.bukkit)
    implementation(libs.more.persistent.data.types)
    implementation(libs.anvilgui)
    implementation(libs.folia.lib)
    implementation(libs.libby.bukkit)
    implementation(libs.jetbrains.annotations)
    implementation(libs.jspecify)

    compileOnly(libs.findbugs.annotations)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // LibraryManager动态加载依赖
    compileOnly(libs.houbb.pinyin)
    compileOnly(libs.houbb.opencc4j)
    compileOnly(libs.houbb.heaven)
    compileOnly(libs.houbb.nlp.common)

    compileOnly(libs.guizhan.lib)
    compileOnly(libs.slimefun.translation)
    compileOnly(libs.placeholderapi)

    compileOnly(libs.networks.expansion)
    compileOnly(libs.slime.ae)
    compileOnly(libs.cmi.lib)
    compileOnly(libs.gugu.slimefun.lib)
    compileOnly(libs.emc.tech)
    compileOnly(libs.slime.hud)
    compileOnly(libs.slimefun.recipe)
    compileOnly(libs.ryken.slime.customizer)

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

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

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