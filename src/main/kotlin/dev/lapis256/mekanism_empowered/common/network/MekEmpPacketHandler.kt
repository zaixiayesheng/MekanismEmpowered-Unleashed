package dev.lapis256.mekanism_empowered.common.network

import dev.lapis256.mekanism_empowered.common.network.to_server.configuration_update.PacketSideInserterData
import mekanism.common.lib.Version
import mekanism.common.network.BasePacketHandler
import net.neoforged.bus.api.IEventBus


class MekEmpPacketHandler(modEventBus: IEventBus, version: Version) : BasePacketHandler(modEventBus, version) {
    override fun registerClientToServer(registrar: PacketRegistrar) {
        registrar.play(PacketSideInserterData.TYPE, PacketSideInserterData.STREAM_CODEC)
    }

    override fun registerServerToClient(registrar: PacketRegistrar) {
    }
}
