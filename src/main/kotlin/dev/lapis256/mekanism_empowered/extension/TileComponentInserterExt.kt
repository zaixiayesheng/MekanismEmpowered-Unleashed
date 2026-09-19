package dev.lapis256.mekanism_empowered.extension

import dev.lapis256.mekanism_empowered.common.tile.interfaces.ISideInserterConfiguration
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.interfaces.ISideConfiguration


@Suppress("Unused")
inline val <TILE> TILE.inserterConfig where TILE : TileEntityMekanism, TILE : ISideConfiguration
    get() = (this as ISideInserterConfiguration).`mekanismEmpowered$getInserterConfig`()

@Suppress("Unused")
inline val <TILE> TILE.inserter where TILE : TileEntityMekanism, TILE : ISideConfiguration
    get() = (this as ISideInserterConfiguration).`mekanismEmpowered$getInserter`()
