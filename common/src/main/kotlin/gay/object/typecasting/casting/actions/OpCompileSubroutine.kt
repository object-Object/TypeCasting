package gay.`object`.typecasting.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction.CostMediaActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import gay.`object`.typecasting.casting.getPatternList
import gay.`object`.typecasting.casting.iotas.SubroutineIota

object OpCompileSubroutine : ConstMediaAction {
    override val argc = 1
    override val mediaCost get() = 5 * MediaConstants.CRYSTAL_UNIT

    override fun executeWithOpCount(args: List<Iota>, env: CastingEnvironment): CostMediaActionResult {
        val list = args.getPatternList(0, argc)
        val subroutine = SubroutineIota(list)
        subroutine.compile(env)
        return CostMediaActionResult(
            resultStack = listOf(subroutine),
            opCount = list.size.toLong().coerceAtLeast(1),
        )
    }

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        throw IllegalStateException()
    }
}
