package dev.lapis256.mekanism_empowered.mixin.common.tile.qio;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplTileEntityQIO;
import mekanism.api.Upgrade;
import mekanism.common.tile.qio.TileEntityQIOComponent;
import mekanism.common.tile.qio.TileEntityQIOFilterHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = TileEntityQIOFilterHandler.class, remap = false)
public abstract class MixinTileEntityQIOFilterHandler extends TileEntityQIOComponent {
    public MixinTileEntityQIOFilterHandler(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Definition(id = "speedUpgrades", local = @Local(type = int.class, ordinal = 0))
    @Expression("? + ? * speedUpgrades")
    @ModifyExpressionValue(method = "recalculateUpgrades", at = @At("MIXINEXTRAS:EXPRESSION"))
    protected int mekanismEmpowered$modifyMaxTransitCount(int original, @Local(name = "speedUpgrades") int speedUpgrades) {
        return MixinImplTileEntityQIO.modifyMaxTransitCount(this, original, speedUpgrades);
    }

    @ModifyExpressionValue(method = "recalculateUpgrades", at = @At(value = "INVOKE", target = "Ljava/lang/Math;round(F)I"))
    protected int mekanismEmpowered$modifyMaxTransitTypes(int original, @Local int speedUpgrades) {
        return MixinImplTileEntityQIO.modifyMaxTransitTypes(this, original, speedUpgrades);
    }

    @Definition(id = "upgrade", local = @Local(type = Upgrade.class))
    @Definition(id = "SPEED", field = "Lmekanism/api/Upgrade;SPEED:Lmekanism/api/Upgrade;")
    @Expression("upgrade == SPEED")
    @ModifyExpressionValue(method = "recalculateUpgrades", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean mekanismEmpowered$modifyRecalculationTarget(boolean original, @Local(argsOnly = true) Upgrade upgrade) {
        return original || MixinImplTileEntityQIO.isAdditionalRecalculationTarget(upgrade);
    }
}
