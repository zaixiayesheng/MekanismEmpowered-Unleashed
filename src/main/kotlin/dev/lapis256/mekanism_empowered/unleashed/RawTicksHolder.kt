package dev.lapis256.mekanism_empowered.unleashed


/**
 * 由泵/流体装填器/公式装配器/数字矿机四台特殊机器的 Mixin 实现。
 *
 * 原版 [mekanism.common.util.MekanismUtils.getTicks] 会把"负 tick 编码值"
 * 钳制到 >= 1，导致机器的 ticksRequired/delay 字段永远拿不到负值。
 * 因此 [MixinMekanismUtils] 在钳制发生前，把原始编码值通过本接口
 * 暂存到方块实体上；机器各自的补次回调再把它换算成"每 tick 操作次数"。
 *
 * 同时这也是与无用之物（UselessMod）并存的关键：它同类补次回调读取的是
 * 机器字段（恒 >= 1），负值只存在于我们的暂存值里，所以它的补次循环
 * 永远空转，我们无需判断对方是否安装。
 */
interface RawTicksHolder {
    fun getRawTicks(): Int

    fun setRawTicks(rawTicks: Int)
}
