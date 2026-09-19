package dev.lapis256.mekanism_empowered.mixin.common.tile;

import dev.lapis256.mekanism_empowered.common.tile.component.TileComponentInserter;
import dev.lapis256.mekanism_empowered.common.tile.component.TileComponentInserterConfig;
import dev.lapis256.mekanism_empowered.common.tile.interfaces.ISideInserterConfiguration;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = TileEntityConfigurableMachine.class, remap = false)
public abstract class MixinTileEntityConfigurableMachine extends TileEntityMekanism implements ISideInserterConfiguration {
    public MixinTileEntityConfigurableMachine(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Unique
    private TileComponentInserterConfig mekanismEmpowered$insertConfig;

    @Unique
    private TileComponentInserter mekanismEmpowered$inserter;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void mekanismEmpowered$initInsertConfig(CallbackInfo ci) {
        this.mekanismEmpowered$insertConfig = new TileComponentInserterConfig(this);
        this.mekanismEmpowered$inserter = new TileComponentInserter((TileEntityConfigurableMachine) (Object) this);
    }

    @Override
    public @NotNull TileComponentInserterConfig mekanismEmpowered$getInserterConfig() {
        return this.mekanismEmpowered$insertConfig;
    }

    @Override
    public @Nullable TileComponentInserter mekanismEmpowered$getInserter() {
        return this.mekanismEmpowered$inserter;
    }

    @Inject(method = "onUpdateServer", at = @At(value = "TAIL"))
    private void mekanismEmpowered$injectedOnUpdateServer(CallbackInfoReturnable<Boolean> cir) {
        this.mekanismEmpowered$inserter.tickServer();
    }
}
