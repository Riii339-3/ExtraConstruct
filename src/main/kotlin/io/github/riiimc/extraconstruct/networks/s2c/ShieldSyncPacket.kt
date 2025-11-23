package io.github.riiimc.extraconstruct.networks.s2c

import com.mega.uom.common.capability.ModCapabilities
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.LivingEntity
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

data class ShieldSyncPacket(
    val entityId: Int,
    val data: Float
) {
    companion object {
        fun encode(msg: ShieldSyncPacket, buf: FriendlyByteBuf) {
            buf.writeInt(msg.entityId)
            buf.writeFloat(msg.data)
        }

        fun decode(buf: FriendlyByteBuf): ShieldSyncPacket {
            return ShieldSyncPacket(buf.readInt(), buf.readFloat())
        }

        fun handle(msg: ShieldSyncPacket, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.enqueueWork {
                val level = Minecraft.getInstance().level ?: return@enqueueWork
                val entity = level.getEntity(msg.entityId) as? LivingEntity ?: return@enqueueWork

                val cap = ModCapabilities.getCapability(entity, ModCapabilities.FE_SHIELD_EC)
                cap?.setDamageResistance(msg.data)
            }
            context.packetHandled = true
        }
    }
}
