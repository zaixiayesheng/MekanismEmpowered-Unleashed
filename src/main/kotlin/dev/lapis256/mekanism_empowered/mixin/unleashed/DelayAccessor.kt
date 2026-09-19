package dev.lapis256.mekanism_empowered.mixin.unleashed

import mekanism.common.tile.machine.TileEntityDigitalMiner
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

/**
 * Accessor：暴露数字矿机的私有字段 `delay` 的写入口。
 *
 * 数字矿机在"负 tick"模式下需要直接改写延迟计数，
 * 而该字段在原版中是私有的，只能通过 Accessor 访问。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [TileEntityDigitalMiner::class], remap = false)
interface DelayAccessor {

    /** 写入数字矿机的私有字段 `delay`。 */
    @Accessor("delay")
    fun setDelay(delay: Int)
}
