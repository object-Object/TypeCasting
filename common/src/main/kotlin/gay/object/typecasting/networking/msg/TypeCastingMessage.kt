package gay.`object`.typecasting.networking.msg

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager.PacketContext
import gay.`object`.typecasting.TypeCasting
import gay.`object`.typecasting.networking.TypeCastingNetworking
import gay.`object`.typecasting.networking.handler.applyOnClient
import gay.`object`.typecasting.networking.handler.applyOnServer
import net.fabricmc.api.EnvType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import java.util.function.Supplier

sealed interface TypeCastingMessage

sealed interface TypeCastingMessageC2S : TypeCastingMessage {
    fun sendToServer() {
        TypeCastingNetworking.CHANNEL.sendToServer(this)
    }
}

sealed interface TypeCastingMessageS2C : TypeCastingMessage {
    fun sendToPlayer(player: ServerPlayer) {
        TypeCastingNetworking.CHANNEL.sendToPlayer(player, this)
    }

    fun sendToPlayers(players: Iterable<ServerPlayer>) {
        TypeCastingNetworking.CHANNEL.sendToPlayers(players, this)
    }
}

sealed interface TypeCastingMessageCompanion<T : TypeCastingMessage> {
    val type: Class<T>

    fun decode(buf: FriendlyByteBuf): T

    fun T.encode(buf: FriendlyByteBuf)

    fun apply(msg: T, supplier: Supplier<PacketContext>) {
        val ctx = supplier.get()
        when (ctx.env) {
            EnvType.SERVER, null -> {
                TypeCasting.LOGGER.debug("Server received packet from {}: {}", ctx.player.name.string, this)
                when (msg) {
                    is TypeCastingMessageC2S -> msg.applyOnServer(ctx)
                    else -> TypeCasting.LOGGER.warn("Message not handled on server: {}", msg::class)
                }
            }
            EnvType.CLIENT -> {
                TypeCasting.LOGGER.debug("Client received packet: {}", this)
                when (msg) {
                    is TypeCastingMessageS2C -> msg.applyOnClient(ctx)
                    else -> TypeCasting.LOGGER.warn("Message not handled on client: {}", msg::class)
                }
            }
        }
    }

    fun register(channel: NetworkChannel) {
        channel.register(type, { msg, buf -> msg.encode(buf) }, ::decode, ::apply)
    }
}
