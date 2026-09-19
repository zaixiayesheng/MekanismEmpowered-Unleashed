package dev.lapis256.mekanism_empowered.common.tile.component.config

import mekanism.api.RelativeSide
import java.util.EnumMap


class InserterConfigInfo : IPersistentInserterConfigInfo {
    override val config: MutableMap<RelativeSide, Boolean> = EnumMap(RelativeSide::class.java)

    override fun isSideEnabled(relativeSide: RelativeSide) = config.getOrDefault(relativeSide, false)

    fun setSideConfig(relativeSide: RelativeSide, enabled: Boolean) {
        config[relativeSide] = enabled
    }

    fun toggleSideConfig(relativeSide: RelativeSide) {
        config[relativeSide] = !isSideEnabled(relativeSide)
    }
}
