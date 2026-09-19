package dev.lapis256.mekanism_empowered.core.extension

import mekanism.common.tile.component.config.DataType


inline val DataType.canInput
    get() = this == DataType.INPUT || this == DataType.INPUT_OUTPUT || this == DataType.INPUT_1 || this == DataType.INPUT_2
