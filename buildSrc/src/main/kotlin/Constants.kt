import org.gradle.jvm.toolchain.JvmVendorSpec


object Constants {
    object Mod {
        const val ID = "mekanism_empowered_unleashed"
        const val NAME = "Mekanism: Empowered Unleashed"
        const val DESCRIPTION = "The conflict-free merge of Mekanism: Empowered and Mekanism Unleashed."
        const val LICENSE = "MIT"
        const val VERSION = "21.1-1.0.1"
        const val GROUP = "dev.lapis256"
        const val AUTHOR = "Lapis256, WhitePhant0m, zaixiayesheng"
        const val REPOSITORY_URL = "https://github.com/zaixiayesheng/MekanismEmpowered-Unleashed"
        const val ISSUE_TRACKER_URL = "$REPOSITORY_URL/issues"
    }

    object Dev {
        const val JDK_VERSION = 21
        @Suppress("UnstableApiUsage")
        val JVM_VENDOR: JvmVendorSpec = JvmVendorSpec.JETBRAINS
    }
}
