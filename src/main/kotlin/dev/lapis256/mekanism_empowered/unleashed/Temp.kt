package dev.lapis256.mekanism_empowered.unleashed

import java.util.function.BiConsumer

/**
 * Mixin 辅助类：存放 Mixin 类本身无法持有的状态。
 *
 * Mixin 类不允许声明（非私有的）静态字段，而注入回调需要跨调用共享
 * "是否正在注入中"的标志位，所以把这些状态集中放到这个独立对象里。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
object Temp {

    /**
     * 重入保护标志（线程级）。
     *
     * 机器在"负 tick"模式下会递归调用自身的 onUpdateServer 来处理
     * 一 tick 内的多次操作；该标志防止递归调用再次触发注入逻辑，
     * 造成无限递归。
     */
    @JvmField
    val isInjecting: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

    /**
     * 核心注入逻辑：
     *
     * [reqTime] 为负时（表示"每操作不足 1 tick，一 tick 内应执行多次"），
     * 循环调用 [process]（即机器的 onUpdateServer）补齐剩余次数。
     * 每次补齐的调用结束时会再次经过注入点，靠 [isInjecting] 拦截，
     * 保证补次循环只由最外层驱动、不层层嵌套。
     */
    @JvmField
    val inject: BiConsumer<Int, Runnable> = BiConsumer { reqTime, process ->
        if (!isInjecting.get()) {
            isInjecting.set(true)
            for (i in reqTime until 0) process.run()
            isInjecting.set(false)
        }
    }
}
