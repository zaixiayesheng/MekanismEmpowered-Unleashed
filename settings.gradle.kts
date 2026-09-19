pluginManagement {
    repositories {
        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
        }
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// 内嵌兼容子模组：只注册旧 id "mekanism_empowered" 的微型 mod，
// 以 jar-in-jar 方式随主 jar 分发（修复 MekaJade Upgrades 图标问题）。
include("compat")

rootProject.name = file("buildSrc/src/main/kotlin/Constants.kt").readLines()
    .map(String::trim)
    .find { it.startsWith("const val NAME") }
    ?.substringAfter('"')
    ?.substringBefore('"')
    ?.replace(" ", "")
    ?.replace(":", "")
    ?: throw IllegalStateException("Failed to find mod NAME")
