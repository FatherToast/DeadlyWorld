package fathertoast.deadlyworld.common.util.mixin_hooks;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CommonMixinHooks {

    public static void pointedDripstoneProjectileHit( Level level, BlockState state, BlockHitResult hitResult, Projectile projectile, CallbackInfo ci ) {
        BlockPos pos = hitResult.getBlockPos();

        if ( !level.isClientSide && projectile.mayInteract( level, pos )
                && projectile.getType().is( EntityTypeTags.IMPACT_PROJECTILES )
                && projectile.getDeltaMovement().length() > 0.5D ) {
            level.destroyBlock( pos, true );
            ci.cancel();
        }
    }
}
