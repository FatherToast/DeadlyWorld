package fathertoast.deadlyworld.common.mixin;

import fathertoast.deadlyworld.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin( Explosion.class )
public abstract class ExplosionMixin {
    
    @Mutable
    @Final
    @Shadow
    private final Entity source;
    
    protected ExplosionMixin( Entity source ) {
        this.source = source;
    }
    
    @ModifyVariable(
            method = "explode",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/item/enchantment/ProtectionEnchantment;getExplosionKnockbackAfterDampener(Lnet/minecraft/world/entity/LivingEntity;D)D"
            ),
            ordinal = 7,
            index = 28
    )
    
    public double onExplode( double original ) {
        return CommonMixinHooks.modifyExplosionKnockback( source, original );
    }
}
