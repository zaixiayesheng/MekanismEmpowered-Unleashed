package dev.lapis256.mekanism_empowered.core.common.upgrade

import dev.lapis256.mekanism_empowered.core.api.upgrade.AdditionalUpgradeDelegate
import mekanism.api.Upgrade
import net.minecraft.core.Holder
import net.minecraft.world.item.Item


/**
 * Maintains a registry mapping upgrades to their corresponding item representations.
 *
 * This is primarily used to allow upgrades to be represented and handled as tangible items in-game.
 */
@Suppress("Unused")
object UpgradeItemRegistry {
    private val upgradeMap = mutableMapOf<Upgrade, Holder<Item>>()

    /**
     * Retrieves the item associated with the specified upgrade.
     *
     * @param upgrade The upgrade whose associated item should be retrieved.
     * @return The item associated with the upgrade, or `null` if none is registered.
     */
    @JvmStatic
    fun getItem(upgrade: Upgrade) = upgradeMap[upgrade]

    /**
     * Registers an item to represent the specified upgrade.
     * If the upgrade was already registered, the item will be overwritten.
     *
     * @param upgrade The upgrade to associate with the item.
     * @param item The item to associate with the upgrade.
     */
    fun register(upgrade: Upgrade, item: Holder<Item>) {
        upgradeMap[upgrade] = item
    }

    /**
     * Registers an item to represent the specified upgrade.
     * This overload supports [AdditionalUpgradeDelegate] for interoperability with Java code.
     * If the upgrade was already registered, the item will be overwritten.
     *
     * @param upgrade The upgrade property to associate with the item.
     * @param item The item to associate with the upgrade.
     */
    @JvmStatic
    fun register(upgrade: AdditionalUpgradeDelegate, item: Holder<Item>) {
        register(upgrade.get(), item)
    }
}
