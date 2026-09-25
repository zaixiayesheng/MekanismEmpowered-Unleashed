package dev.lapis256.mekanism_empowered.unleashed

import dev.lapis256.mekanism_empowered.common.MekanismEmpowered
import net.neoforged.fml.ModList

/**
 * 与无用之物（UselessMod）共存的开关与状态。
 *
 * 1.0.4 起不再声明不兼容，改为由本模组把冲突点逐个接管：
 *
 * - 升级公式（`MekanismUtils` 的三个方法）：本模组低优先级先应用、先 cancel，
 *   对方的同类回调再也执行不到 → 见 [dev.lapis256.mekanism_empowered.mixin.unleashed.MixinMekanismUtils]；
 * - 升级文案（`UpgradeUtils` 的两个 `@Overwrite`）：本模组先应用即胜出 → 见
 *   [dev.lapis256.mekanism_empowered.mixin.unleashed.MixinUpgradeUtils]；
 * - 弹出延迟（`TileComponentEjector#outputItems`）：对方在 RETURN 里把
 *   `tickDelay` 写成 0，我们在同一个返回点再写回本模组的值 → 见
 *   [dev.lapis256.mekanism_empowered.mixin.common.tile.component.MixinTileComponentEjector]；
 * - 四台机器的"每 tick 多件"：本模组把机器字段钳在 >= 1，对方同点位的补次回调
 *   读到正数后 `min(-ticks, 128)` 恒 <= 0，天然空转（详见各机器混入的注释）；
 * - 对方改枚举构造器上限的那条：Mekanism 的 `maxStack` 是 private、全源码只经
 *   `getMax()` 读取，而 `getMax()` 由本模组接管，因此不受影响。
 *
 * 它其余功能（合金炉、牛肉工具、矿物生成器等）不受影响。
 */
object UselessModCompat {
    /** 无用之物是否在场。 */
    @JvmField
    val LOADED: Boolean = ModList.get().isLoaded("useless_mod")

    private var logged = false

    /** 第一次用到时在日志里说明一句，方便排查。 */
    @JvmStatic
    fun logOnce() {
        if (LOADED && !logged) {
            logged = true
            MekanismEmpowered.LOGGER.info("检测到无用之物（UselessMod）：它的通用机械升级公式、升级文案与弹出延迟由本模组接管，其余功能照常。")
        }
    }
}
