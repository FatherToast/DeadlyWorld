package fathertoast.deadlyworld.common.util.mixin_hooks;

import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.SpawnerConfig;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.world.levelgen.settings.SpawnerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

public class CommonMixinHooks {

    public static void pointedDripstoneProjectileHit( Level level, BlockState state, BlockHitResult hitResult, Projectile projectile, CallbackInfo ci ) {
        if ( !Config.MAIN.STALACTITE_OVERHAUL.pointedDripstoneSniping.get() ) return;

        BlockPos pos = hitResult.getBlockPos();

        if ( !level.isClientSide && projectile.mayInteract( level, pos )
                && projectile.getType().is( EntityTypeTags.IMPACT_PROJECTILES )
                && projectile.getDeltaMovement().length() > 0.5D ) {
            level.destroyBlock( pos, true );
            ci.cancel();
        }
    }

    public static void changeMonsterRoomSpawner( FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir,
                                                 Predicate predicate, BlockPos blockpos, RandomSource randomsource, WorldGenLevel worldgenlevel ) {
        worldgenlevel.setBlock( blockpos, DWBlocks.spawner( SpawnerType.DUNGEON ).get().defaultBlockState(), Block.UPDATE_CLIENTS );

        if ( worldgenlevel.getBlockEntity( blockpos ) instanceof DeadlySpawnerBlockEntity spawner ) {
            SpawnerSettings settings = SpawnerSettings.of( SpawnerType.DUNGEON, Config.getDimensionConfigs( worldgenlevel.getLevel().dimension() ) );
            spawner.getSpawnerLogic().initializeSpawner( worldgenlevel, blockpos, randomsource, settings );

            if ( Config.getDimensionConfigs( worldgenlevel.getLevel().dimension() ).SPAWNERS.DUNGEON.useForgeHookEntities.get() )
                spawner.getSpawnerLogic().enableUseForgeHook( worldgenlevel.getLevel(), blockpos );
        }
        cir.setReturnValue( true );
    }
}
