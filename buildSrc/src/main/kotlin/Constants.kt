import org.gradle.jvm.toolchain.JvmVendorSpec


object Constants {
    object Mod {
        const val ID = "mekanism_empowered"
        const val NAME = "Mekanism: Empowered"
        const val DESCRIPTION = "An addon mod that empowers Mekanism. Merged with Mekanism Unleashed."
        const val LICENSE = "MIT"
        const val VERSION = "21.1-1.0.0"
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
