package dev.lapis256.mekanism_empowered.common.init

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import mekanism.common.Mekanism
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister


object MekEmpCreativeTab {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MekanismEmpoweredAPI.MOD_ID)

    init {
        REGISTRY.register("main") { _ ->
            CreativeModeTab.builder()
                .title(Component.literal(MekanismEmpoweredAPI.MOD_NAME))
                .icon { ItemStack(MekEmpItems.EMPOWERED_SPEED.get()) }
                .displayItems { _, output ->
                    MekEmpItems.REGISTRY.entries.map(DeferredHolder<Item, out Item>::get).forEach(output::accept)
                }
                .withTabsBefore(Mekanism.rl(Mekanism.MODID))
                .build()
        }
    }
}
