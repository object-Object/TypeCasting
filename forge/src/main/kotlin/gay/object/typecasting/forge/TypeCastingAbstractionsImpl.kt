@file:JvmName("TypeCastingAbstractionsImpl")

package gay.`object`.typecasting.forge

import gay.`object`.typecasting.registry.TypeCastingRegistrar
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

fun <T : Any> initRegistry(registrar: TypeCastingRegistrar<T>) {
    MOD_BUS.addListener { event: RegisterEvent ->
        event.register(registrar.registryKey) { helper ->
            registrar.init(helper::register)
        }
    }
}
