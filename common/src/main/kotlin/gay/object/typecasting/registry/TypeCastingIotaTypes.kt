package gay.`object`.typecasting.registry

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import gay.`object`.typecasting.casting.iotas.SubroutineIota

object TypeCastingIotaTypes : TypeCastingRegistrar<IotaType<*>>(
    HexRegistries.IOTA_TYPE,
    { HexIotaTypes.REGISTRY },
) {
    val SUBROUTINE = register("subroutine") { SubroutineIota.TYPE }
}
