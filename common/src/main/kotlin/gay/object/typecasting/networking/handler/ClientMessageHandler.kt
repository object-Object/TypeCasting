package gay.`object`.typecasting.networking.handler

import dev.architectury.networking.NetworkManager.PacketContext
import gay.`object`.typecasting.config.TypeCastingServerConfig
import gay.`object`.typecasting.networking.msg.*

fun TypeCastingMessageS2C.applyOnClient(ctx: PacketContext) = ctx.queue {
    when (this) {
        is MsgSyncConfigS2C -> {
            TypeCastingServerConfig.onSyncConfig(serverConfig)
        }

        // add more client-side message handlers here
    }
}
