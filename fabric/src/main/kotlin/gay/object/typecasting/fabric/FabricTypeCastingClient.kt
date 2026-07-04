package gay.`object`.typecasting.fabric

import gay.`object`.typecasting.TypeCastingClient
import net.fabricmc.api.ClientModInitializer

object FabricTypeCastingClient : ClientModInitializer {
    override fun onInitializeClient() {
        TypeCastingClient.init()
    }
}
