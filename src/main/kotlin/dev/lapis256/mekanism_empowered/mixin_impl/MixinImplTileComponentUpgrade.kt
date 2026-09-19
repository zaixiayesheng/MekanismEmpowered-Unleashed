package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade
import mekanism.api.Upgrade
import mekanism.common.tile.component.TileComponentUpgrade
import mekanism.common.util.NBTUtils
import net.minecraft.nbt.CompoundTag
import java.util.EnumMap


object MixinImplTileComponentUpgrade {
    @JvmStatic
    fun TileComponentUpgrade.appendClientSyncData(updateTag: CompoundTag) {
        if (supports(MekEmpUpgrade.AUTO_INSERTER)) {
            updateTag.putBoolean(MekEmpSerializationConstants.INSERTER_INSTALLED, getUpgrades(MekEmpUpgrade.AUTO_INSERTER) > 0)
        }
    }

    @JvmStatic
    fun TileComponentUpgrade.readAppendedClientSyncData(upgrades: EnumMap<Upgrade, Int>, updateTag: CompoundTag) {
        if (supports(MekEmpUpgrade.AUTO_INSERTER)) {
            NBTUtils.setBooleanIfPresent(updateTag, MekEmpSerializationConstants.INSERTER_INSTALLED) { installed ->
                if (installed) {
                    upgrades[MekEmpUpgrade.AUTO_INSERTER] = MekEmpUpgrade.AUTO_INSERTER.max
                } else {
                    upgrades.remove(MekEmpUpgrade.AUTO_INSERTER)
                }
            }
        }
    }

    @JvmStatic
    fun isAppendedClientSyncTarget(upgrade: Upgrade): Boolean {
        return upgrade == MekEmpUpgrade.AUTO_INSERTER
    }
}
