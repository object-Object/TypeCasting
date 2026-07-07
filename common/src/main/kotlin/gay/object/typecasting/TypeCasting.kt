package gay.`object`.typecasting

import gay.`object`.typecasting.config.TypeCastingServerConfig
import gay.`object`.typecasting.networking.TypeCastingNetworking
import gay.`object`.typecasting.registry.TypeCastingActions
import gay.`object`.typecasting.registry.TypeCastingIotaTypes
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TypeCasting {
    const val MODID = "typecasting"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmStatic
    fun id(path: String) = ResourceLocation(MODID, path)

    fun init() {
        TypeCastingServerConfig.init()
        initRegistries(
            TypeCastingActions,
            TypeCastingIotaTypes,
        )
        TypeCastingNetworking.init()
    }

    fun initServer() {
        TypeCastingServerConfig.initServer()
    }
}
