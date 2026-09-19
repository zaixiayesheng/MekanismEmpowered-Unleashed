package dev.lapis256.mekanism_empowered.mixin.unleashed

import com.llamalad7.mixinextras.injector.ModifyReturnValue
import dev.lapis256.mekanism_empowered.unleashed.UnleashedUtils
import mekanism.api.Upgrade
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import java.util.Locale

/**
 * 升级上限中枢：让速度/能量升级的上限**可配置**（8-32，默认 16）。
 *
 * 实现方式是在运行时改写 `Upgrade#getMax()` 的返回值：
 *
 * - 相比原版 Unleashed 的"枚举构造器补丁"（在类加载期修改
 *   maxStack 参数），本方案**每次调用时读配置**，配置修改即时
 *   生效，也没有类加载时序问题；
 * - 一个旋钮同时控制四条升级线：原版 SPEED/ENERGY 与强化
 *   SPEED/ENERGY（按枚举常量名匹配，大小写都兼容）；
 * - Empowered 的第二层解锁判断（`isSpeedMaxed()` 比较
 *   `Upgrade.SPEED.max`）会自动跟随这个值——"插满 N 解锁"
 *   的 N 就是配置值；
 * - 配置未加载时（注册表阶段）由 [UnleashedUtils.maxUpgrades]
 *   安全回退到默认值 16。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [Upgrade::class], remap = false)
class MixinUpgradeMax {

    /** 改写 getMax()：受控的四条升级线返回配置值，其余原样。 */
    @ModifyReturnValue(method = ["getMax"], at = [At("RETURN")])
    fun mekanismUnleashedScaledMax(original: Int): Int {
        val self = this as Upgrade
        return if (!isScaled(self.name)) original else UnleashedUtils.maxUpgrades()
    }

    /*
     * 注意：Kotlin 的 companion object 会生成非私有的静态字段，
     * 违反 Mixin 的硬规则，所以必须声明为 private companion。
     */
    private companion object {

        /** 受配置控制上限的四条升级线（枚举常量名）。 */
        private val SCALED_UPGRADES = setOf("SPEED", "ENERGY", "EMPOWERED_SPEED", "EMPOWERED_ENERGY")

        /** 大小写都兼容的匹配（新增升级的枚举名大小写取决于扩展加载器）。 */
        private fun isScaled(name: String): Boolean =
            name in SCALED_UPGRADES || name.uppercase(Locale.ROOT) in SCALED_UPGRADES
    }
}
