package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.MekanismEmpowered
import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig
import dev.lapis256.mekanism_empowered.core.extension.fractionUpgrades
import dev.lapis256.mekanism_empowered.core.extension.getInstalledOrDefault
import dev.lapis256.mekanism_empowered.core.extension.isEnergyMaxed
import dev.lapis256.mekanism_empowered.core.extension.isSpeedMaxed
import mekanism.api.Upgrade
import mekanism.common.tile.interfaces.IUpgradeTile
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


object MixinImplMekanismUtils {
    private val multiplier: Double
        get() = MekEmpGeneralConfig.maxUpgradeMultiplier.toDouble()

    /**
     * 诊断日志开关：JVM 参数 -Dmekanism_empowered.debug=true 时，
     * 在强化层的每个入口打印实际数值（机器类、速度/强化数量、
     * 是否解锁、倍率），用于排查"与其他模组并存时强化不生效"。
     */
    private val debugLog: Boolean = System.getProperty("mekanism_empowered.debug") != null

    @JvmStatic
    fun IUpgradeTile.modifyTicks(original: Double): Double {
        if (!isSpeedMaxed()) {
            if (debugLog) MekanismEmpowered.LOGGER.info(
                "[debug] modifyTicks {} speed={} empowered={} maxed=false original={}",
                javaClass.name, getInstalledOrDefault(Upgrade.SPEED),
                getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_SPEED), original
            )
            return original
        }
        val result = original * multiplier.pow(-fractionUpgrades(MekEmpUpgrade.EMPOWERED_SPEED))
        if (debugLog) MekanismEmpowered.LOGGER.info(
            "[debug] modifyTicks {} speed={} empowered={} maxed=true original={} -> {}",
            javaClass.name, getInstalledOrDefault(Upgrade.SPEED),
            getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_SPEED), original, result
        )
        return result
    }

    @JvmStatic
    fun IUpgradeTile.modifyEnergyPerTick(original: Double): Double {
        val speed = getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_SPEED)
        if (!isSpeedMaxed() || speed <= 0) {
            if (debugLog) MekanismEmpowered.LOGGER.info(
                "[debug] modifyEnergyPerTick {} speed(vanilla)={} empoweredSpeed={} gated=true original={}",
                javaClass.name, getInstalledOrDefault(Upgrade.SPEED), speed, original
            )
            return original
        }
        val energy = getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_ENERGY)
        // Normalized against the configured tier-2 maximum (8-32, default 16) instead of
        // hardcoded 8, so the energy-neutral invariant holds for every upgrade past the
        // vanilla cap. Merged with Mekanism Unleashed.
        val tierMax = MekEmpUpgrade.EMPOWERED_SPEED.max
        val result = original * multiplier.pow((2 * speed - min(energy, max(speed, tierMax))) / tierMax.toDouble())
        if (debugLog) MekanismEmpowered.LOGGER.info(
            "[debug] modifyEnergyPerTick {} vanillaSpeed={} empoweredSpeed={} empoweredEnergy={} tierMax={} original={} -> {}",
            javaClass.name, getInstalledOrDefault(Upgrade.SPEED), speed, energy, tierMax, original, result
        )
        return result
    }

    @JvmStatic
    fun IUpgradeTile.modifyMaxEnergy(original: Double): Double {
        if (!isEnergyMaxed()) {
            return original
        }
        val result = original * multiplier.pow(2 * fractionUpgrades(MekEmpUpgrade.EMPOWERED_ENERGY))
        if (debugLog) MekanismEmpowered.LOGGER.info(
            "[debug] modifyMaxEnergy {} vanillaEnergy={} empoweredEnergy={} original={} -> {}",
            javaClass.name, getInstalledOrDefault(Upgrade.ENERGY),
            getInstalledOrDefault(MekEmpUpgrade.EMPOWERED_ENERGY), original, result
        )
        return result
    }
}
