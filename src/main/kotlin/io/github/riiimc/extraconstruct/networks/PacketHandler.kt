package io.github.riiimc.extraconstruct.networks

import com.mega.uom.ModSource
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

class PacketHandler {
    private val PROTOCOL_VERSION: String = "1"
    private var INSTANCE: SimpleChannel? = NetworkRegistry.newSimpleChannel(
        ResourceLocation(ModSource.MODID, "fantasy_ending_packet"),
        { PROTOCOL_VERSION },
        { s: String? -> true },
        { s: String? -> true });
    private var id: Int = 0

}