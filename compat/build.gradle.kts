/**
 * 内嵌兼容子模组（jar-in-jar 被主 jar 携带）。
 *
 * 唯一的用途：向 FML 注册旧 mod id "mekanism_empowered"，让 MekaJade Upgrades
 * 这类写死检查旧 id 的附属继续认出强化升级并显示正确图标。实际功能全部由
 * 主模组 mekanism_empowered_unleashed 提供，本子模组不加载任何内容。
 */
plugins {
    id("java")
}

base {
    archivesName = "mekanism-empowered-compat"
    version = "1.0.0"
    group = "dev.zaixiayesheng"
}

repositories {
    mavenCentral()
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    // 只需要 @Mod 注解即可，不引入任何运行时依赖。
    // FML 的类在 fancymodloader:loader 里（neoforge 本体 jar 不含 fml/common 包）；
    // 关闭传递依赖，loader 的传递项（Mojang 库等）编译用不上。
    compileOnly("net.neoforged.fancymodloader:loader:4.0.43") {
        isTransitive = false
    }
}

tasks.withType<JavaCompile>().configureEach {
    // 与游戏运行时的 JVM 21 保持一致
    options.release.set(21)
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "dev.zaixiayesheng.mekanism_empowered_compat")
    }
}
