@file:JvmName("TypeCastingAbstractionsImpl")

package gay.`object`.typecasting.fabric

import gay.`object`.typecasting.registry.TypeCastingRegistrar
import net.minecraft.core.Registry

fun <T : Any> initRegistry(registrar: TypeCastingRegistrar<T>) {
    val registry = registrar.registry
    registrar.init { id, value -> Registry.register(registry, id, value) }
}
