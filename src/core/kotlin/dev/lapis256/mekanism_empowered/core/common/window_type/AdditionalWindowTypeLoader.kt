package dev.lapis256.mekanism_empowered.core.common.window_type

import dev.lapis256.mekanism_empowered.core.api.enum_extension.EnumExtensionLoader
import mekanism.common.inventory.container.SelectedWindowData


class AdditionalWindowTypeLoader(
    val constructor: (String, Int, String, Boolean, Byte) -> SelectedWindowData.WindowType
) : EnumExtensionLoader<SelectedWindowData.WindowType, AdditionalWindowTypeDelegate>() {
    override fun initEnumExtensions() = loadEnumExtensions(IAdditionalWindowTypes::class)

    override fun constructEntry(ordinal: Int, property: AdditionalWindowTypeDelegate) =
        constructor(property.internalName, ordinal, property.saveName, property.canPin, property.maxData)
}
