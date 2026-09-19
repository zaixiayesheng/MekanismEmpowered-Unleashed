package dev.lapis256.mekanism_empowered.api.text

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.core.api.text.ILangEnglishHolder
import mekanism.api.text.ILangEntry
import net.minecraft.Util

enum class MekEmpAPILang(type: String, path: String, override val english: String) : ILangEntry, ILangEnglishHolder {
    UPGRADE_SPEED("upgrade", "speed", "Empowered Speed"),
    UPGRADE_SPEED_DESCRIPTION("upgrade", "speed.description", "Greatly increases machine speed. Requires maxed out Speed Upgrades"),
    UPGRADE_ENERGY("upgrade", "energy", "Empowered Energy"),
    UPGRADE_ENERGY_DESCRIPTION("upgrade", "energy.description", "Reduces energy cost from Empowered Speed Upgrade"),
    UPGRADE_FAST_ITEM_EJECT("upgrade", "fast_item_eject", "Fast Item Eject"),
    UPGRADE_FAST_ITEM_EJECT_DESCRIPTION("upgrade", "fast_item_eject.description",  "Increases item ejection speed"),
    UPGRADE_FAST_ITEM_INSERT("upgrade", "fast_item_insert", "Fast Item Insert"),
    UPGRADE_FAST_ITEM_INSERT_DESCRIPTION("upgrade", "fast_item_insert.description", "Increases item insertion speed. Requires Auto Inserter"),
    UPGRADE_IO_CAPACITY("upgrade", "io_capacity", "I/O Capacity"),
    UPGRADE_IO_CAPACITY_DESCRIPTION("upgrade", "io_capacity.description", "Increases number of items transferred per I/O operation"),
    UPGRADE_AUTO_INSERTER("upgrade", "auto_inserter", "Auto Inserter"),
    UPGRADE_AUTO_INSERTER_DESCRIPTION("upgrade", "auto_inserter.description", "Adds automatic item insertion to machines"),
    ;

    val key: String = Util.makeDescriptionId(type, MekanismEmpoweredAPI.rl(path))

    override fun getTranslationKey(): String = key
}
