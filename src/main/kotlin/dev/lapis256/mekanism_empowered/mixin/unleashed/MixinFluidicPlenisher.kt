package dev.lapis256.mekanism_empowered.mixin.unleashed

import dev.lapis256.mekanism_empowered.unleashed.RawTicksHolder
import dev.lapis256.mekanism_empowered.unleashed.Temp
import mekanism.common.tile.machine.TileEntityFluidicPlenisher
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.max
import kotlin.math.min

/**
 * 流体装填器：支持"每 tick 多次装填"，机制与泵相同：
 * 每次完成装填（`doPlenish`）后按暂存的原始编码值补跑。
 *
 * 与无用之物（UselessMod）并存：它同点位的回调读的是恒 >= 1 的
 * `ticksRequired` 字段，补次循环天然空转，无需 mod id 判断。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [TileEntityFluidicPlenisher::class], remap = false)
abstract class MixinFluidicPlenisher : RawTicksHolder {

    /** 原版 getTicks 钳制前的原始编码值（负 = 一 tick 多次装填）。 */
    @field:Unique
    @JvmField
    var rawTicks: Int = 1

    override fun getRawTicks(): Int = rawTicks

    override fun setRawTicks(raw: Int) {
        rawTicks = raw
    }

    /** 原版每 tick 更新入口。 */
    @Shadow
    protected abstract fun onUpdateServer(): Boolean

    @Inject(
        method = ["onUpdateServer"],
        at = [
            At(
                value = "INVOKE",
                target = "Lmekanism/common/tile/machine/TileEntityFluidicPlenisher;doPlenish()V",
                shift = At.Shift.AFTER
            )
        ]
    )
    fun injected(cir: CallbackInfoReturnable<Boolean>) {
        val extras = min(128, max(1, -rawTicks) - 1)
        if (extras > 0) {
            Temp.inject.accept(-extras, Runnable { onUpdateServer() })
        }
    }
}
