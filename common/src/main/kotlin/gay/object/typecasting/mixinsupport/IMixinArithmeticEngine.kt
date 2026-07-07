package gay.`object`.typecasting.mixinsupport

import at.petrak.hexcasting.api.casting.arithmetic.engine.ArithmeticEngine
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.math.HexPattern

interface IMixinArithmeticEngine {
    fun `typecasting$resolveOperator`(
        pattern: HexPattern,
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation,
    ): Operator
}

val ArithmeticEngine.mixin get() = this as IMixinArithmeticEngine
