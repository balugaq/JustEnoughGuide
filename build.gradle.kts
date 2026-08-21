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
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("maven-publish")
    id("signing")
    id("io.github.sgtsilvio.gradle.maven-central-publishing") version "0.5.0"
}

group = "io.github.balugaq"
version = "2.1.51"

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
    compileOnly(libs.logi.tech)

    // System-scoped local JARs
    compileOnly(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<Javadoc>().configureEach {
    // 出错（含 doclint 之外的警告）也不让 javadoc 任务失败，避免阻断构建/发布
    isFailOnError = false
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
        addStringOption("Xdoclint", "none")
    }
}

// 给所有 JavaExec 类任务（test / runServer / 以及其他 fork JVM 的任务）统一设置 UTF-8 编码，
// 避免因本地系统默认编码（如 GBK）导致乱码。
tasks.withType<JavaExec>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    systemProperty("sun.stdout.encoding", "UTF-8")
    systemProperty("sun.stderr.encoding", "UTF-8")
}

tasks {
    compileJava {
        options.compilerArgs.add("-Xlint:-removal")
        options.encoding = "UTF-8"
        options.release = 21
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

    runServer {
        dependsOn(shadowJar)
        val run = file(providers.gradleProperty("server.run.dir").orElse("run"))
        runDirectory.set(run)

        doFirst {
            run.resolve("eula.txt").writeText("eula=true")

            val pl = run.resolve("plugins")
            pl.mkdirs()
            copy {
                from(projectDir.resolve("build/libs")) {
                    include("${name}-${version}.jar")
                }
                into(pl)
            }
        }

        jvmArgs(
            "-Dfile.encoding=UTF-8",
            "-Dsun.jnu.encoding=UTF-8",
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001",
            "-Dnet.kyori.adventure.text.warn_when_legacy_formatting_detected=false"
        )
        maxHeapSize = "4G"
        minecraftVersion("1.21.11")
    }
}
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.named<Javadoc>("javadoc"))
}

publishing {
    repositories {
        maven {
            name = "Central"
            url = uri("https://central.sonatype.com/api/v1/publisher")
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.named("shadowJar"))
            // Maven Central 发布硬性要求：附带 sources / javadoc 构件
            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name = "JustEnoughGuide"
                description = "A Slimefun addon for Minecraft that significantly enhances the functionality and user experience of the original Slimefun guide book."
                url = "https://github.com/balugaq/JustEnoughGuide"
                licenses {
                    license {
                        name = "GNU General Public License v3.0 or later"
                        url  = "https://www.gnu.org/licenses/gpl-3.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "balugaq"
                        name = "balugaq"
                        email = "balugaq@qq.com"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/balugaq/JustEnoughGuide.git"
                    developerConnection = "scm:git:ssh://github.com/balugaq/JustEnoughGuide.git"
                    url = "https://github.com/balugaq/JustEnoughGuide"
                }
            }
        }
    }
}

// 签名配置
signing {
    // 从环境变量或 gradle.properties 读取敏感信息；
    // 仅在提供了签名密钥时才启用签名，避免本地 build/无密钥时配置失败
    val signingKey = providers.gradleProperty("signingKey")
        .orElse(providers.systemProperty("signingKey"))
        .orElse(providers.environmentVariable("SIGNING_KEY"))
        .orNull

    val signingPassword = providers.gradleProperty("signingPassword")
        .orElse(providers.systemProperty("signingPassword"))
        .orElse(providers.environmentVariable("SIGNING_PASSWORD"))
        .orNull
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    } else {
        // 未提供签名密钥（例如本地开发构建），跳过签名
    }
}