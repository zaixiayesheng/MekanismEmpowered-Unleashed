package dev.lapis256.mekanism_empowered.core.common

import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Merged into the main mod (single mod id). The former @Mod entry point
 * initialization now lives in [dev.lapis256.mekanism_empowered.common.MekanismEmpowered];
 * only the shared logger remains here.
 */
object MekanismEmpoweredCore {
    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger(MekanismEmpoweredCoreAPI.MOD_ID)
}
