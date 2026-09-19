package dev.lapis256.mekanism_empowered.core.api

import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object MekanismEmpoweredCoreAPI {
    const val MOD_ID = "mekanism_empowered_unleashed"
    const val MOD_NAME = "Mekanism: Empowered Unleashed"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun rl(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}
