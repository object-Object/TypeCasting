package gay.object.typecasting.mixin;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gay.object.typecasting.casting.actions.OpSetOpLimitTrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CastingVM.class)
public class MixinCastingVM {
    @ModifyReturnValue(method = "executeInner", at = @At("RETURN"))
    private CastResult typecasting$maybeTriggerOpLimit(CastResult original) {
        return OpSetOpLimitTrap.maybeTriggerOpLimit((CastingVM) (Object) this, original);
    }
}
