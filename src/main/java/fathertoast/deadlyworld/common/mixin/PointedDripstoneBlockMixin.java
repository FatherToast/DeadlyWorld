package fathertoast.deadlyworld.common.mixin;

import fathertoast.deadlyworld.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( PointedDripstoneBlock.class )
public abstract class PointedDripstoneBlockMixin extends Block implements Fallable, SimpleWaterloggedBlock {

    public PointedDripstoneBlockMixin( Properties properties ) {
        super( properties );
    }


    @Inject(method = "onProjectileHit", at = @At("HEAD"), cancellable = true)
    public void injectOnProjectileHit( Level level, BlockState state, BlockHitResult hitResult, Projectile projectile, CallbackInfo ci ) {
        CommonMixinHooks.pointedDripstoneProjectileHit( level, state, hitResult, projectile, ci );
    }
}
