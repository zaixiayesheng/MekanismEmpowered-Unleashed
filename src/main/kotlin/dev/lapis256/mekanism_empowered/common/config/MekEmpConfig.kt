package dev.lapis256.mekanism_empowered.common.config

import dev.lapis256.easy_nest_config.impl.ConfigHelper
import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.core.common.config.MekanismNestConfig
import mekanism.common.config.MekanismConfigHelper
import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.IConfigSpec
import net.neoforged.fml.event.config.ModConfigEvent


object MekEmpConfig {
    private lateinit var helper: ConfigHelper
    private val configs: Map<IConfigSpec, MekanismNestConfig>
        get() = helper.configs.asSequence()
            .filter { it.value is MekanismNestConfig }
            .map { it.key to it.value as MekanismNestConfig }
            .toMap()

    val configValues: List<MekanismNestConfig>
        get() = configs.values.toList()

    fun registerConfigs(modContainer: ModContainer) {
        helper = ConfigHelper(modContainer, MekanismEmpoweredAPI.MOD_NAME_CLEAN)

        helper.registerApplyHandler(ConfigTranslationHandler)

        helper.registerConfig(MekEmpGeneralConfig)
        helper.registerConfig(MekEmpTierConfig)
    }

    fun onConfigLoad(configEvent: ModConfigEvent) {
        MekanismConfigHelper.onConfigLoad(configEvent, MekanismEmpoweredAPI.MOD_ID, configs)
    }
}
