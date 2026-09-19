package dev.lapis256.mekanism_empowered.core.api.enum_extension

import kotlin.reflect.KClass


object EnumExtensionRegistry {
    val enumExtensions = mutableMapOf<KClass<*>, MutableList<EnumEntryDelegate<*>>>()

    fun <ENUM : Enum<ENUM>> registerEnumEntry(enumClass: KClass<ENUM>, entry: EnumEntryDelegate<ENUM>) {
        enumExtensions.computeIfAbsent(enumClass) { mutableListOf() }.add(entry)
    }

    fun <ENUM : Enum<ENUM>> getEnumEntry(enumClass: KClass<ENUM>): List<EnumEntryDelegate<ENUM>> {
        return enumExtensions[enumClass]?.filterIsInstance<EnumEntryDelegate<ENUM>>() ?: emptyList()
    }
}
