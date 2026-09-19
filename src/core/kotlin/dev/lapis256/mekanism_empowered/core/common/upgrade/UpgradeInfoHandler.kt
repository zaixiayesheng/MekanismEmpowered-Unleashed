package dev.lapis256.mekanism_empowered.core.common.upgrade

import mekanism.api.Upgrade
import mekanism.common.tile.interfaces.ITileUpgradable
import net.minecraft.network.chat.Component
import kotlin.reflect.KClass


typealias UpgradeInfoFunction = (tile: ITileUpgradable, upgrade: Upgrade) -> List<Component>

/**
 * Handles registration and retrieval of upgrade-related information for upgradable tiles.
 *
 * The priority order for determining the upgrade information provider is:
 * 1. Explicit overrides registered via [UpgradeInfoRegistry.registerOverrideForTiles] (highest priority)
 * 2. Overrides from subclasses overriding {@link ITileUpgradable#getInfo}
 * 3. The fallback provider given during registration (the lowest priority)
 */
object UpgradeInfoHandler {

    /**
     * Registry for managing upgrade information providers for specific tile types.
     *
     * Provides fallback behavior if no specific override is found.
     *
     * Priority order:
     * 1. Overrides registered via [registerOverrideForTiles]
     * 2. Subclasses overriding [ITileUpgradable.getInfo]
     * 3. Fallback provider
     */
    class UpgradeInfoRegistry(
        private val fallbackProvider: UpgradeInfoFunction
    ) {
        private val overrides = mutableSetOf<Pair<Class<out ITileUpgradable>, UpgradeInfoFunction>>()
        private val resolvedCache = mutableMapOf<Class<out ITileUpgradable>, UpgradeInfoFunction?>()

        @JvmSynthetic
        internal fun getInfo(tile: ITileUpgradable, upgrade: Upgrade) = resolvedCache.getOrPut(tile.javaClass) {
            overrides.firstOrNull { it.first.isAssignableFrom(tile.javaClass) }?.second ?: if (!isOverriddenMethod(tile)) fallbackProvider else null
        }?.invoke(tile, upgrade)

        /**
         * Registers overrides conditionally based on a load flag.
         *
         * @param isLoad Whether to perform registration.
         * @param invoker The registration logic to execute if allowed.
         * @return This registry instance.
         */
        fun conditionallyRegisterOverride(isLoad: Boolean, invoker: UpgradeInfoRegistry.() -> Unit): UpgradeInfoRegistry {
            if (isLoad) {
                invoker(this)
            }
            return this
        }

        /**
         * Registers an upgrade info provider for the given tile types (KClass version).
         * Has the highest priority when resolving info.
         *
         * @param tile The tile classes to associate with the provider.
         * @param infoProvider The function supplying upgrade info.
         * @return This registry instance.
         */
        fun registerOverrideForTiles(vararg tile: KClass<out ITileUpgradable>, infoProvider: UpgradeInfoFunction) =
            registerOverrideForTiles(tile.map(KClass<out ITileUpgradable>::java), infoProvider)

        /**
         * Registers an upgrade info provider for the given tile types (Class version).
         * Has the highest priority when resolving info.
         *
         * @param tile The tile classes to associate with the provider.
         * @param infoProvider The function supplying upgrade info.
         * @return This registry instance.
         */
        fun registerOverrideForTiles(vararg tile: Class<out ITileUpgradable>, infoProvider: UpgradeInfoFunction) =
            registerOverrideForTiles(tile.toList(), infoProvider)

        private fun registerOverrideForTiles(tile: List<Class<out ITileUpgradable>>, infoProvider: UpgradeInfoFunction): UpgradeInfoRegistry {
            tile.map { it to infoProvider }.forEach(overrides::add)
            return this
        }
    }

    private val upgradeMap = mutableMapOf<Upgrade, UpgradeInfoRegistry>()

    /**
     * Registers a fallback upgrade info provider for the given upgrade.
     *
     * @param upgrade The upgrade to associate with the provider.
     * @param fallbackProvider The fallback provider to use when no higher-priority info is found.
     * @return The created registry.
     */
    fun register(upgrade: Upgrade, fallbackProvider: UpgradeInfoFunction) = UpgradeInfoRegistry(fallbackProvider).also { upgradeMap[upgrade] = it }

    @JvmSynthetic
    internal fun getInfo(tile: ITileUpgradable, upgrade: Upgrade) = upgradeMap[upgrade]?.getInfo(tile, upgrade)

    private val isOverriddenCache = mutableMapOf<Class<out ITileUpgradable>, Boolean>()

    private val superMethod = ITileUpgradable::class.java.getDeclaredMethod("getInfo", Upgrade::class.java)

    private fun isOverriddenMethod(tile: ITileUpgradable) = isOverriddenMethod(tile.javaClass)

    private fun isOverriddenMethod(subClass: Class<out ITileUpgradable>) = isOverriddenCache.getOrPut(subClass) {
        val subMethod = runCatching { subClass.getMethod("getInfo", Upgrade::class.java) }.getOrNull() ?: return false
        subMethod.declaringClass != superMethod.declaringClass
    }
}
