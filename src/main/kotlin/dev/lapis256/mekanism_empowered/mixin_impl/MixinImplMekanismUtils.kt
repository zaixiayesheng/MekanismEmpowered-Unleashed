package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig
import dev.lapis256.mekanism_empowered.core.extension.fractionUpgrades
import dev.lapis256.mekanism_empowered.core.extension.getInstalledOrDefault
import dev.lapis256.mekanism_empowered.core.extension.isEnergyMaxed
import dev.lapis256.mekanism_empowered.core.extension.isSpeedMaxed
import mekanism.common.tile.interfaces.IUpgradeTile
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


object MixinImplMekanismUtils {
    private val multiplier: Double
        get() = MekEmpGeneralConfig.maxUpgradeMultiplier.toDouble()

    @JvmStatic
    fun IUpgradeTile.modifyTicks(original: Double): Double {
        if (!isSpeedMaxed()) {
            return original
        }
        return original * multiplier.pow(-fractionUpgrades(MekEmpUpgrade.EMPOWERED_SPEED))
    }

    @JvmStatic
    fun IUpgradeTile.modifyEnergyPerTick(original: Double): Double {
        val speed = getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_SPEED)
        if (!isSpeedMaxed() || speed <= 0) {
            return original
        }
        val energy = getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_ENERGY)
        // Normalized against the configured tier-2 maximum (8-32, default 16) instead of
        // hardcoded 8, so the energy-neutral invariant holds for every upgrade past the
        // vanilla cap. Merged with Mekanism Unleashed.
        val tierMax = MekEmpUpgrade.EMPOWERED_SPEED.max
        return original * multiplier.pow((2 * speed - min(energy, max(speed, tierMax))) / tierMax.toDouble())
    }

    @JvmStatic
    fun IUpgradeTile.modifyMaxEnergy(original: Double): Double {
        if (!isEnergyMaxed()) {
            return original
        }
        return original * multiplier.pow(2 * fractionUpgrades(MekEmpUpgrade.EMPOWERED_ENERGY))
    }
}
