package dev.lapis256.mekanism_empowered.mixin.common.tile;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade;
import dev.lapis256.mekanism_empowered.mixin_impl.MixinImplModifyRecalculationTarget;
import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.machine.TileEntityChemicalInfuser;
import mekanism.common.tile.machine.TileEntityChemicalWasher;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.tile.machine.TileEntityElectrolyticSeparator;
import mekanism.common.tile.machine.TileEntityFormulaicAssemblicator;
import mekanism.common.tile.machine.TileEntityIsotopicCentrifuge;
import mekanism.common.tile.machine.TileEntityPigmentMixer;
import mekanism.common.tile.machine.TileEntityRotaryCondensentrator;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.tile.qio.TileEntityQIOFilterHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


class MixinModifyRecalculationTarget {
    @Pseudo
    @Mixin(
        value = {
            TileEntityMekanism.class,
            TileEntityFactory.class,
            TileEntityChemicalInfuser.class,
            TileEntityChemicalWasher.class,
            TileEntityElectrolyticSeparator.class,
            TileEntityIsotopicCentrifuge.class,
            TileEntityPigmentMixer.class,
            TileEntityRotaryCondensentrator.class,
            TileEntityFormulaicAssemblicator.class,
            TileEntityQIOFilterHandler.class,
            TileEntityProgressMachine.class,
            TileEntityDigitalMiner.class

            // No current plans to increase Chemical usage.
//            TileEntityItemStackChemicalToItemStackFactory.class,
//            TileEntityChemicalDissolutionChamber.class,
//            TileEntityAdvancedElectricMachine.class,

        },
        targets = {
            "com.jerry.mekextras.common.tile.factory.TileEntityExtraFactory",
            "com.jerry.mekextras.common.tile.factory.TileEntityExtraItemStackChemicalToItemStackFactory",
            "com.jerry.mekextras.common.tile.machine.TileEntityAdvancedElectricPump",
            "com.jerry.mekextras.common.integration.mekaf.tile.factory.base.TileEntityExtraAdvancedFactoryBase",
            "com.jerry.mekextras.common.integration.mekmm.tile.factory.TileEntityExtraMoreMachineFactory",
            "com.jerry.mekextras.common.integration.mekmm.tile.factory.TileEntityExtraPlantingFactory",
            "com.jerry.mekextras.common.integration.mekaf.tile.factory.TileEntityExtraCentrifugingFactory",
            "com.jerry.mekextras.common.integration.mekaf.tile.factory.TileEntityExtraDissolvingFactory",
            "com.jerry.mekextras.common.integration.mekaf.tile.factory.TileEntityExtraWashingFactory",

            "com.jerry.mekaf.common.tile.factory.base.TileEntityAdvancedFactoryBase",
            "com.jerry.mekaf.common.tile.factory.base.TileEntityChemicalToChemicalFactory",
            "com.jerry.mekaf.common.tile.factory.TileEntityDissolvingFactory",
            "com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator",
            "com.jerry.meklm.common.tile.machine.TileEntityLargePigmentMixer",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator",
            "com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator",

            "io.github.masyumero.emextras.common.tile.factory.TileEntityEMExtraFactory",
            "io.github.masyumero.emextras.common.tile.factory.TileEntityEMExtraItemStackChemicalToItemStackFactory",
            "io.github.masyumero.emextras.common.integration.mekaf.tile.factory.TileEntityEMExtraAdvancedBase",
            "io.github.masyumero.emextras.common.integration.mekmm.tile.factory.TileEntityEMExtraMoreMachineFactory",

            "fixdol.mekanismelements.common.tile.prefab.MSTileEntityProgressMachine"
        },
        remap = false
    )
    public static class Speed {
        @Definition(id = "upgrade", local = @Local(type = Upgrade.class))
        @Definition(id = "SPEED", field = "Lmekanism/api/Upgrade;SPEED:Lmekanism/api/Upgrade;")
        @Expression("upgrade == SPEED")
        @ModifyExpressionValue(method = "recalculateUpgrades", at = @At("MIXINEXTRAS:EXPRESSION"))
        private boolean mekanismEmpowered$modifyRecalculationTarget(boolean original, @Local(argsOnly = true) Upgrade upgrade) {
            return MixinImplModifyRecalculationTarget.modifySpeed(original, upgrade);
        }
    }

    /**
     * Re-runs the energy recalculation path with the vanilla ENERGY upgrade when
     * the EMPOWERED_ENERGY upgrade was passed in. Implemented as a RETURN injection
     * with virtual re-dispatch instead of bytecode-pattern matching: Mekanism
     * 10.7.23+ (and its performance forks) rewrite {@code recalculateUpgrades} in
     * ways that break the previous {@code upgrade == ENERGY} expression matcher.
     * <p>
     * 【合并改动】原实现用 mixinextras 表达式匹配 `upgrade == ENERGY`，
     * 在 Mekanism 10.7.23 / 社区 fork 上该字节码模式不存在导致 CRITICAL
     * 注入失败（@Pseudo 也无法豁免注入失败）。改为在 RETURN 处用虚方法
     * 重新分发 recalculateUpgrades(ENERGY)，与具体字节码无关、永不失效。
     */
    @Pseudo
    @Mixin(value = TileEntityMekanism.class, remap = false)
    public static class Energy {
        @Inject(method = "recalculateUpgrades", at = @At("RETURN"))
        private void mekanismEmpowered$modifyRecalculationTarget(Upgrade upgrade, CallbackInfo ci) {
            if (upgrade == MekEmpUpgrade.getEMPOWERED_ENERGY()) {
                ((TileEntityMekanism) (Object) this).recalculateUpgrades(Upgrade.ENERGY);
            }
        }
    }
}
