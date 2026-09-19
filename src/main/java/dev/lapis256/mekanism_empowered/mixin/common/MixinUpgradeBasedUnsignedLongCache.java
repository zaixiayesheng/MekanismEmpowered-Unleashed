package dev.lapis256.mekanism_empowered.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplUpgradeBasedUnsignedLongCache;
import mekanism.common.attachments.component.UpgradeAware;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.LongSupplier;


@Mixin(targets = "mekanism.common.item.block.ItemBlockTooltip$UpgradeBasedUnsignedLongCache", remap = false)
public class MixinUpgradeBasedUnsignedLongCache {
    @Shadow
    private long value;

    @Unique
    @Final
    private final MixinImplUpgradeBasedUnsignedLongCache mekanismEmpowered$impl = new MixinImplUpgradeBasedUnsignedLongCache();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mekanismEmpowered$init(ItemStack stack, LongSupplier baseStorage, CallbackInfo ci, @Local UpgradeAware upgradeAware) {
        value = mekanismEmpowered$impl.modifyMaxEnergy(upgradeAware, value);
    }

    @ModifyReturnValue(method = "getAsLong", at = @At(value = "RETURN"))
    private long mekanismEmpowered$modifyMaxEnergy(long original, @Local UpgradeAware upgradeAware) {
        return value = mekanismEmpowered$impl.modifyMaxEnergy(upgradeAware, original);
    }
}
