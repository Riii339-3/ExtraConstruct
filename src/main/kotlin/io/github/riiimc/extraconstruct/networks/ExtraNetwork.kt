package io.github.riiimc.extraconstruct.networks

import io.github.riiimc.extraconstruct.ExtraConstruct
import io.github.riiimc.extraconstruct.networks.s2c.ShieldSyncPacket
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

object ExtraNetwork {
    private const val PROTOCOL = "1"
    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(ExtraConstruct.MODID, "main"),
        { PROTOCOL }, { it == PROTOCOL }, { it == PROTOCOL }
    )

    private var id = 0

    fun register() {
        CHANNEL.registerMessage(
            id++,
            ShieldSyncPacket::class.java,
            ShieldSyncPacket::encode,
            ShieldSyncPacket::decode,
            ShieldSyncPacket::handle
        )
    }
}
