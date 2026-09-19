package dev.lapis256.mekanism_empowered.core.common.config

import dev.lapis256.easy_nest_config.api.AbstractNestConfig
import dev.lapis256.mekanism_empowered.core.common.MekanismEmpoweredCore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mekanism.common.config.IMekanismConfig
import mekanism.common.config.value.*
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec
import kotlin.reflect.KProperty


@Suppress("Unused")
abstract class MekanismNestConfig(type: ModConfig.Type, name: String) : AbstractNestConfig(type, name), IMekanismConfig {
    private val cachedConfigValues: MutableList<CachedValue<*>> = mutableListOf()

    object ConfigSaveScope : CoroutineScope {
        private val job = SupervisorJob()
        override val coroutineContext = Dispatchers.IO + job
    }

    override fun save() {
        ConfigSaveScope.launch {
            for (attempt in 0..2) {
                val result = runCatching { configSpec.save() }
                if (result.isSuccess) return@launch
                MekanismEmpoweredCore.LOGGER.error("Failed to save config", result.exceptionOrNull())
                if (attempt == 2) MekanismEmpoweredCore.LOGGER.error("Giving up")
            }
        }
    }

    override fun clearCache(unloading: Boolean) {
        cachedConfigValues.forEach { it.clearCache(unloading) }
    }

    override fun addCachedValue(configValue: CachedValue<*>) {
        cachedConfigValues.add(configValue)
    }

    override fun getFileName(): String = name
    override fun getConfigType(): ModConfig.Type = type
    override fun getConfigSpec(): ModConfigSpec = spec


    fun ModConfigSpec.ConfigValue<Boolean>.cached(): CachedBooleanValue = CachedBooleanValue.wrap(this@MekanismNestConfig, this)
    fun ModConfigSpec.ConfigValue<Byte>.cached(): CachedByteValue = CachedByteValue.wrap(this@MekanismNestConfig, this)
    fun <T> ModConfigSpec.ConfigValue<T>.cached(): CachedConfigValue<T> = CachedConfigValue.wrap(this@MekanismNestConfig, this)
    fun ModConfigSpec.ConfigValue<Double>.cached(): CachedDoubleValue = CachedDoubleValue.wrap(this@MekanismNestConfig, this)
    fun <E : Enum<E>> ModConfigSpec.EnumValue<E>.cached(): CachedConfigValue<E> = CachedEnumValue.wrap(this@MekanismNestConfig, this)
    fun ModConfigSpec.ConfigValue<Double>.cachedF(): CachedFloatValue = CachedFloatValue.wrap(this@MekanismNestConfig, this)
    fun ModConfigSpec.ConfigValue<Int>.cached(): CachedIntValue = CachedIntValue.wrap(this@MekanismNestConfig, this)
    fun ModConfigSpec.ConfigValue<Long>.cached(): CachedLongValue = CachedLongValue.wrap(this@MekanismNestConfig, this)
    fun ModConfigSpec.ConfigValue<Short>.cached(): CachedShortValue = CachedShortValue.wrap(this@MekanismNestConfig, this)

    operator fun CachedBooleanValue.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun CachedByteValue.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun <T> CachedConfigValue<T>.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun CachedDoubleValue.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun <E : Enum<E>> CachedEnumValue<E>.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun CachedFloatValue.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun CachedIntValue.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun CachedLongValue.getValue(a: Any?, p: KProperty<*>) = get()
    operator fun CachedShortValue.getValue(a: Any?, p: KProperty<*>) = get()
}
