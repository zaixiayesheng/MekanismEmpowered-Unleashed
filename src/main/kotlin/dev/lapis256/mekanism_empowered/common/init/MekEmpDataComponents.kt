package dev.lapis256.mekanism_empowered.common.init

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.attachments.component.AttachedInserterConfigInfo
import mekanism.common.registration.MekanismDeferredHolder
import mekanism.common.registration.impl.DataComponentDeferredRegister
import net.minecraft.core.component.DataComponentType


object MekEmpDataComponents {
    val REGISTRY = DataComponentDeferredRegister(MekanismEmpoweredAPI.MOD_ID)

    @JvmField
    val INSERTER_CONFIG: MekanismDeferredHolder<DataComponentType<*>, DataComponentType<AttachedInserterConfigInfo>> =
        REGISTRY.simple(
            "inserter_config"
        ) { builder ->
            builder.persistent(AttachedInserterConfigInfo.CODEC)
                .networkSynchronized(AttachedInserterConfigInfo.STREAM_CODEC)
                .cacheEncoding()
        }
}
