package gay.`object`.typecasting.forge

import gay.`object`.typecasting.TypeCasting
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent

object ForgeTypeCastingServer {
    @Suppress("UNUSED_PARAMETER")
    fun init(event: FMLDedicatedServerSetupEvent) {
        TypeCasting.initServer()
    }
}
