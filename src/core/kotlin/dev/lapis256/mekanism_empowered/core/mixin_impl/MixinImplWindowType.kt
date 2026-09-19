package dev.lapis256.mekanism_empowered.core.mixin_impl

import dev.lapis256.mekanism_empowered.core.common.window_type.AdditionalWindowTypeLoader
import mekanism.common.inventory.container.SelectedWindowData


class MixinImplWindowType(constructor: (String, Int, String, Boolean, Byte) -> SelectedWindowData.WindowType) {
    private val loader = AdditionalWindowTypeLoader(constructor)

    fun initAdditionalWindowTypes(builtInUpgrades: Array<SelectedWindowData.WindowType>) = loader.initAdditionalEnumEntry(builtInUpgrades)
}
