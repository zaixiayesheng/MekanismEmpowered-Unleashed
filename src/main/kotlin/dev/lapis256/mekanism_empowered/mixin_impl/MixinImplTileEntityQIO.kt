package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.core.extension.getInstalled
import mekanism.api.Upgrade
import mekanism.api.math.MathUtils
import mekanism.common.tile.qio.TileEntityQIOComponent
import kotlin.math.pow


object MixinImplTileEntityQIO {
    @JvmStatic
    fun TileEntityQIOComponent.modifyMaxTransitCount(original: Int, speedUpgrades: Int): Int {
        if (speedUpgrades < Upgrade.SPEED.max) {
            return original
        }

        val installed = getInstalled(MekEmpUpgrade.IO_CAPACITY) ?: return original

        // 320 to 1344 items
        return original + 128 * installed
    }

    @JvmStatic
    fun TileEntityQIOComponent.modifyMaxTransitTypes(original: Int, speedUpgrades: Int): Int {
        if (speedUpgrades < Upgrade.SPEED.max) {
            return original
        }

        // 5 to 13 types
        return original + (getInstalled(MekEmpUpgrade.EMPOWERED_SPEED) ?: return original)
    }

    @JvmStatic
    fun isAdditionalRecalculationTarget(upgrade: Upgrade) = upgrade == MekEmpUpgrade.IO_CAPACITY

    private fun TileEntityQIOComponent.modifyTickDelay(original: Int, upgrade: Upgrade): Int {
        val installed = getInstalled(upgrade) ?: return original
        val max = upgrade.max.toDouble()
        return MathUtils.clampToInt((original + 1.0).pow((max - installed) / max) - 1)
    }

    @JvmStatic
    fun TileEntityQIOComponent.modifyExporterTickDelay(original: Int) = modifyTickDelay(original, MekEmpUpgrade.FAST_ITEM_EJECT)

    @JvmStatic
    fun TileEntityQIOComponent.modifyImporterTickDelay(original: Int) = modifyTickDelay(original, MekEmpUpgrade.FAST_ITEM_INSERT)

}
