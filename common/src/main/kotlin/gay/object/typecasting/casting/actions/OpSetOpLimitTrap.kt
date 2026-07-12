package gay.`object`.typecasting.casting.actions

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.*
import at.petrak.hexcasting.api.casting.getIntBetween
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.api.utils.hasInt
import at.petrak.hexcasting.api.utils.hasList
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel

object OpSetOpLimitTrap : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation,
    ): OperationResult {
        val stack = image.stack.toMutableList()
        if (stack.size < 2) {
            throw MishapNotEnoughArgs(2, stack.size)
        }

        val code = stack.getList(stack.lastIndex - 1, stack.size)
        val opLimit = stack.getIntBetween(
            idx = stack.lastIndex,
            min = 1,
            max = env.maxOpCount() - image.opsConsumed.toInt(),
            argc = stack.size,
        )
        stack.removeLast()
        stack.removeLast()

        image.userData.put(CODE_TAG, code.serializeToNBT())
        image.userData.putInt(OP_LIMIT_TAG, opLimit)

        return OperationResult(
            newImage = image.withUsedOp().copy(stack = stack),
            sideEffects = listOf(),
            newContinuation = continuation,
            sound = HexEvalSounds.NORMAL_EXECUTE,
        )
    }

    @JvmStatic
    fun maybeTriggerOpLimit(vm: CastingVM, result: CastResult): CastResult {
        val image = result.newData ?: return result
        if (image.escapeNext || image.parenCount > 0) return result
        val opLimit = getOpLimit(image.userData) ?: return result
        if (vm.env.maxOpCount() - image.opsConsumed >= opLimit) return result

        val newCont = getCode(image.userData, vm.env.world)?.let {
            result.continuation
                .pushFrame(FrameFinishEval)
                .pushFrame(FrameEvaluate(it, isMetacasting = true))
        } ?: result.continuation

        image.userData.remove(CODE_TAG)
        image.userData.remove(OP_LIMIT_TAG)

        return result.copy(continuation = newCont)
    }

    fun getCode(userData: CompoundTag, level: ServerLevel): SpellList? =
        if (userData.hasList(CODE_TAG)) {
            HexIotaTypes.LIST.deserialize(userData.getList(CODE_TAG, Tag.TAG_COMPOUND), level)?.list
        } else null

    fun getOpLimit(userData: CompoundTag): Int? =
        if (userData.hasInt(OP_LIMIT_TAG)) userData.getInt(OP_LIMIT_TAG) else null

    const val CODE_TAG = "typecasting:eval_with_op_limit/code"
    const val OP_LIMIT_TAG = "typecasting:eval_with_op_limit/op_limit"
}
