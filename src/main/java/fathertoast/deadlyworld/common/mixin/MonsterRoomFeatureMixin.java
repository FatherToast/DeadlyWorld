package fathertoast.deadlyworld.common.mixin;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Predicate;

@Mixin( MonsterRoomFeature.class )
public abstract class MonsterRoomFeatureMixin extends Feature<NoneFeatureConfiguration> {
    
    public MonsterRoomFeatureMixin( Codec<NoneFeatureConfiguration> codec ) {
        super( codec );
    }
    
    @Inject(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/MonsterRoomFeature;safeSetBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Predicate;)V",
                    ordinal = 4
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true
    )
    public void onPlace( FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir, Predicate predicate, BlockPos blockpos, RandomSource randomsource, WorldGenLevel worldgenlevel ) {
        CommonMixinHooks.changeMonsterRoomSpawner( cir, blockpos, randomsource, worldgenlevel );
    }
}
