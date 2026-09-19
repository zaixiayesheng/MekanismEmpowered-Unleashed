package dev.lapis256.mekanism_empowered.core.api.upgrade

import dev.lapis256.mekanism_empowered.core.api.enum_extension.EnumExtensionRegistry
import mekanism.api.Upgrade
import mekanism.api.text.EnumColor
import mekanism.api.text.ILangEntry


/**
 * Stores and manages all [AdditionalUpgradeDelegate] instances.
 * In Kotlin, when using delegated properties (e.g., `val UPGRADE by register(...)}`),
 * `internalName` can be omitted; the property name will be automatically used as the `internalName`.
 *
 * Example:
 * ```kt
 * val TEST: Upgrade by AdditionalUpgrade.register(
 *     "test", APILang.UPGRADE_TEST, APILang.UPGRADE_TEST_DESCRIPTION, 8, EnumColor.DARK_GREEN
 * )
 *
 * val TEST: Supplier<Upgrade> = AdditionalUpgrade.register(
 *     "TEST", "test", APILang.UPGRADE_TEST, APILang.UPGRADE_TEST_DESCRIPTION, 8, EnumColor.DARK_GREEN
 * )
 * ```
 *
 * This object also interacts with the [IAdditionalUpgrades] interface, which is intended to define additional upgrades.
 * Implementations of [IAdditionalUpgrades] allow you to register custom upgrades via [register].
 */
object AdditionalUpgrade {
    /**
     * Registers a new upgrade with an explicit internal identifier.
     *
     * @param internalName A unique identifier for the upgrade.
     * @param name A name used as part of the associated item's identifier.
     * @param langKey The translation key for the upgrade name.
     * @param descLangKey The translation key for the upgrade description.
     * @param maxStack The maximum number of upgrades installable.
     * @param color The UI color associated with this upgrade.
     * @return The registered upgrade property.
     */
    @JvmStatic
    fun register(internalName: String?, name: String, langKey: ILangEntry, descLangKey: ILangEntry, maxStack: Int, color: EnumColor) =
        AdditionalUpgradeDelegate(internalName, name, langKey, descLangKey, maxStack, color)
            .also { EnumExtensionRegistry.registerEnumEntry(Upgrade::class, it) }

    /**
     * Registers a new upgrade using the property name as the internal identifier.
     * Intended for use with Kotlin delegated properties.
     *
     * @param name A name used as part of the associated item's identifier.
     * @param langKey The translation key for the upgrade name.
     * @param descLangKey The translation key for the upgrade description.
     * @param maxStack The maximum number of upgrades installable.
     * @param color The UI color associated with this upgrade.
     * @return The registered upgrade property.
     */
    @JvmStatic
    fun register(name: String, langKey: ILangEntry, descLangKey: ILangEntry, maxStack: Int, color: EnumColor) =
        register(null, name, langKey, descLangKey, maxStack, color)
}
