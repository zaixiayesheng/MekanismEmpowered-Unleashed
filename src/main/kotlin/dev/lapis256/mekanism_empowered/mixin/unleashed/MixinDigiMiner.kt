package dev.lapis256.mekanism_empowered.mixin.unleashed

import mekanism.api.Upgrade
import mekanism.common.tile.machine.TileEntityDigitalMiner
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Slice
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * 数字矿机：支持"每 tick 多次采矿"。
 *
 * 当速度升级把单次采矿耗时压到 1 tick 以内时，Mekanism 的
 * `delay` 会变成负数。原版对此无能为力，这里在每 tick 的能量
 * 扣除之后，按 `-delay` 的次数补跑 [tryMineBlock]，然后重算
 * 升级并刷新延迟计数。
 *
 * 注入点说明：
 * - 位置选在 `MinerEnergyContainer.extract(...)` 调用**之后**——
 *   确保先扣完这一 tick 的电，再执行补次采矿；
 * - 用 `slice` 把扫描范围限制在 `setActive` 之后的代码段，
 *   避免误匹配方法里其他位置的 extract 调用。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [TileEntityDigitalMiner::class], remap = false)
abstract class MixinDigiMiner {

    /** 原版私有字段：采矿延迟计数（负数表示"一 tick 应采多次"）。 */
    @Shadow
    private var delay: Int = 0

    /** 单次采矿的实际执行方法。 */
    @Shadow
    protected abstract fun tryMineBlock()

    /** 升级变化时重算缓存的入口（数字矿机自己重写了该方法）。 */
    @Shadow
    abstract fun recalculateUpgrades(upgrade: Upgrade?)

    /** 读取当前延迟计数。 */
    @Shadow
    abstract fun getDelay(): Int

    @Inject(
        method = ["onUpdateServer"],
        at = [
            At(
                value = "INVOKE",
                target = "Lmekanism/common/capabilities/energy/MinerEnergyContainer;extract(JLmekanism/api/Action;Lmekanism/api/AutomationType;)J",
                shift = At.Shift.AFTER
            )
        ],
        slice = [Slice(from = At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityDigitalMiner;setActive(Z)V"))]
    )
    fun injected(cir: CallbackInfoReturnable<Boolean>) {
        if (delay < 0) {
            // delay 为负：一 tick 内补采 (-delay) 次
            for (i in delay - 1 until 0) tryMineBlock()
            // 重算速度升级带来的新延迟，并写回私有字段
            recalculateUpgrades(Upgrade.SPEED)
            (this as DelayAccessor).setDelay(getDelay())
        }
    }
}
