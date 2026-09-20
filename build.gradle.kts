import net.neoforged.moddevgradle.internal.RunGameTask
import org.apache.tools.ant.filters.ReplaceTokens
import org.slf4j.event.Level
import java.text.SimpleDateFormat
import java.util.*


plugins {
    id("java")
    id("java-library")
    id("idea")

    id("localRuntime")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moddev)
}

val modId = Constants.Mod.ID
val mcVersion: String = libs.versions.minecraft.get()
val kffVersion: String = libs.versions.kotlinForForge.get()

val jdkVersion = Constants.Dev.JDK_VERSION
val jvmVendor = Constants.Dev.JVM_VENDOR


val exportMixin = true
val loadAddons = true


base {
    archivesName = "${rootProject.name}-$mcVersion"
    version = Constants.Mod.VERSION
    group = Constants.Mod.GROUP
}

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "Mekanism / JEI"
        url = uri("https://modmaven.dev/")
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
    }
    maven {
        name = "R2"
        url = uri("https://maven.lapis256.dev")
    }
    mavenCentral()
}

val generateModMetadata by tasks.registering(ProcessResources::class)

val coreApiSourceSet: SourceSet = sourceSets.create("core.api", Action {})

val mainApiSourceSet: SourceSet = sourceSets.create("main.api", Action {
    compileClasspath += coreApiSourceSet.output
    runtimeClasspath += coreApiSourceSet.output
})

val coreSourceSet: SourceSet = sourceSets.create("core", Action {
    compileClasspath += coreApiSourceSet.output
    runtimeClasspath += coreApiSourceSet.output

    resources {
        exclude("**/.cache")
    }
})

val mainSourceSet: SourceSet = sourceSets.getByName("main") {
    compileClasspath += mainApiSourceSet.output + coreApiSourceSet.output + coreSourceSet.output
    runtimeClasspath += mainApiSourceSet.output + coreApiSourceSet.output + coreSourceSet.output

    resources {
        srcDirs(
            "src/generated/resources",
            generateModMetadata.get().outputs.files
        )
        exclude("**/.cache")
    }
}

val dataSourceSet: SourceSet = sourceSets.create("data", Action {
    compileClasspath += coreSourceSet.output + mainSourceSet.compileClasspath + mainSourceSet.output
    runtimeClasspath += coreSourceSet.output + mainSourceSet.runtimeClasspath + mainSourceSet.output
})

dependencies {
    run {
        val mainApiCompileOnly by configurations.getting

        mainApiCompileOnly(variantOf(libs.mekanism, "api"))
    }

    run {
        val coreCompileOnly by configurations.getting

        coreCompileOnly(libs.mekanism)
        coreCompileOnly(libs.kotlinForForge)
        coreCompileOnly(libs.easyNestConfig)

        // 【合并改动】EasyNestConfig 原本以 JarJar 内嵌在独立的 Core jar 里；
        // Core 并入本体后改为内嵌在主 jar 中（单一文件分发，无需独立 core 前置）。
        val jarJar by configurations.getting
        jarJar(libs.easyNestConfig) {
            version {
                strictly("[$this,)")
                prefer(this.toString())
            }
        }
    }

    run {
        val coreApiCompileOnly by configurations.getting

        coreApiCompileOnly(libs.kotlinForForge)
        coreApiCompileOnly(variantOf(libs.mekanism, "api"))
    }

    implementation(libs.kotlinForForge)
    implementation(libs.mekanism)
    implementation(variantOf(libs.mekanism, "generators"))

    compileOnly(variantOf(libs.mekanism, "all"))

    compileOnly(libs.mekanismExtras)
    compileOnly(libs.mekanismElements)
    compileOnly(libs.igleelib)
    compileOnly(libs.evolvedMekanism)
    compileOnly(libs.mekanismMoreMachine)
    compileOnly(libs.evolvedMekanismExtras)
    compileOnly(libs.chemlibMekanized)

    localRuntime(libs.jei)

    if (loadAddons) {
        localRuntime(libs.mekanismElements)
        localRuntime(libs.mekanismExtras)
        localRuntime(libs.igleelib)
        localRuntime(libs.evolvedMekanism)
        localRuntime(libs.mekanismMoreMachine)
        // TODO: localRuntime(libs.evolvedMekanismExtras)
//        localRuntime(libs.chemlibMekanized)
    }

    implementation(libs.easyNestConfig)
}

neoForge {
    enable {
        version = libs.versions.neoforge.get()
        this.isDisableRecompilation = System.getenv("CI") == "true"
    }

    addModdingDependenciesTo(coreApiSourceSet)
    addModdingDependenciesTo(coreSourceSet)
    addModdingDependenciesTo(mainApiSourceSet)
    addModdingDependenciesTo(dataSourceSet)

    validateAccessTransformers = true

    accessTransformers {
        val atFile = rootProject.file("src/core/resources/META-INF/accesstransformer.cfg").takeIf(File::exists) ?: return@accessTransformers
        from(atFile)
        publish(atFile)
    }

    parchment {
        mappingsVersion = libs.versions.parchmentmc.get()
        minecraftVersion = mcVersion
    }

    runs {
        create("client", Action {
            client()
            gameDirectory.set(rootProject.file("run"))
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        })

        create("server", Action {
            server()
            gameDirectory.set(rootProject.file("run-server"))
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        })

        create("data", Action {
            data()
            sourceSet = dataSourceSet
            gameDirectory.set(rootProject.file("run-data"))
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        })

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            logLevel = Level.DEBUG
        }
    }

    mods {
        // 【合并改动】core / core.api 两个源码集并入主 mod：单一 mod id（mekanism_empowered），
        // 不再生成独立的 mekanism_empowered_core 条目。
        create(modId, Action {
            sourceSet(mainSourceSet)
            sourceSet(mainApiSourceSet)
            sourceSet(dataSourceSet)
            sourceSet(coreApiSourceSet)
            sourceSet(coreSourceSet)
        })
    }

    ideSyncTask(generateModMetadata)
}

fun setupMetaDataTask(modId: String, modName: String, task: TaskProvider<ProcessResources>, deps: List<ModDep>, at: String? = null) {
    task {
        val replaceProperties: MutableMap<String, String> = mutableMapOf(
            "version" to version.toString(),
            "group" to project.group.toString(),
            "minecraft_version" to mcVersion,
            "mod_loader" to "kotlinforforge",
            "mod_loader_version_range" to GreaterThanOrEqual(kffVersion).toString(),
            "mod_name" to modName,
            "mod_author" to Constants.Mod.AUTHOR,
            "mod_id" to modId,
            "license" to Constants.Mod.LICENSE,
            "description" to Constants.Mod.DESCRIPTION,
            "display_url" to Constants.Mod.REPOSITORY_URL,
            "issue_tracker_url" to Constants.Mod.ISSUE_TRACKER_URL,
            "access_transformers" to "",
            "dependencies" to buildDeps(*deps.toTypedArray(), modId = modId),
        )

        if (at != null) {
            replaceProperties["access_transformers"] = "accessTransformers = [ { file = \"$at\" } ]"
        }

        inputs.properties(replaceProperties)
        filter<ReplaceTokens>("beginToken" to "\${", "endToken" to "}", "tokens" to replaceProperties)
        from(rootProject.file("src/templates"))
        into("build/generated/sources/$modId")
    }
}

fun setupJarTask(modName: String, task: TaskProvider<Jar>, vararg sourceSets: SourceSetOutput) = setupJarTask(modName, false, task, null, *sourceSets)
fun setupJarTask(modName: String, renameFile: Boolean, task: TaskProvider<Jar>, classifier: String? = null, vararg sourceSets: SourceSetOutput) {
    val cleanModName = modName.replace(" ", "").replace(":", "")
    task {
        manifest {
            attributes(
                "Specification-Title" to modName,
                "Specification-Vendor" to Constants.Mod.AUTHOR,
                "Specification-Version" to version,
                "Implementation-Title" to cleanModName,
                "Implementation-Version" to version,
                "Implementation-Vendor" to Constants.Mod.AUTHOR,
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.VERSION")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to mcVersion,
            )
        }

        archiveClassifier.set(classifier)
        if (renameFile) {
            archiveFileName.set("$cleanModName-$mcVersion-${project.version}.jar")
        }
        from(*sourceSets)
    }
}

val baseDependencies = listOf(
    ModDep("neoforge", libs.versions.neoforge..<"21.2"),
    ModDep("minecraft", mcVersion.eq()),
    ModDep("kotlinforforge", kffVersion.gte()),
    ModDep("mekanism", "10.7.19".gte(), ordering = Order.AFTER),
)
val mainModDependencies = baseDependencies.toMutableList().apply {
    // 【合并改动】不再依赖独立的 mekanism_empowered_core（已并入本体）
    add(ModDep.optional("mekanism_extras", "1.4.0".gte()))
    add(ModDep.optional("evolvedmekanism", "1.2.1-fix2".gte()))
    add(ModDep.optional("mekmm", "1.3.3".gte()))
    add(ModDep.optional("emextras", "1.1.1".gte()))
    add(ModDep.incompatible("mekanism_unleashed", "0.0.0".gte(), "Incompatible Mixins"))
    // 无用之物：其通用机械增强与本模组公式/上限/显示全面冲突，
    // 经字节码级排查与实测后决定不兼容（FML 直接拒绝两者共存）。
    add(ModDep.incompatible("useless_mod", "0.0.0".gte(), "Conflicting Mekanism upgrade overrides"))
}

// 【合并改动】单一元数据任务；at 参数让 mods.toml 声明 core 源码集里的访问转换器
setupMetaDataTask(modId, Constants.Mod.NAME, generateModMetadata, mainModDependencies, at = "accesstransformer.cfg")

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = jdkVersion
    }

    java {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(jdkVersion)
            vendor = jvmVendor
        }
        JavaVersion.toVersion(jdkVersion).let {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    kotlin {
        jvmToolchain(jdkVersion)

        compilerOptions {
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    processResources {
        dependsOn(generateModMetadata)
    }

    named<Jar>("sourcesJar") {
        dependsOn(classes, "mainApiClasses", "coreClasses", "coreApiClasses")

        from(
            mainApiSourceSet.kotlin,
            coreSourceSet.kotlin,
            coreApiSourceSet.kotlin,
        )
    }

    // 【合并改动】主 jar 同时打包 core / core.api 的输出（含 core 的 mixin 配置、
    // 访问转换器与服务文件），不再生成独立的 Core jar。
    setupJarTask(Constants.Mod.NAME, jar, mainSourceSet.output, mainApiSourceSet.output, coreSourceSet.output, coreApiSourceSet.output)

    setupJarTask(
        Constants.Mod.NAME,
        false,
        register<Jar>("apiJar"),
        "api",
        mainApiSourceSet.output,
    )

    build {
        dependsOn("apiJar")
    }

    withType<RunGameTask>().configureEach {
        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(jdkVersion))
            vendor.set(jvmVendor)
        })
        standardInput = System.`in`
    }

    withType<Jar>().configureEach {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.ID}" }
        }

        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
