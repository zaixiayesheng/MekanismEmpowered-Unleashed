package dev.lapis256.mekanism_empowered.core.api.enum_extension

import dev.lapis256.mekanism_empowered.core.api.MekanismEmpoweredCoreAPI
import kotlin.reflect.KClass


abstract class EnumExtensionLoader<ENUM : Enum<ENUM>, D : EnumEntryDelegate<ENUM>> {
    abstract fun initEnumExtensions(): List<IEnumExtension>

    abstract fun constructEntry(ordinal: Int, property: D): ENUM


    fun init() = initEnumExtensions().forEach { MekanismEmpoweredCoreAPI.LOGGER.info("Loaded enum extension: $it") }

    inline fun <reified E : Enum<E>> initAdditionalEnumEntry(builtInUpgrades: Array<E>) = buildList(builtInUpgrades.size) {
        addAll(builtInUpgrades)

        init()

        var id = builtInUpgrades.size
        for (entry in EnumExtensionRegistry.getEnumEntry(E::class)) {
            @Suppress("UNCHECKED_CAST")
            val property = entry as D
            add(constructEntry(id++, property) as E)
        }
    }.toTypedArray()

    fun loadEnumExtensions(target: KClass<out IEnumExtension>) =
        target.java.classLoader.getResources("META-INF/services/${target.qualifiedName}")
            .asSequence()
            .flatMap { it.openStream().bufferedReader().readLines() }
            .map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .map { Class.forName(it, true, target.java.classLoader) }
            .filterIsInstance<Class<out IEnumExtension>>()
            .map {
                when {
                    it.kotlin.objectInstance != null -> it.kotlin.objectInstance
                    target.java.isAssignableFrom(it) -> it.getDeclaredConstructor().newInstance()
                    else -> null
                } ?: error("Cannot instantiate ${it.name}")
            }
            .toList()

}
