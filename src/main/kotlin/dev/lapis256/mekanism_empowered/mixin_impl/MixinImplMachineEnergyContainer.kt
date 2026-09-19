package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.component.TileComponentUpgrade


object MixinImplMachineEnergyContainer {
    @JvmStatic
    fun TileEntityMekanism.modifyUpdateMaxEnergyTarget(original: Boolean) = original || supportsUpgrade(MekEmpUpgrade.EMPOWERED_ENERGY)

    @JvmStatic
    fun TileComponentUpgrade.modifyUpdateEnergyPerTickTarget(original: Boolean) =
        original || supports(MekEmpUpgrade.EMPOWERED_ENERGY) || supports(MekEmpUpgrade.EMPOWERED_SPEED)
}
