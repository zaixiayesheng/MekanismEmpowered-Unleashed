package dev.lapis256.mekanism_empowered.mixin.common.tile.component;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileComponentEjector;
import dev.lapis256.mekanism_empowered.unleashed.UselessModCompat;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * 弹出速率与容量。
 *
 * 优先级 2000：只影响下面那个 RETURN 注入的顺序 —— 无用之物（UselessMod）
 * 也在 `outputItems` 的 RETURN 上写 `tickDelay`，同一个返回点上的注入按
 * 「优先级升序应用」的顺序执行，数值越大越晚执行，我们才能最后一个说话。
 */
@Mixin(value = TileComponentEjector.class, remap = false, priority = 2000)
public class MixinTileComponentEjector {
    @Shadow
    @Final
    private TileEntityMekanism tile;

    @Shadow
    private int tickDelay;

    @ModifyExpressionValue(method = "outputItems", at = @At(value = "CONSTANT", args = "intValue=10"))
    private int mekanismEmpowered$modifyTickDelay(int original) {
        return MixinImplTileComponentEjector.modifyTickDelay(tile, original);
    }

    @ModifyArg(method = "eject", at = @At(value = "INVOKE", target = "Lmekanism/common/util/FluidUtils;emit(Ljava/util/Collection;Lmekanism/api/fluid/IExtendedFluidTank;I)V"), index = 2)
    private int mekanismEmpowered$modifyFluidEjectRate(int original) {
        return MixinImplTileComponentEjector.modifyFluidEjectRate(tile, original);
    }

    @ModifyArg(method = "eject", at = @At(value = "INVOKE", target = "Lmekanism/common/util/ChemicalUtil;emit(Ljava/util/Collection;Lmekanism/api/chemical/IChemicalTank;J)V"), index = 2)
    private long mekanismEmpowered$modifyChemicalEjectRate(long original) {
        return MixinImplTileComponentEjector.modifyChemicalEjectRate(tile, original);
    }

    /**
     * 与无用之物（UselessMod）并存用：它在 `outputItems` 返回时把 `tickDelay`
     * 直接写成 0（每 tick 都弹出），会盖掉本模组的 Fast Item Eject 升级。
     * 我们在同一个返回点、以更晚执行的顺序再写回本模组算出的值。
     *
     * 只有在装了无用之物时才补写，平时不插手原版行为。
     */
    @Inject(method = "outputItems", at = @At("RETURN"))
    private void mekanismEmpowered$restoreTickDelay(CallbackInfo ci) {
        if (UselessModCompat.LOADED) {
            UselessModCompat.logOnce();
            // 原版 outputItems 里写的就是 MekanismUtils.TICKS_PER_HALF_SECOND，
            // 这里以同一个初值重算一次本模组的值。
            this.tickDelay = MixinImplTileComponentEjector.modifyTickDelay(tile, MekanismUtils.TICKS_PER_HALF_SECOND);
        }
    }
}
