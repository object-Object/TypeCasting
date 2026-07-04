package gay.`object`.typecasting.fabric

import gay.`object`.typecasting.TypeCasting
import net.fabricmc.api.ModInitializer

object FabricTypeCasting : ModInitializer {
    override fun onInitialize() {
        TypeCasting.init()
    }
}
