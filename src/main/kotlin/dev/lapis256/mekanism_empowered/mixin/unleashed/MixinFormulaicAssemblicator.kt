package dev.lapis256.mekanism_empowered.mixin.unleashed

import dev.lapis256.mekanism_empowered.unleashed.Temp
import mekanism.common.tile.machine.TileEntityFormulaicAssemblicator
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * 公式装配器：支持"每 tick 多次合成"。
 *
 * 每次完成一次合成（`doSingleCraft`）之后，若 `ticksRequired`
 * 为负，就通过 [Temp.inject] 循环补跑 onUpdateServer 补齐剩余
 * 次数。递归由 [Temp.isInjecting] 拦截，避免无限嵌套。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [TileEntityFormulaicAssemblicator::class], remap = false)
abstract class MixinFormulaicAssemblicator {

    /** 原版字段：单次合成所需 tick 数（负值 = 一 tick 合成多次）。 */
    @Shadow
    private var ticksRequired: Int = 0

    /** 原版每 tick 更新入口。 */
    @Shadow
    protected abstract fun onUpdateServer(): Boolean

    @Inject(
        method = ["onUpdateServer"],
        at = [
            At(
                value = "INVOKE",
                target = "Lmekanism/common/tile/machine/TileEntityFormulaicAssemblicator;doSingleCraft()Z",
                shift = At.Shift.AFTER
            )
        ]
    )
    fun injected(cir: CallbackInfoReturnable<Boolean>) {
        // 完成一次合成后，按负 tick 数补齐剩余次数
        Temp.inject.accept(ticksRequired, Runnable { onUpdateServer() })
    }
}
