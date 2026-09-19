package dev.lapis256.mekanism_empowered.core.api.upgrade

import dev.lapis256.mekanism_empowered.core.api.enum_extension.EnumEntryDelegate
import mekanism.api.Upgrade
import mekanism.api.text.EnumColor
import mekanism.api.text.ILangEntry


class AdditionalUpgradeDelegate(
    internalName: String?,
    val name: String,
    val langKey: ILangEntry,
    val descLangKey: ILangEntry,
    val maxStack: Int,
    val color: EnumColor
) : EnumEntryDelegate<Upgrade>(Upgrade::class, internalName)
