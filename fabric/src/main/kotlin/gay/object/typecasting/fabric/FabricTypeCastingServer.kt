package gay.`object`.typecasting.fabric

import gay.`object`.typecasting.TypeCasting
import net.fabricmc.api.DedicatedServerModInitializer

object FabricTypeCastingServer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        TypeCasting.initServer()
    }
}
