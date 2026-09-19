package dev.lapis256.mekanism_empowered.mixin.common.tile.component;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileComponentUpgrade;
import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.Map;


@Mixin(value = TileComponentUpgrade.class, remap = false)
public class MixinTileComponentUpgrade {
    @Shadow
    @Final
    private Map<Upgrade, Integer> upgrades;

    @Shadow
    @Final
    private TileEntityMekanism tile;

    @Inject(method = "addToUpdateTag", at = @At("TAIL"))
    private void mekanismEmpowered$appendClientSyncData(CallbackInfo ci, @Local(argsOnly = true) CompoundTag updateTag) {
        MixinImplTileComponentUpgrade.appendClientSyncData((TileComponentUpgrade) (Object) this, updateTag);
    }

    @Inject(method = "readFromUpdateTag", at = @At("TAIL"))
    private void mekanismEmpowered$readAppendedClientSyncData(CallbackInfo ci, @Local(argsOnly = true) CompoundTag updateTag) {
        MixinImplTileComponentUpgrade.readAppendedClientSyncData((TileComponentUpgrade) (Object) this, (EnumMap<Upgrade, Integer>) upgrades, updateTag);
    }

    @Definition(id = "upgrade", local = @Local(type = Upgrade.class))
    @Definition(id = "MUFFLING", field = "Lmekanism/api/Upgrade;MUFFLING:Lmekanism/api/Upgrade;")
    @Expression("upgrade == MUFFLING")
    @ModifyExpressionValue(method = "addUpgrades(Lmekanism/api/Upgrade;II)I", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean mekanismEmpowered$modifyClientSyncTarget(boolean original, @Local(argsOnly = true) Upgrade upgrade) {
        return original || MixinImplTileComponentUpgrade.isAppendedClientSyncTarget(upgrade);
    }

    /**
     * 【顺序 bug 修复】原版 recalculateUpgrades 只认 SPEED/ENERGY 两种升级：
     * 插入强化升级（EMPOWERED_SPEED/EMPOWERED_ENERGY）时原版无动作，机器里
     * 缓存的每 tick 耗电/储能上限就不会刷新；在 MekExt 等覆写了
     * recalculateUpgrades 的附属机器上，我们原有按表达式改判的分流也会失效。
     * 表现为"强化升级必须先放、速度后放才生效"，重插原版升级/挖掉重放才恢复。
     * 这里不管插的是什么升级，只要数量真的变了，就把 SPEED 与 ENERGY 两条
     * 原版刷新路径都跑一遍（虚方法分发，附属机器的覆写也会正常执行），
     * 缓存永远是最新，插入顺序不再影响效果。
     */
    @Inject(method = "addUpgrades(Lmekanism/api/Upgrade;II)I", at = @At("RETURN"))
    private void mekanismEmpoweredUnleashed$refreshEnergyAfterAdd(Upgrade upgrade, int installed, int maxAvailable, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() > 0) {
            tile.recalculateUpgrades(Upgrade.SPEED);
            tile.recalculateUpgrades(Upgrade.ENERGY);
        }
    }

    /**
     * 卸升级同理：任何类型被卸掉后都强制刷新一遍能量相关缓存。
     */
    @Inject(method = "removeUpgrade", at = @At("RETURN"))
    private void mekanismEmpoweredUnleashed$refreshEnergyAfterRemove(Upgrade upgrade, boolean removeAll, CallbackInfo ci) {
        tile.recalculateUpgrades(Upgrade.SPEED);
        tile.recalculateUpgrades(Upgrade.ENERGY);
    }
}
