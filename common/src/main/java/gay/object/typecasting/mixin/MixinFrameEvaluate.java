package gay.object.typecasting.mixin;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gay.object.typecasting.casting.actions.OpSetOpLimitTrap;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FrameEvaluate.class)
public class MixinFrameEvaluate {
    @WrapMethod(method = "evaluate")
    private CastResult typecasting$maybeTriggerOpLimit(
        SpellContinuation continuation,
        ServerLevel level,
        CastingVM harness,
        Operation<CastResult> original
    ) {
        var result = original.call(continuation, level, harness);
        return OpSetOpLimitTrap.maybeTriggerOpLimit(harness, level, result);
    }
}
