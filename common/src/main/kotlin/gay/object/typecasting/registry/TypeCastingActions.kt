package gay.`object`.typecasting.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexActions
import gay.`object`.typecasting.casting.actions.OpCompileSubroutine

object TypeCastingActions : TypeCastingRegistrar<ActionRegistryEntry>(
    HexRegistries.ACTION,
    { HexActions.REGISTRY },
) {
    val COMPILE_SUBROUTINE = make(
        "compile_subroutine",
        HexDir.EAST,
        "qaedweqaqeqqeqqqeqqeqwaeqeaeqeaeaeqeaeqea",
        OpCompileSubroutine,
    )

    private fun make(name: String, startDir: HexDir, signature: String, action: Action) =
        make(name, startDir, signature) { action }

    private fun make(name: String, startDir: HexDir, signature: String, getAction: () -> Action) = register(name) {
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), getAction())
    }
}
