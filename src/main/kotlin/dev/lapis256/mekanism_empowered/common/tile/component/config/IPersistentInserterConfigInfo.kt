package dev.lapis256.mekanism_empowered.common.tile.component.config

import mekanism.api.RelativeSide


interface IPersistentInserterConfigInfo {
    val config: MutableMap<RelativeSide, Boolean>

    fun isSideEnabled(relativeSide: RelativeSide): Boolean
}
