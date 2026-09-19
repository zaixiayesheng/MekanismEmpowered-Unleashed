package dev.lapis256.mekanism_empowered.mixin.unleashed

import dev.lapis256.mekanism_empowered.unleashed.UnleashedUtils
import mekanism.api.Upgrade
import mekanism.common.MekanismLang
import mekanism.common.config.MekanismConfig
import mekanism.common.tile.interfaces.IUpgradeTile
import mekanism.common.util.UpgradeUtils
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import java.util.ArrayList
import kotlin.math.pow

/**
 * 升级效果显示修正：修正 `UpgradeUtils` 里两条信息文案的数值。
 *
 * 这里保留了 `@Overwrite`（而非定点注入），因为 Empowered 的
 * Mixin 只碰 `getItem` / `getInfo`，与本类的两个方法无交集，
 * 不会冲突。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [UpgradeUtils::class], remap = false)
class MixinUpgradeUtils {

    /*
     * 两个被覆写的方法都是静态方法，且 @Overwrite 要求方法附带
     * @author/@reason 的文档注释（Mixin 强制）。
     * companion 必须 private：见 MixinUpgradeMax 的说明。
     */
    private companion object {

        /**
         * getExpScaledInfo：指数型机器的速度效果文案。
         * 每插 1 个升级，效果按 2 的幂次展示。
         *
         * @author WhitePhant0m, zaixiayesheng
         * @reason Show correct Speed effect on Exponentially scaling machines.
         */
        @JvmStatic
        @Overwrite
        fun getExpScaledInfo(tile: IUpgradeTile, upgrade: Upgrade): List<Component> {
            val ret = ArrayList<Component>()
            if (tile.supportsUpgrades() && upgrade.max > 1) {
                ret.add(
                    MekanismLang.UPGRADES_EFFECT.translate(
                        UnleashedUtils.exponential(2.0.pow(tile.component.getUpgrades(upgrade).toDouble()))
                    )
                )
            }
            return ret
        }

        /**
         * getMultScaledInfo：乘数型机器的速度/能量效果文案。
         * 能量按容量系数展示、速度按时间系数的倒数展示，
         * 其余升级按"已装数/上限"的幂次展示。
         *
         * @author WhitePhant0m, nin8995(original author), zaixiayesheng
         * @reason Show correct Speed/Energy effect.
         */
        @JvmStatic
        @Overwrite
        fun getMultScaledInfo(tile: IUpgradeTile, upgrade: Upgrade): List<Component> {
            val ret = ArrayList<Component>()
            if (tile.supportsUpgrades() && upgrade.max > 1) {
                val effect = when {
                    upgrade == Upgrade.ENERGY -> UnleashedUtils.capacity(tile)
                    upgrade == Upgrade.SPEED -> 1 / UnleashedUtils.time(tile)
                    else -> MekanismConfig.general.maxUpgradeMultiplier.get().toDouble()
                        .pow(tile.component.getUpgrades(upgrade).toDouble() / upgrade.max.toDouble())
                }
                ret.add(MekanismLang.UPGRADES_EFFECT.translate(UnleashedUtils.exponential(effect)))
            }
            return ret
        }
    }
}
