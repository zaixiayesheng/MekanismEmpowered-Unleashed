package dev.lapis256.mekanism_empowered.mixin.unleashed

import com.llamalad7.mixinextras.injector.ModifyReturnValue
import com.llamalad7.mixinextras.sugar.Local
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplMekanismUtils.modifyEnergyPerTick
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplMekanismUtils.modifyMaxEnergy
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplMekanismUtils.modifyTicks
import dev.lapis256.mekanism_empowered.unleashed.RawTicksHolder
import dev.lapis256.mekanism_empowered.unleashed.UnleashedUtils
import mekanism.api.Upgrade
import mekanism.api.math.MathUtils
import mekanism.common.config.MekanismConfig
import mekanism.common.tile.interfaces.IUpgradeTile
import mekanism.common.util.MekanismUtils
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.max
import kotlin.math.pow

/**
 * 数值中枢：对 Mekanism 的全局公式 `MekanismUtils` 做修改。
 *
 * 历史：原版 Unleashed 用 `@Overwrite` 整段替换方法体，与 Empowered 的
 * 定点注入互相破坏（合并前崩溃的根源）。合并后曾改为"定点注入、注入点
 * 错开"，让两个模组的效果按顺序相乘。
 *
 * 现状：为了让合并版在**其他同样接管公式的模组**（如 UselessMod 的
 * Mekanism 升级模块，它在 HEAD 直接 setReturnValue 截胡整个方法）存在时
 * 依然生效，改为 **HEAD 注入 + 低优先级 + cancel** 的整体接管：
 *
 * - `priority = 500`（低于默认 1000）保证本模组的回调最先执行。
 *   Mixin 是**按优先级升序应用**的（见 MixinInfo#compareTo），优先级越低
 *   越先应用，同一个注入点上的回调也越先执行；先 cancel 者胜。
 *   1.0.3 之前这里写的是 2000，方向反了 —— 实测无用之物的回调排在
 *   我们前面，它一 cancel 我们就再也不会执行，这才是"增强升级失效"
 *   的真正原因；
 * - 先 cancel 者胜，后续回调与方法体都不会再跑；
 * - 回调内部按"原版公式 → 本模组第一层 → Empowered 第二层 → 编码"
 *   的顺序完整复现整条链（直接调用 Empowered 的 mixin 实现函数），
 *   因此无论 Empowered 的注入点是否被执行，结果都一致。
 *
 * 组合语义：机器未插新升级时 Empowered 的倍率为 1，等于只有
 * 本模组生效；插满两层后两者相乘，实现 16+16 的双层体系。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [MekanismUtils::class], remap = false, priority = 500)
class MixinMekanismUtils {

    /*
     * 目标方法全部是静态方法，handler 必须是静态方法：
     * 放在 private companion 中并用 @JvmStatic 提升到外部类上。
     * （Mixin 禁止 mixin 类携带非私有静态字段，companion 必须 private。）
     */
    private companion object {

        /** 原版 getTicksD 的基础计算：def × 倍率^(-speed/8)。 */
        @JvmStatic
        private fun vanillaTicksD(tile: IUpgradeTile, def: Int): Double =
            def * MekanismConfig.general.maxUpgradeMultiplier.get().toDouble()
                .pow(tile.component.getUpgrades(Upgrade.SPEED) / -8.0)

        /**
         * getTicks：合成器机器钳制最少 1 tick。
         *
         * 原因：getTicksD 为负（快于 1 tick/操作）时，普通机器走
         * getOperationsPerTick 的"每 tick 多操作"路径；而四台特殊
         * 机器（泵/矿机/装填器/装配器）有自己的补次 Mixin。其余
         * 机器（即"合成器"）没有补次机制，必须保证 ticks >= 1。
         *
         * 四台特殊机器同样保持钳制，但在钳制发生前把原始编码值
         * 通过 [RawTicksHolder] 暂存到方块实体上，供补次回调换算
         * "每 tick 操作次数"。机器字段恒 >= 1，这让无用之物
         * （UselessMod）同类的补次回调读到正数后天然空转。
         */
        @JvmStatic
        @ModifyReturnValue(method = ["getTicks"], at = [At("RETURN")])
        private fun mekanismUnleashedGetTicks(original: Int, @Local(argsOnly = true) tile: IUpgradeTile, @Local(argsOnly = true) def: Int): Int {
            if (tile is RawTicksHolder) {
                tile.setRawTicks(MathUtils.clampToInt(MekanismUtils.getTicksD(tile, def)))
            }
            return if (UnleashedUtils.isCrafter(tile)) max(1, original) else original
        }

        /**
         * getTicksD 整体接管：
         * 原版公式 → Empowered 第二层倍率 → 负 tick 编码。
         */
        @JvmStatic
        @Inject(method = ["getTicksD"], at = [At("HEAD")], cancellable = true)
        private fun mekanismUnleashedGetTicksD(tile: IUpgradeTile, def: Int, cir: CallbackInfoReturnable<Double>) {
            val tier2 = tile.modifyTicks(vanillaTicksD(tile, def))
            // 负值编码"一 tick 应执行多少次操作"
            val encoded = if (tier2 >= 1) MathUtils.clampToInt(tier2).toDouble()
            else MathUtils.clampToInt(1 / tier2) * -1.0
            cir.setReturnValue(encoded)
            cir.cancel()
        }

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
            cir.cancel()
        }

        /**
         * getEnergyPerTick 整体接管：
         * 本模组第一层指数 → Empowered 第二层倍率 → ceilToLong。
         */
        @JvmStatic
        @Inject(method = ["getEnergyPerTick"], at = [At("HEAD")], cancellable = true)
        private fun mekanismUnleashedGetEnergyPerTick(tile: IUpgradeTile, def: Long, cir: CallbackInfoReturnable<Long>) {
            val base = def * MekanismConfig.general.maxUpgradeMultiplier.get().toDouble()
                .pow(UnleashedUtils.energyExponent(tile))
            val tier2 = tile.modifyEnergyPerTick(base)
            cir.setReturnValue(MathUtils.ceilToLong(tier2))
            cir.cancel()
        }

        /**
         * getMaxEnergy 整体接管：
         * 原版容量公式 → Empowered 第二层倍率 → clampToLong。
         * （本模组本身不改 getMaxEnergy，但必须接管，否则其他模组的
         * HEAD 截胡会连带吞掉 Empowered 的储能加成。）
         */
        @JvmStatic
        @Inject(method = ["getMaxEnergy"], at = [At("HEAD")], cancellable = true)
        private fun mekanismUnleashedGetMaxEnergy(tile: IUpgradeTile, def: Long, cir: CallbackInfoReturnable<Long>) {
            val base = def * MekanismConfig.general.maxUpgradeMultiplier.get().toDouble()
                .pow(tile.component.getUpgrades(Upgrade.ENERGY) / 8.0)
            val tier2 = tile.modifyMaxEnergy(base)
            cir.setReturnValue(MathUtils.clampToLong(tier2))
            cir.cancel()
        }
    }
}
