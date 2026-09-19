package dev.lapis256.mekanism_empowered.common.config

import dev.lapis256.easy_nest_config.api.NestConfig
import dev.lapis256.mekanism_empowered.core.common.config.MekanismNestConfig
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.fluids.FluidType


object MekEmpTierConfig : MekanismNestConfig(ModConfig.Type.SERVER, "tiers") {

    @NestConfig
    @ConfigTranslation(MekEmpConfigTranslations.TIER_GAUGE_DROPPER)
    object GaugeDropper {
        val basicRate = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_BASIC_RATE.applyToBuilder(builder)
            .defineInRange("basicRate", 256 * 3, 1, Int.MAX_VALUE).cached()
        val basicCapacity = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_BASIC_CAPACITY.applyToBuilder(builder)
            .defineInRange("basicCapacity", 16 * FluidType.BUCKET_VOLUME * 3, 1, Int.MAX_VALUE).cached()
        val advancedRate = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_ADVANCED_RATE.applyToBuilder(builder)
            .defineInRange("advancedRate", 256 * 5, 1, Int.MAX_VALUE).cached()
        val advancedCapacity = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_ADVANCED_CAPACITY.applyToBuilder(builder)
            .defineInRange("advancedCapacity", 16 * FluidType.BUCKET_VOLUME * 5, 1, Int.MAX_VALUE).cached()
        val eliteRate = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_ELITE_RATE.applyToBuilder(builder)
            .defineInRange("eliteRate", 256 * 7, 1, Int.MAX_VALUE).cached()
        val eliteCapacity = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_ELITE_CAPACITY.applyToBuilder(builder)
            .defineInRange("eliteCapacity", 16 * FluidType.BUCKET_VOLUME * 7, 1, Int.MAX_VALUE).cached()
        val ultimateRate = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_ULTIMATE_RATE.applyToBuilder(builder)
            .defineInRange("ultimateRate", 256 * 9, 1, Int.MAX_VALUE).cached()
        val ultimateCapacity = MekEmpConfigTranslations.TIER_GAUGE_DROPPER_ULTIMATE_CAPACITY.applyToBuilder(builder)
            .defineInRange("ultimateCapacity", 16 * FluidType.BUCKET_VOLUME * 9, 1, Int.MAX_VALUE).cached()
    }

    override fun getTranslation() = "Tier Config"
}
