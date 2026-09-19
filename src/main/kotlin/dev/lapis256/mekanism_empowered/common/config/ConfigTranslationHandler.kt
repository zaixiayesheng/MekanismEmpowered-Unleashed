package dev.lapis256.mekanism_empowered.common.config

import dev.lapis256.easy_nest_config.api.IApplyHandler
import net.neoforged.neoforge.common.ModConfigSpec
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigTranslation(val value: MekEmpConfigTranslations)

object ConfigTranslationHandler : IApplyHandler {
    override fun apply(clazz: KClass<*>, builder: ModConfigSpec.Builder) {
        val annotation = clazz.findAnnotation<ConfigTranslation>() ?: return
        annotation.value.applyToBuilder(builder)
    }
}
