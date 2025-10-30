package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Predicate;

public class LoneHangingSpawnerFeature extends DeadlyFeature<LoneHangingSpawnerFeature.Configuration> {
    public record Configuration(
            BlockStateProvider spawnerProvider,
            BlockStateProvider trailProvider,
            ConfigConstantIntProvider distFromFloor,
            SpawnerSettings spawnerSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<LoneHangingSpawnerFeature.Configuration> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "spawner_provider" ).forGetter( LoneHangingSpawnerFeature.Configuration::spawnerProvider ),
                BlockStateProvider.CODEC.fieldOf( "trail_provider" ).forGetter( LoneHangingSpawnerFeature.Configuration::trailProvider ),
                ConfigConstantIntProvider.CODEC.fieldOf( "distance_from_floor" ).forGetter( LoneHangingSpawnerFeature.Configuration::distFromFloor ),
                SpawnerSettings.CODEC.fieldOf( "spawner" ).forGetter( LoneHangingSpawnerFeature.Configuration::spawnerSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( LoneHangingSpawnerFeature.Configuration::cannotReplace )
        ).apply( instance, LoneHangingSpawnerFeature.Configuration::new ) );
    }

    public LoneHangingSpawnerFeature() { this( LoneHangingSpawnerFeature.Configuration.CODEC ); }

    public LoneHangingSpawnerFeature(Codec<LoneHangingSpawnerFeature.Configuration> codec ) { super( codec ); }

    @Override
    public boolean place( FeaturePlaceContext<LoneHangingSpawnerFeature.Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final LoneHangingSpawnerFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        final int distFromFloor = config.distFromFloor.getValue();
        final BlockPos origin = context.origin();
        BlockPos.MutableBlockPos cursor = origin.mutable();


        // Check if we can place here
        if( notSubfeature ) {
            // TODO - replace with something less bad
            if( hasNearbyTraps( level, cursor, 3 ) ) return false;

            // Make sure the spawner block at least can be placed
            if( !predicate.test( level.getBlockState( cursor ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( cursor ) != null ) return false;

            // Make sure we have enough air above us to place the spawner
            // and at least one piece of chain above it before hitting a ceiling
            boolean enoughSpace = level.getBlockState( cursor.move( Direction.UP ) ).isAir();
            boolean foundCeiling = false;

            // Scan up 50 blocks at most to find a ceiling
            for ( int y = 0; y < 50; y++ ) {
                if ( level.getBlockState( cursor ).isSolid() ) {
                    foundCeiling = true;
                    break;
                }
                cursor = cursor.move( Direction.UP, 1 );
            }
            if ( !enoughSpace || !foundCeiling )
                return false;
        }

        // Reset cursor to spawner pos
        cursor.set( origin ).move( Direction.UP, distFromFloor );
        // Place the spawner
        BlockState spawnerBlock = config.spawnerProvider.getState( random, cursor );
        setBlock( level, cursor, spawnerBlock );
        if( spawnerBlock.getBlock() instanceof DeadlySpawnerBlock ) {
            config.spawnerSettings.initializeSpawner( level, cursor, random );
        }

        // Place chains until we hit the ceiling
        BlockState trailBlock = config.trailProvider.getState( random, cursor );
        for ( int y = 0; y < 50; y++ ) {
            cursor.move( Direction.UP, 1 );

            if ( level.getBlockState( cursor ).isAir() )
                safeSetBlock( level, cursor, trailBlock, predicate );
            else return true;
        }
        return true;
    }
}
