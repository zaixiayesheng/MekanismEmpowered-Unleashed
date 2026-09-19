package dev.lapis256.mekanism_empowered.mixin.common.tile.component;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileComponentUpgrade;
import mekanism.api.Upgrade;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.Map;


@Mixin(value = TileComponentUpgrade.class, remap = false)
public class MixinTileComponentUpgrade {
    @Shadow
    @Final
    private Map<Upgrade, Integer> upgrades;

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
}
