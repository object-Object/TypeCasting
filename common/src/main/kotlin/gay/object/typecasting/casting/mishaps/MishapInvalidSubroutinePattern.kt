package gay.`object`.typecasting.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import net.minecraft.world.item.DyeColor

class MishapInvalidSubroutinePattern(private val pattern: HexPattern) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.YELLOW)

    override fun resolutionType(ctx: CastingEnvironment) = ResolvedPatternType.INVALID

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
    }

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) =
        error("typecasting.invalid_subroutine_pattern", PatternIota.display(pattern))
}
