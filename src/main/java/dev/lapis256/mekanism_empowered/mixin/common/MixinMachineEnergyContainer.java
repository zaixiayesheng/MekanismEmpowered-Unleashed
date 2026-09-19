package dev.lapis256.mekanism_empowered.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplMachineEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentUpgrade;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = MachineEnergyContainer.class, remap = false)
public class MixinMachineEnergyContainer<TILE extends TileEntityMekanism> {
    @Shadow
    @Final
    protected TILE tile;

    @ModifyExpressionValue(method = "updateMaxEnergy", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/base/TileEntityMekanism;supportsUpgrade(Lmekanism/api/Upgrade;)Z"))
    private boolean mekanismEmpowered$modifyUpdateMaxEnergyTarget(boolean original) {
        return MixinImplMachineEnergyContainer.modifyUpdateMaxEnergyTarget(tile, original);
    }

    @ModifyExpressionValue(method = "updateEnergyPerTick", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/component/TileComponentUpgrade;supports(Lmekanism/api/Upgrade;)Z", ordinal = 0))
    private boolean mekanismEmpowered$modifyUpdateEnergyPerTickTarget(boolean original, @Local TileComponentUpgrade component) {
        return MixinImplMachineEnergyContainer.modifyUpdateEnergyPerTickTarget(component, original);
    }
}
