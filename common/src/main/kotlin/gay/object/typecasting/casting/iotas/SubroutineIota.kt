package gay.`object`.typecasting.casting.iotas

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.*
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.downcast
import at.petrak.hexcasting.api.utils.isOfTag
import at.petrak.hexcasting.api.utils.red
import at.petrak.hexcasting.common.casting.PatternRegistryManifest
import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import gay.`object`.typecasting.casting.mishaps.MishapInvalidSubroutinePattern
import gay.`object`.typecasting.casting.mishaps.MishapSubroutineSize
import gay.`object`.typecasting.config.TypeCastingServerConfig
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel

class SubroutineIota(patterns: List<HexPattern>) : Iota(TYPE, patterns) {
    @Suppress("UNCHECKED_CAST")
    val patterns = payload as List<HexPattern>

    private var subroutine: CompiledSubroutine? = null

    override fun isTruthy() = patterns.isNotEmpty()

    override fun toleratesOther(that: Iota) =
        typesMatch(this, that)
            && that is SubroutineIota
            && patterns.size == that.patterns.size
            && patterns.zip(that.patterns).all { (thisPat, thatPat) -> thisPat.sigsEqual(thatPat) }

    override fun serialize() = patterns.map { it.serializeToNBT() }.toCollection(ListTag())

    override fun size() = patterns.size + 1

    override fun executable() = true

    override fun execute(vm: CastingVM, world: ServerLevel, continuation: SpellContinuation): CastResult {
        try {
            val subroutine = subroutine ?: compile(vm.env)

            val stack = vm.image.stack.toMutableList()
            var opCount = 1L

            for ((pattern, action, getName) in subroutine.patterns) {
                try {
                    if (action.argc > stack.size) {
                        throw MishapNotEnoughArgs(action.argc, stack.size)
                    }
                    val args = stack.takeLast(action.argc)
                    repeat(action.argc) { stack.removeLast() }
                    val result = action.executeWithOpCount(args, vm.env)
                    stack.addAll(result.resultStack)

                    // If some addon makes their pattern cost more than 1 op, only discount it by 1
                    val discount = if (TypeCastingServerConfig.config.discountSubroutineOps) 1 else 0
                    opCount += result.opCount - discount
                } catch (mishap: Mishap) {
                    return CastResult(
                        cast = this, // lie?
                        continuation = continuation,
                        newData = null,
                        sideEffects = listOf(OperatorSideEffect.DoMishap(mishap, Mishap.Context(pattern, getName()))),
                        resolutionType = mishap.resolutionType(vm.env),
                        sound = HexEvalSounds.MISHAP,
                    )
                }
            }

            if (vm.env.extractMedia(subroutine.mediaCost, true) > 0) {
                throw MishapNotEnoughMedia(subroutine.mediaCost)
            }

            // Always consume at least 1 op
            val opsConsumed = vm.image.opsConsumed + opCount.coerceAtLeast(1)
            if (opsConsumed > vm.env.maxOpCount()) {
                throw MishapEvalTooMuch()
            }

            return CastResult(
                cast = this,
                continuation = continuation,
                newData = vm.image.copy(stack = stack, opsConsumed = opsConsumed),
                sideEffects = if (subroutine.mediaCost > 0) {
                    listOf(OperatorSideEffect.ConsumeMedia(subroutine.mediaCost))
                } else listOf(),
                resolutionType = ResolvedPatternType.EVALUATED,
                sound = HexEvalSounds.NORMAL_EXECUTE,
            )
        } catch (mishap: Mishap) {
            return CastResult(
                cast = this,
                continuation = continuation,
                newData = null,
                sideEffects = listOf(OperatorSideEffect.DoMishap(mishap, Mishap.Context(null, null))),
                resolutionType = mishap.resolutionType(vm.env),
                sound = HexEvalSounds.MISHAP,
            )
        }
    }

    fun compile(env: CastingEnvironment) = CompiledSubroutine(patterns, env).also { this.subroutine = it }

    companion object {
        val TYPE = object : IotaType<SubroutineIota>() {
            override fun deserialize(tag: Tag, world: ServerLevel) =
                SubroutineIota(mapTag(tag, HexPattern::fromNBT))

            override fun display(tag: Tag) =
                "typecasting.tooltip.subroutine_contents"
                    .asTranslatedComponent(mapTag(tag, ::getDisplay))
                    .red

            override fun color() = 0xff_b9005d.toInt()

            private fun <T> mapTag(tag: Tag, transform: (CompoundTag) -> T): List<T> =
                tag.downcast(ListTag.TYPE).map { transform(it.downcast(CompoundTag.TYPE)) }
        }
    }
}

data class CompiledSubroutine(val patterns: List<CompiledPattern>) {
    constructor(patterns: List<HexPattern>, env: CastingEnvironment) : this(
        patterns
            .also {
                if (it.size > TypeCastingServerConfig.config.maxSubroutineSize) {
                    throw MishapSubroutineSize()
                }
            }
            .map {
                val lookup = PatternRegistryManifest.matchPattern(it, env, false)
                env.precheckAction(lookup)

                when (lookup) {
                    is PatternShapeMatch.Normal -> lookupKey(it, lookup.key, env)
                    is PatternShapeMatch.PerWorld -> lookupKey(it, lookup.key, env)
                    is PatternShapeMatch.Special -> CompiledPattern(it, lookup.handler.act(), lookup.handler::getName)
                    is PatternShapeMatch.Nothing -> throw MishapInvalidPattern(it)
                }
            }
    )

    val mediaCost = patterns.sumOf { it.action.mediaCost }

    companion object {
        private fun lookupKey(
            pattern: HexPattern,
            key: ResourceKey<ActionRegistryEntry>,
            env: CastingEnvironment,
        ): CompiledPattern {
            val reqsEnlightenment = isOfTag(HexActions.REGISTRY, key, HexTags.Actions.REQUIRES_ENLIGHTENMENT)
            if (reqsEnlightenment && !env.isEnlightened) {
                throw MishapUnenlightened()
            }
            return CompiledPattern(
                pattern = pattern,
                action = HexActions.REGISTRY.get(key)!!.action,
                getName = { HexAPI.instance().getActionI18n(key, reqsEnlightenment) },
            )
        }
    }
}

data class CompiledPattern(
    val pattern: HexPattern,
    val action: ConstMediaAction,
    val getName: () -> Component?,
) {
    constructor(pattern: HexPattern, action: Action, getName: () -> Component?) : this(
        pattern = pattern,
        action = action as? ConstMediaAction ?: throw MishapInvalidSubroutinePattern(pattern),
        getName = getName,
    )
}
