package dev.lapis256.mekanism_empowered.api

import net.minecraft.resources.ResourceLocation


object MekanismEmpoweredAPI {
    const val MOD_ID = "mekanism_empowered"
    const val MOD_NAME = "Mekanism: Empowered"
    val MOD_NAME_CLEAN = MOD_NAME.replace(": ", "")

    fun rl(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}
