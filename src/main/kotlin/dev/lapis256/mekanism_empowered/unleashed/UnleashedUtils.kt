package dev.lapis256.mekanism_empowered.unleashed

import dev.lapis256.mekanism_empowered.common.config.MekEmpGeneralConfig
import mekanism.api.Upgrade
import mekanism.common.config.MekanismConfig
import mekanism.common.tile.interfaces.IUpgradeTile
import mekanism.common.tile.machine.TileEntityDigitalMiner
import mekanism.common.tile.machine.TileEntityElectricPump
import mekanism.common.tile.machine.TileEntityFluidicPlenisher
import mekanism.common.tile.machine.TileEntityFormulaicAssemblicator
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * 数值公式工具集：合并自 Mekanism Unleashed (WhitePhant0m)。
 *
 * 这里集中了"提升原版升级上限（可配置 8-32）"所需的全部公式，
 * 供 [dev.lapis256.mekanism_empowered.mixin.unleashed.MixinMekanismUtils]
 * 以定点注入的方式替换 Mekanism 原版 `MekanismUtils` 中的对应计算。
 */
object UnleashedUtils {

    /**
     * 这些机器不走"合成器"路径（它们有各自专属的 Mixin 处理
     * 负 tick 补次），其余机器统一按合成器钳制最少 1 tick。
     */
    private val NON_CRAFTER_CLASSES: Set<Class<*>> = setOf(
        TileEntityDigitalMiner::class.java,
        TileEntityFluidicPlenisher::class.java,
        TileEntityFormulaicAssemblicator::class.java,
        TileEntityElectricPump::class.java
    )

    fun isCrafter(tile: IUpgradeTile): Boolean = tile.javaClass !in NON_CRAFTER_CLASSES

    /**
     * 速度时间系数：每插 1 个原版速度升级，单次操作耗时
     * × 倍率^(-1/8)（即每个升级约提速 33%，与原版节奏一致）。
     */
    fun time(tile: IUpgradeTile): Double =
        MekanismConfig.general.maxUpgradeMultiplier.get().toDouble().pow(tile.component.getUpgrades(Upgrade.SPEED) / -8.0)

    /**
     * 每 tick 能耗的指数部分。
     *
     * 原版公式为 `(2*speed - energy) / 8`，但 Unleashed 原版把能量项
     * 封顶在 `max(8, speed)`——导致"能量升级超过 8 个不再减耗"的 bug。
     * 这里把封顶值改为**跟随配置上限**（默认 16，范围 8-32），
     * 保证插满能量升级时每个都真实计入减耗。
     *
     * 能量中性的关键：速度数 == 能量数时，指数恰好抵消 [time] 的指数，
     * 即"每操作能耗 = 基础值 ×1"，提速只体现在吞吐上。
     */
    fun energyExponent(tile: IUpgradeTile): Double {
        val speed = tile.component.getUpgrades(Upgrade.SPEED)
        val energy = tile.component.getUpgrades(Upgrade.ENERGY)
        val maxUpgrades = maxUpgrades()
        return (2 * speed - min(energy, max(maxUpgrades, speed))) / 8.0
    }

    /**
     * 能量容量系数：每插 1 个原版能量升级，容量 × 倍率^(1/8)。
     */
    fun capacity(tile: IUpgradeTile): Double =
        MekanismConfig.general.maxUpgradeMultiplier.get().toDouble().pow(tile.component.getUpgrades(Upgrade.ENERGY) / 8.0)

    /**
     * 配置安全读取：升级上限。
     *
     * SERVER 类型配置在注册表初始化阶段**尚未加载**，
     * 而 `Upgrade#getMax()` 可能在那个阶段就被调用（例如盔甲材质
     * 注册、工具提示缓存构建），直接读配置会抛
     * `IllegalStateException: Cannot get config value before config is loaded`。
     * 因此这里捕获异常并回退到配置默认值 16；游戏内（配置已加载）
     * 的读取不受影响，正常返回玩家配置值。
     */
    fun maxUpgrades(): Int = try {
        max(8, min(32, MekEmpGeneralConfig.maxUpgrades))
    } catch (e: IllegalStateException) {
        16
    }

    /**
     * 配置安全读取：Meka 装备附魔开关（回退默认 false），
     * 理由同 [maxUpgrades]。
     */
    fun enchantableMekaGear(): Boolean = try {
        MekEmpGeneralConfig.enchantableMekaGear
    } catch (e: IllegalStateException) {
        false
    }

    /**
     * 把数值格式化为"有效数字 + 科学计数法"的显示字符串，
     * 用于升级效果提示（例如 2.5E3）。
     */
    fun exponential(d0: Double): String {
        val significant = 4
        val exp = floor(log10(d0)).toInt()
        var d = d0 * 10.0.pow(-exp)
        d = Math.round(d * 10.0.pow(significant - 1)).toDouble() / 10.0.pow(significant - 1)
        val dt = Math.round(d * 10.0.pow(significant - 1)).toDouble() / 10.0.pow(significant - 1 - exp)
        return if (abs(exp) <= significant - 1) dt.toString() else "$d" + "E$exp"
    }
}
