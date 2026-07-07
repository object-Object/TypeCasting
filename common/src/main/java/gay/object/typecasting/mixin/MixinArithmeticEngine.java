package gay.object.typecasting.mixin;

import at.petrak.hexcasting.api.casting.arithmetic.engine.ArithmeticEngine;
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gay.object.typecasting.mixinsupport.IMixinArithmeticEngine;
import gay.object.typecasting.mixinsupport.OperatorResolvedException;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ArithmeticEngine.class, remap = false)
public abstract class MixinArithmeticEngine implements IMixinArithmeticEngine {
    @Unique
    private boolean typecasting$isResolvingOperator = false;

    @SuppressWarnings("UnusedReturnValue")
    @Invoker
    public abstract OperationResult callRun(
        HexPattern pattern,
        CastingEnvironment env,
        CastingImage image,
        SpellContinuation continuation
    ) throws Mishap;

    @NotNull
    @Override
    public Operator typecasting$resolveOperator(
        @NotNull HexPattern pattern,
        @NotNull CastingEnvironment env,
        @NotNull CastingImage image,
        @NotNull SpellContinuation continuation
    ) {
        // HACKY HACKY HACKY HACK
        typecasting$isResolvingOperator = true;
        try {
            callRun(pattern, env, image, continuation);
        } catch (OperatorResolvedException e) {
            return e.getOperator();
        } finally {
            typecasting$isResolvingOperator = false;
        }
        throw new IllegalStateException();
    }

    @WrapOperation(
        method = "run",
        at = @At(
            value = "INVOKE",
            target = "Lat/petrak/hexcasting/api/casting/arithmetic/operator/Operator;operate(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;Lat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation;)Lat/petrak/hexcasting/api/casting/eval/OperationResult;"
        )
    )
    private OperationResult typecasting$maybeResolveOperator(
        Operator instance,
        CastingEnvironment castingEnvironment,
        CastingImage castingImage,
        SpellContinuation continuation,
        Operation<OperationResult> original
    ) {
        if (typecasting$isResolvingOperator) {
            throw new OperatorResolvedException(instance);
        }
        return original.call(instance, castingEnvironment, castingImage, continuation);
    }
}
