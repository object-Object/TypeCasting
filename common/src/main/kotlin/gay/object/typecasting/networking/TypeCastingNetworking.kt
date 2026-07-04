package gay.`object`.typecasting.networking

import dev.architectury.networking.NetworkChannel
import gay.`object`.typecasting.TypeCasting
import gay.`object`.typecasting.networking.msg.TypeCastingMessageCompanion

object TypeCastingNetworking {
    val CHANNEL: NetworkChannel = NetworkChannel.create(TypeCasting.id("networking_channel"))

    fun init() {
        for (subclass in TypeCastingMessageCompanion::class.sealedSubclasses) {
            subclass.objectInstance?.register(CHANNEL)
        }
    }
}
