package dev.lapis256.mekanism_empowered.common.attachments.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.lapis256.mekanism_empowered.common.tile.component.config.IPersistentInserterConfigInfo
import io.netty.buffer.ByteBuf
import mekanism.api.RelativeSide
import mekanism.api.SerializationConstants
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import java.util.*


data class AttachedInserterConfigInfo(override val config: MutableMap<RelativeSide, Boolean>) : IPersistentInserterConfigInfo {
    companion object {
        val CODEC: Codec<AttachedInserterConfigInfo> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(RelativeSide.CODEC, Codec.BOOL).fieldOf(SerializationConstants.SIDE).forGetter(AttachedInserterConfigInfo::config)
            ).apply(
                instance, ::AttachedInserterConfigInfo
            )
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, AttachedInserterConfigInfo> = StreamCodec.composite(
            ByteBufCodecs.map({ i -> EnumMap(RelativeSide::class.java) }, RelativeSide.STREAM_CODEC, ByteBufCodecs.BOOL),
            AttachedInserterConfigInfo::config,
            ::AttachedInserterConfigInfo
        )

        fun create(configInfo: IPersistentInserterConfigInfo) = AttachedInserterConfigInfo(configInfo.config.toMutableMap())
    }

    override fun isSideEnabled(relativeSide: RelativeSide) = config.getOrDefault(relativeSide, false)
}
