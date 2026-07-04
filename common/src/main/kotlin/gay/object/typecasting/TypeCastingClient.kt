package gay.`object`.typecasting

import gay.`object`.typecasting.config.TypeCastingClientConfig
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screens.Screen

object TypeCastingClient {
    fun init() {
        TypeCastingClientConfig.init()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(TypeCastingClientConfig.GlobalConfig::class.java, parent).get()
    }
}
