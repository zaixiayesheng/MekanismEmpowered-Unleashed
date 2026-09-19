package dev.lapis256.mekanism_empowered.common.inventory.container

import dev.lapis256.mekanism_empowered.core.common.window_type.IAdditionalWindowTypes
import dev.lapis256.mekanism_empowered.core.common.window_type.AdditionalWindowType


object MekEmpWindowType : IAdditionalWindowTypes {
    val INSERTER by AdditionalWindowType.register("inserter", true, 1)
}
