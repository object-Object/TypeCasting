@file:JvmName("TypeCastingAbstractions")

package gay.`object`.typecasting

import dev.architectury.injectables.annotations.ExpectPlatform
import gay.`object`.typecasting.registry.TypeCastingRegistrar

fun initRegistries(vararg registries: TypeCastingRegistrar<*>) {
    for (registry in registries) {
        initRegistry(registry)
    }
}

@ExpectPlatform
fun <T : Any> initRegistry(registrar: TypeCastingRegistrar<T>) {
    throw AssertionError()
}
