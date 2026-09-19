package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import mekanism.api.Upgrade


object MixinImplModifyRecalculationTarget {
    @JvmStatic
    fun modifySpeed(original: Boolean, upgrade: Upgrade) = original || upgrade == MekEmpUpgrade.EMPOWERED_SPEED
}
