package dev.lapis256.mekanism_empowered.common

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.config.MekEmpConfig
import dev.lapis256.mekanism_empowered.common.init.MekEmpCreativeTab
import dev.lapis256.mekanism_empowered.common.init.MekEmpDataComponents
import dev.lapis256.mekanism_empowered.common.init.MekEmpItems
import dev.lapis256.mekanism_empowered.common.init.MekEmpUpgrades
import dev.lapis256.mekanism_empowered.common.network.MekEmpPacketHandler
import dev.lapis256.mekanism_empowered.core.common.init.GlobalLootModifierSerializers
import dev.lapis256.mekanism_empowered.core.common.init.LootConditionTypes
import dev.lapis256.mekanism_empowered.core.common.util.AdditionalUpgradeUtil
import dev.lapis256.mekanism_empowered.integration.Integrations
import mekanism.common.lib.Version
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Mod(MekanismEmpoweredAPI.MOD_ID)
class MekanismEmpowered(modContainer: ModContainer, modEventBus: IEventBus) {
    init {
        MekEmpConfig.registerConfigs(modContainer)

        MekEmpUpgrades.registerUpgradeInfo()
        MekEmpUpgrades.registerSupportedUpgrades()

        modEventBus.addListener(MekEmpConfig::onConfigLoad)

        MekEmpItems.REGISTRY.register(modEventBus)
        MekEmpCreativeTab.REGISTRY.register(modEventBus)
        MekEmpDataComponents.REGISTRY.register(modEventBus)

        // === 以下三行合并自 Mekanism: Empowered Core（原独立 @Mod 入口的初始化） ===
        // 战利品修饰器序列化器 + 战利品条件类型注册（原 core 的注册内容）
        GlobalLootModifierSerializers.REGISTRY.register(modEventBus)
        LootConditionTypes.REGISTRY.register(modEventBus)
        // 公共设置阶段：应用各机器延迟注册的"支持升级"清单
        modEventBus.addListener<FMLCommonSetupEvent> {
            AdditionalUpgradeUtil.applyDeferredSupportedUpgrades()
        }

        Integrations.initCommon(modEventBus)
    }

    val versionNumber = Version(modContainer)
    val packetHandler = MekEmpPacketHandler(modEventBus, versionNumber)

    companion object {
        @JvmField
        val LOGGER: Logger = LoggerFactory.getLogger(MekanismEmpoweredAPI.MOD_ID)
    }
}
