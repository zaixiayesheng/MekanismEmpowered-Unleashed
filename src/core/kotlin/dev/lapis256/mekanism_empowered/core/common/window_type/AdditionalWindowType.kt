package dev.lapis256.mekanism_empowered.core.common.window_type

import dev.lapis256.mekanism_empowered.core.api.enum_extension.EnumExtensionRegistry
import mekanism.common.inventory.container.SelectedWindowData


object AdditionalWindowType {
    @JvmStatic
    fun register(internalName: String?, saveName: String, canPin: Boolean, maxData: Byte = 1) =
        AdditionalWindowTypeDelegate(internalName, saveName, canPin, maxData)
            .also { EnumExtensionRegistry.registerEnumEntry(SelectedWindowData.WindowType::class, it) }

    @JvmStatic
    fun register(saveName: String, canPin: Boolean, maxData: Byte = 1) =
        register(null, saveName, canPin, maxData)
}
