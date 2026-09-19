package dev.lapis256.mekanism_empowered.mixin.unleashed

import dev.lapis256.mekanism_empowered.unleashed.UnleashedUtils
import mekanism.common.registries.MekanismArmorMaterials
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyArg

/**
 * Meka 装甲：附魔开关（配置项 `enchantableMekaGear`）。
 *
 * 装甲材质在注册表初始化阶段通过 `lambda$static$7` 构造，
 * 这里把传入 `ArmorMaterial` 构造器的附魔性能参数替换为
 * 30（开启时）或保持原值（关闭时）。
 *
 * 注意：注入目标 `lambda$static$7` 是编译期生成的 lambda 编号，
 * Mekanism 未来更新若改变该编号会导致此注入失效（仅影响
 * 附魔开关，不影响其他功能）——已在 MERGE_NOTES 中记录。
 *
 * 合并自 Mekanism Unleashed (WhitePhant0m)。
 */
@Mixin(value = [MekanismArmorMaterials::class])
abstract class MixinMekanismArmorMaterials {

    /*
     * 目标方法（lambda$static$7）是静态方法，因此 handler 必须是
     * 静态方法：放在 private companion 中并用 @JvmStatic 提升到
     * 外部类上。
     * （Mixin 禁止 mixin 类携带非私有静态字段，companion 必须 private。）
     */
    private companion object {

        /** 替换 ArmorMaterial 构造器的附魔性能参数。 */
        @JvmStatic
        @ModifyArg(
            method = ["lambda\$static\$7"],
            at = At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ArmorMaterial;<init>(Ljava/util/Map;ILnet/minecraft/core/Holder;Ljava/util/function/Supplier;Ljava/util/List;FF)V"
            )
        )
        private fun modifyEnchantValue(orig: Int): Int =
            if (UnleashedUtils.enchantableMekaGear()) 30 else orig
    }
}
