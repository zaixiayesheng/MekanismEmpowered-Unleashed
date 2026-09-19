package dev.lapis256.mekanism_empowered.mixin.unleashed

import dev.lapis256.mekanism_empowered.unleashed.RawTicksHolder
import mekanism.common.tile.machine.TileEntityDigitalMiner
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.max
import kotlin.math.min

/**
 * 数字矿机：支持"每 tick 多次采矿"。
 *
 * 注入点在每 tick 的能量扣除（第 2 次 `extract`）之后：先扣完电，
 * 再按 [RawTicksHolder] 暂存的原始编码值补采剩余次数。
 *
 * 原版的 `delay` 字段恒 >= 1（负值只存在于我们的暂存值里），因此
 * 无用之物（UselessMod）同类回调里 `delay < 0` 的判断永远不成立，
 * 它的补采循环天然空转，无需任何 mod id 判断。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [TileEntityDigitalMiner::class], remap = false)
abstract class MixinDigiMiner : RawTicksHolder {

    /** 原版 getTicks 钳制前的原始编码值（负 = 一 tick 多次采矿）。 */
    @field:Unique
    @JvmField
    var rawTicks: Int = 1

    override fun getRawTicks(): Int = rawTicks

    override fun setRawTicks(raw: Int) {
        rawTicks = raw
    }

    /** 单次采矿的实际执行方法。 */
    @Shadow
    protected abstract fun tryMineBlock()

    @Inject(
        method = ["onUpdateServer"],
        at = [
            At(
                value = "INVOKE",
                target = "Lmekanism/common/capabilities/energy/MinerEnergyContainer;extract(JLmekanism/api/Action;Lmekanism/api/AutomationType;)J",
                ordinal = 1,
                shift = At.Shift.AFTER
            )
        ]
    )
    fun injected(cir: CallbackInfoReturnable<Boolean>) {
        // 本 tick 总采矿次数 = -原始编码值；原版 delay 机制本身每 delayLength
        // tick 采 1 次（极速时即每 tick 1 次），这里补齐剩余次数并封顶 128。
        val extras = min(128, max(1, -rawTicks) - 1)
        repeat(extras) { tryMineBlock() }
    }
}
