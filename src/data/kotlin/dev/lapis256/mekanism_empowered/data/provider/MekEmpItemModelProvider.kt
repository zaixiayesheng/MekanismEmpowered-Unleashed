package dev.lapis256.mekanism_empowered.data.provider

import dev.lapis256.mekanism_empowered.api.MekanismEmpoweredAPI
import dev.lapis256.mekanism_empowered.common.init.MekEmpItems
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredHolder


class MekEmpItemModelProvider(output: PackOutput, helper: ExistingFileHelper) : ItemModelProvider(output, MekanismEmpoweredAPI.MOD_ID, helper) {
    private val parent = mcLoc("item/generated")

    override fun registerModels() {
        MekEmpItems.REGISTRY.entries.forEach(::add)
    }

    private fun add(item: DeferredHolder<Item, out Item>) {
        withExistingParent(item.id.path, parent)
            .texture("layer0", MekanismEmpoweredAPI.rl("item/${item.id.path}"))
    }
}
