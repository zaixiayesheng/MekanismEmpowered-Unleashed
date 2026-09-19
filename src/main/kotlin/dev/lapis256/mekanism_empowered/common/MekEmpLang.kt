package dev.lapis256.mekanism_empowered.common

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.core.api.text.ILangEnglishHolder
import mekanism.api.text.ILangEntry
import net.minecraft.Util

enum class MekEmpLang(type: String, path: String, override val english: String) : ILangEntry, ILangEnglishHolder {
    INSERTER_CONFIG("gui.configuration", "inserter", "Inserter Config"),
    ;

    val key: String = Util.makeDescriptionId(type, MekanismEmpoweredAPI.rl(path))

    override fun getTranslationKey() = key
}
