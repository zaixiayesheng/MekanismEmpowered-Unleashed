package dev.zaixiayesheng.mekanism_empowered_compat;

import net.neoforged.fml.common.Mod;


/**
 * 内嵌兼容子模组（随主模组 jar 以 jar-in-jar 方式携带）。
 *
 * 唯一的用途：向 FML 注册旧 mod id "mekanism_empowered"，让 MekaJade Upgrades
 * 这类写死检查旧 id 的附属继续认出强化升级并显示正确图标。
 *
 * 本类不初始化任何内容：图标、物品、升级注册等实际功能全部来自
 * 主模组 mekanism_empowered_unleashed 里的原有类。
 */
@Mod("mekanism_empowered")
public class MekanismEmpoweredCompat {
}
