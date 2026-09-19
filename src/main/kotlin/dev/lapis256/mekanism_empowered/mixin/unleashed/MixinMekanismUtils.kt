package dev.lapis256.mekanism_empowered.mixin.unleashed

import com.llamalad7.mixinextras.injector.ModifyReturnValue
import com.llamalad7.mixinextras.sugar.Local
import dev.lapis256.mekanism_empowered.unleashed.UnleashedUtils
import mekanism.api.math.MathUtils
import mekanism.common.tile.interfaces.IUpgradeTile
import mekanism.common.util.MekanismUtils
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyArg
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.max

/**
 * 数值中枢：对 Mekanism 的全局公式 `MekanismUtils` 做定点修改。
 *
 * 这是合并前两个模组互相冲突的核心位置。原版 Unleashed 用
 * `@Overwrite` 整段替换方法体，会与 Empowered 挂在原版指令上
 * 的注入互相破坏；本实现改用**定点注入**，且注入点与 Empowered
 * 刻意错开，两边效果按顺序相乘：
 *
 * | 方法 | Empowered 的注入点 | 本类的注入点 |
 * |---|---|---|
 * | getTicksD | RETURN（乘第二层倍率）| RETURN（负 tick 编码，作用于最终值）|
 * | getEnergyPerTick | ceilToLong 的参数（乘第二层倍率）| Math.pow 的指数（替换第一层公式）|
 *
 * 组合语义：机器未插新升级时 Empowered 的倍率为 1，等于只有
 * 本模组生效；插满两层后两者相乘，实现 16+16 的双层体系。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [MekanismUtils::class], remap = false)
class MixinMekanismUtils {

    /*
     * 目标方法全部是静态方法，handler 必须是静态方法：
     * 放在 private companion 中并用 @JvmStatic 提升到外部类上。
     * （Mixin 禁止 mixin 类携带非私有静态字段，companion 必须 private。）
     */
    private companion object {

        /**
         * getTicks：合成器机器钳制最少 1 tick。
         *
         * 原因：getTicksD 为负（快于 1 tick/操作）时，普通机器走
         * getOperationsPerTick 的"每 tick 多操作"路径；而四台特殊
         * 机器（泵/矿机/装填器/装配器）有自己的补次 Mixin。其余
         * 机器（即"合成器"）没有补次机制，必须保证 ticks >= 1，
         * 否则会出现除零或跳过工作。
         */
        @JvmStatic
        @ModifyReturnValue(method = ["getTicks"], at = [At("RETURN")])
        private fun mekanismUnleashedGetTicks(original: Int, @Local(argsOnly = true) tile: IUpgradeTile): Int =
            if (UnleashedUtils.isCrafter(tile)) max(1, original) else original

        /**
         * getTicksD：把"快于 1 tick/操作"编码为**负数**。
         *
         * 返回值 >= 1 时保持原值（取整）；< 1 时返回
         * `-clamp(1/d)`——即"一 tick 应执行多少次操作"的负数形式。
         * 下游的 getTicks / getOperationsPerTick 据此解读。
         *
         * 注入在 RETURN：Empowered 的第二层倍率会先乘上去，
         * 本编码作用于**最终缩放后的值**，两层顺序无关、语义正确。
         */
        @JvmStatic
        @ModifyReturnValue(method = ["getTicksD"], at = [At("RETURN")])
        private fun mekanismUnleashedGetTicksD(original: Double): Double =
            if (original >= 1) MathUtils.clampToInt(original).toDouble()
            else MathUtils.clampToInt(1 / original) * -1.0

        /**
         * getOperationsPerTick：把负 tick 数翻译成"每 tick 操作次数"。
         *
         * 用 HEAD 注入 + 直接捕获方法参数（不能用 @Local sugar：
         * 两个 int 参数类型相同，歧义无法消除，这是实测踩过的坑）。
         */
        @JvmStatic
        @Inject(method = ["getOperationsPerTick"], at = [At("HEAD")], cancellable = true)
        private fun mekanismUnleashedGetOperationsPerTick(
            tile: IUpgradeTile,
            defTicks: Int,
            defaultOperations: Int,
            cir: CallbackInfoReturnable<Int>
        ) {
            val ticksD = MekanismUtils.getTicksD(tile, defTicks)
            if (ticksD >= 1) {
                cir.returnValue = defaultOperations
            } else {
                // -ticksD = 每 tick 操作次数；乘以基准操作数后钳制到 int
                cir.returnValue = MathUtils.clampToInt(max(1.0, -ticksD) * defaultOperations)
            }
        }

        /**
         * getEnergyPerTick：替换能耗公式的**指数**（修复减耗封顶 bug）。
         *
         * 注入在 `Math.pow` 的指数参数（index = 1）上，与 Empowered
         * 挂在 `ceilToLong` 参数上的注入互不重叠。新指数由
         * [UnleashedUtils.energyExponent] 计算：能量升级的计效上限
         * 跟随配置（8-32）而非硬编码 8。
         */
        @JvmStatic
        @ModifyArg(
            method = ["getEnergyPerTick"],
            at = At(value = "INVOKE", target = "Ljava/lang/Math;pow(DD)D"),
            index = 1
        )
        private fun mekanismUnleashedEnergyExponent(original: Double, @Local(argsOnly = true) tile: IUpgradeTile): Double =
            UnleashedUtils.energyExponent(tile)
    }
}
