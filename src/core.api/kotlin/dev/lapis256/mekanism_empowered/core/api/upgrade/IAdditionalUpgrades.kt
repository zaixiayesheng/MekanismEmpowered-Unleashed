package dev.lapis256.mekanism_empowered.core.api.upgrade

import dev.lapis256.mekanism_empowered.core.api.enum_extension.IEnumExtension

/**
 * Interface for defining additional upgrades.
 *
 * Implementing this interface allows you to register upgrades via [AdditionalUpgrade.register].
 * Additionally, classes implementing this interface should be registered using a provider-configuration file
 * to ensure they are properly discovered and used at runtime.
 * The provider-configuration file should be placed in the `META-INF/services` directory and contain the fully
 * qualified name of the implementation class.
 *
 * Example (Kotlin):
 * ```kt
 * object TestUpgradesKt : IAdditionalUpgrades {
 *     @JvmStatic
 *     val TEST: Upgrade by AdditionalUpgrade.registerUpgrade(
 *         "test", APILang.UPGRADE_TEST, APILang.UPGRADE_TEST_DESCRIPTION, 8, EnumColor.DARK_GREEN
 *     )
 * }
 * ```
 *
 * Example (Java):
 * ```java
 * public class TestUpgrades implements IAdditionalUpgrades {
 *     public static final Supplier<Upgrade> TEST = AdditionalUpgrade.registerUpgrade(
 *         "TEST", "test", APILang.UPGRADE_TEST, APILang.UPGRADE_TEST_DESCRIPTION, 8, EnumColor.DARK_GREEN
 *     );
 * }
 * ```
 *
 * To register the implementation, create a provider-configuration file:
 * - Place a file named `dev.lapis256.mekanism_empowered.core.api.upgrade.IAdditionalUpgrades` in the `META-INF/services` directory.
 * - Inside this file, specify the fully qualified name of the implementation class:
 *   ```
 *   com.example.mekanism_example.TestUpgradesKt
 *   com.example.mekanism_example.TestUpgrades
 *   ```
 */
interface IAdditionalUpgrades : IEnumExtension
