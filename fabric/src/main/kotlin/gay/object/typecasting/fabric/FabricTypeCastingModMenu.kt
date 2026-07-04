package gay.`object`.typecasting.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import gay.`object`.typecasting.TypeCastingClient

object FabricTypeCastingModMenu : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory(TypeCastingClient::getConfigScreen)
}
