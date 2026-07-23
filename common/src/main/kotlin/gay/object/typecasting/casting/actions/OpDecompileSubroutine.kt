package gay.`object`.typecasting.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.misc.MediaConstants
import gay.`object`.typecasting.casting.getSubroutine

object OpDecompileSubroutine : ConstMediaAction {
    override val argc = 1
    override val mediaCost get() = 5 * MediaConstants.CRYSTAL_UNIT // symmetry

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<ListIota> {
        val subroutine = args.getSubroutine(0, argc)
        return subroutine.patterns.map(::PatternIota).asActionResult
    }
}
