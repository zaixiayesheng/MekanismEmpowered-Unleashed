package dev.lapis256.mekanism_empowered.common.tile.interfaces

import dev.lapis256.mekanism_empowered.common.tile.component.TileComponentInserterConfig
import dev.lapis256.mekanism_empowered.common.tile.component.TileComponentInserter
import mekanism.common.tile.interfaces.ISideConfiguration


interface ISideInserterConfiguration : ISideConfiguration {
    fun `mekanismEmpowered$getInserterConfig`(): TileComponentInserterConfig
    fun `mekanismEmpowered$getInserter`(): TileComponentInserter?
}
