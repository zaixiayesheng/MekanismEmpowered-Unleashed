package dev.lapis256.mekanism_empowered.core.common.window_type

import dev.lapis256.mekanism_empowered.core.api.enum_extension.EnumEntryDelegate
import mekanism.common.inventory.container.SelectedWindowData


class AdditionalWindowTypeDelegate(
    internalName: String?,
    val saveName: String,
    val canPin: Boolean,
    val maxData: Byte
) : EnumEntryDelegate<SelectedWindowData.WindowType>(SelectedWindowData.WindowType::class, internalName)
