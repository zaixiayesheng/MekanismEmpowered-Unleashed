package dev.lapis256.mekanism_empowered.mixin.common.tile.component;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileComponentEjector;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentEjector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(value = TileComponentEjector.class, remap = false)
public class MixinTileComponentEjector {
    @Shadow
    @Final
    private TileEntityMekanism tile;

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
}
