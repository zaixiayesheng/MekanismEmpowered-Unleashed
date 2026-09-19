package dev.lapis256.mekanism_empowered.core.api.enum_extension

import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


abstract class EnumEntryDelegate<ENUM : Enum<ENUM>>(enumClass: KClass<ENUM>, private var internal: String?) :
    ReadOnlyProperty<Any?, ENUM>, Supplier<ENUM> {

    val internalName get() = internal ?: error("Internal name is null!")

    private val cachedEntry: ENUM by lazy { enumClass.java.enumConstants.first { it.name == internalName } }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = cachedEntry
    override fun get() = cachedEntry

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, ENUM> {
        if (internal == null) {
            internal = property.name
        }
        return this
    }
}
