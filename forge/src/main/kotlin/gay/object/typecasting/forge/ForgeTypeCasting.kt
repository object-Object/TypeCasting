package gay.`object`.typecasting.forge

import dev.architectury.platform.forge.EventBuses
import gay.`object`.typecasting.TypeCasting
import gay.`object`.typecasting.forge.datagen.ForgeTypeCastingDatagen
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(TypeCasting.MODID)
class ForgeTypeCasting {
    init {
        MOD_BUS.apply {
            EventBuses.registerModEventBus(TypeCasting.MODID, this)
            addListener(ForgeTypeCastingClient::init)
            addListener(ForgeTypeCastingDatagen::init)
            addListener(ForgeTypeCastingServer::init)
        }
        TypeCasting.init()
    }
}
