package dev.lapis256.mekanism_empowered.common.tile.component

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.common.attachments.component.AttachedInserterConfigInfo
import dev.lapis256.mekanism_empowered.common.init.MekEmpDataComponents
import dev.lapis256.mekanism_empowered.common.tile.component.config.InserterConfigInfo
import mekanism.api.RelativeSide
import mekanism.api.SerializationConstants
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.component.ITileComponent
import mekanism.common.util.EnumUtils
import mekanism.common.util.NBTUtils
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity


class TileComponentInserterConfig(val tile: TileEntityMekanism) : ITileComponent {
    val configInfo = InserterConfigInfo()

    init {
        tile.addComponent(this)
    }

    fun toggleSideConfig(relativeSide: RelativeSide) {
        configInfo.toggleSideConfig(relativeSide)
        tile.markForSave()
        tile.sendUpdatePacket()
    }

    fun isSideEnabled(relativeSide: RelativeSide) = configInfo.isSideEnabled(relativeSide)

    override fun getComponentKey() = MekEmpSerializationConstants.COMPONENT_INSERTER_CONFIG

    override fun applyImplicitComponents(input: BlockEntity.DataComponentInput) {
        val configInfo = input.get(MekEmpDataComponents.INSERTER_CONFIG) ?: return
        this.configInfo.config.putAll(configInfo.config)
        if (tile.hasLevel()) {
            tile.markForSave()
        }
    }

    override fun collectImplicitComponents(builder: DataComponentMap.Builder) {
        builder.set(MekEmpDataComponents.INSERTER_CONFIG, AttachedInserterConfigInfo.create(configInfo))
    }

    private fun readFromNBT(componentTag: CompoundTag) {
        if (componentTag.contains(SerializationConstants.CONFIG)) {
            componentTag
                .getByteArray(SerializationConstants.CONFIG)
                .forEachIndexed { i, byte -> configInfo.setSideConfig(RelativeSide.BY_ID.apply(i), byte == 1.toByte()) }
        }
    }

    override fun deserialize(componentTag: CompoundTag, provider: HolderLookup.Provider) {
        readFromNBT(componentTag)
    }

    override fun readFromUpdateTag(updateTag: CompoundTag) {
        NBTUtils.setCompoundIfPresent(updateTag, getComponentKey(), ::readFromNBT)
    }

    private fun writeToNBT(componentTag: CompoundTag) {
        componentTag.putByteArray(
            SerializationConstants.CONFIG,
            EnumUtils.SIDES.sortedBy(RelativeSide::ordinal).map { if (configInfo.isSideEnabled(it)) 1 else 0 }
        )
    }

    override fun serialize(provider: HolderLookup.Provider) = CompoundTag().also(::writeToNBT)

    override fun addToUpdateTag(updateTag: CompoundTag) {
        updateTag.put(getComponentKey(), CompoundTag().also(::writeToNBT))
    }
}
