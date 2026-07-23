package gay.`object`.typecasting.casting

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import gay.`object`.typecasting.casting.iotas.SubroutineIota

fun List<Iota>.getPatternList(idx: Int, argc: Int = 0): List<HexPattern> {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    val reverseIdx = if (argc == 0) idx else argc - (idx + 1)
    if (x !is ListIota) {
        throw MishapInvalidIota.ofType(x, reverseIdx, "list")
    }
    return x.list.map {
        (it as? PatternIota)?.pattern
            ?: throw MishapInvalidIota.of(x, reverseIdx, "typecasting.pattern_list")
    }
}

fun List<Iota>.getSubroutine(idx: Int, argc: Int = 0): SubroutineIota {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    val reverseIdx = if (argc == 0) idx else argc - (idx + 1)
    if (x !is SubroutineIota) {
        throw MishapInvalidIota.of(x, reverseIdx, "typecasting.subroutine")
    }
    return x
}
