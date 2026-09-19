package dev.lapis256.mekanism_empowered.mixin.unleashed

import com.llamalad7.mixinextras.injector.ModifyReturnValue
import dev.lapis256.mekanism_empowered.unleashed.UnleashedUtils
import mekanism.common.item.gear.ItemMekaTool
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At

/**
 * Meka 工具：附魔开关（配置项 `enchantableMekaGear`）。
 *
 * - [isEnchantable]：把原版返回值替换为配置值——开启后 Meka 工具
 *   可被附魔（需自行把物品加入可附魔标签）；
 * - [getEnchantmentValue]：向目标类补充一个覆写方法，控制附魔台
 *   显示的附魔性能（30 = 与金质装备同级）。
 *
 * 配置未加载时（注册表阶段）由 [UnleashedUtils.enchantableMekaGear]
 * 安全回退为 false，保持原版行为。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [ItemMekaTool::class])
abstract class MixinItemMekaTool {

    /** 覆写可附魔判定：直接返回配置开关。 */
    @ModifyReturnValue(method = ["isEnchantable"], at = [At("RETURN")])
    fun isEnchantable(original: Boolean): Boolean = UnleashedUtils.enchantableMekaGear()

    /** 附魔性能：开启时 30（金质水准），否则 0（不可附魔）。 */
    fun getEnchantmentValue(): Int = if (UnleashedUtils.enchantableMekaGear()) 30 else 0
}
