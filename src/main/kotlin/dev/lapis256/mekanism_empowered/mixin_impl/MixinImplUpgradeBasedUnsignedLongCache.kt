package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig
import mekanism.api.math.MathUtils
import mekanism.common.attachments.component.UpgradeAware
import kotlin.math.pow


class MixinImplUpgradeBasedUnsignedLongCache {
    private var lastEmpoweredInstalled = 0
    private var value = 0L

    fun modifyMaxEnergy(upgradeAware: UpgradeAware, base: Long): Long {
        val empoweredInstalled = upgradeAware.getUpgradeCount(MekEmpUpgrade.EMPOWERED_ENERGY)
        if (empoweredInstalled <= 0) {
            return base
        }

        if (lastEmpoweredInstalled != empoweredInstalled) {
            lastEmpoweredInstalled = empoweredInstalled
            value = MathUtils.clampToLong(base * MekEmpGeneralConfig.maxUpgradeMultiplier.toDouble().pow(empoweredInstalled / MekEmpUpgrade.EMPOWERED_ENERGY.max.toDouble()))
        }

        return value
    }
}
