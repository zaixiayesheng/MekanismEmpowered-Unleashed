package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.core.extension.getInstalled
import mekanism.api.math.MathUtils
import mekanism.common.tile.base.TileEntityMekanism
import kotlin.math.pow


object MixinImplTileComponentEjector {
    @JvmStatic
    fun TileEntityMekanism.modifyTickDelay(original: Int): Int {
        val installed = getInstalled(MekEmpUpgrade.FAST_ITEM_EJECT) ?: return original
        val max = MekEmpUpgrade.FAST_ITEM_EJECT.max.toDouble()
        return MathUtils.clampToInt((original + 1.0).pow((max - installed) / max) - 1)
    }

    @JvmStatic
    fun TileEntityMekanism.modifyFluidEjectRate(original: Int): Int {
        val installed = getInstalled(MekEmpUpgrade.IO_CAPACITY) ?: return original
        val max = MekEmpUpgrade.IO_CAPACITY.max.toDouble()
        return MathUtils.clampToInt(original + original * 8 * (installed / max))
    }

    @JvmStatic
    fun TileEntityMekanism.modifyChemicalEjectRate(original: Long): Long {
        val installed = getInstalled(MekEmpUpgrade.IO_CAPACITY) ?: return original
        val max = MekEmpUpgrade.IO_CAPACITY.max.toDouble()
        return MathUtils.clampToLong(original + original * 8 * (installed / max))
    }
}
