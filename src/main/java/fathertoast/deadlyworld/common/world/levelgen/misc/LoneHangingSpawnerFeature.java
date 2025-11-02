package fathertoast.deadlyworld.common.world.levelgen.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
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
            IntProvider distFromFloor,
            SpawnerSettings spawnerSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "spawner_provider" ).forGetter( Configuration::spawnerProvider ),
                BlockStateProvider.CODEC.fieldOf( "trail_provider" ).forGetter( Configuration::trailProvider ),
                IntProvider.CODEC.fieldOf( "distance_from_floor" ).forGetter( Configuration::distFromFloor ),
                SpawnerSettings.CODEC.fieldOf( "spawner" ).forGetter( Configuration::spawnerSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( Configuration::cannotReplace )
        ).apply( instance, Configuration::new ) );
    }
    
    public LoneHangingSpawnerFeature() { this( Configuration.CODEC ); }
    
    public LoneHangingSpawnerFeature( Codec<Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        final int distFromFloor = config.distFromFloor.sample( random );
        final BlockPos.MutableBlockPos cursor = context.origin().mutable();
        
        boolean makeChain;
        
        // Find spawner pos
        final BlockPos spawnerPos;
        if( !level.getBlockState( cursor.move( Direction.UP ) ).isAir() ) {
            // No open space for the spawner to be off the ground
            spawnerPos = context.origin();
            makeChain = false;
        }
        else if( !level.getBlockState( cursor.move( Direction.UP ) ).isAir() ) {
            // No open space for a chain
            spawnerPos = cursor.move( Direction.DOWN ).immutable();
            makeChain = false;
        }
        else {
            // Get as close to the selected distance from floor as we can
            int y = 1;
            while( y < distFromFloor && cursor.getY() < level.getMaxBuildHeight() ) {
                if( !level.getBlockState( cursor.move( Direction.UP ) ).isAir() ) {
                    // Failed to move up
                    cursor.move( Direction.DOWN );
                    break;
                }
                y++;
            }
            spawnerPos = cursor.move( Direction.DOWN ).immutable();
            makeChain = true;
        }
        
        // Scan up to find a ceiling we can reach with a chain
        if( makeChain ) {
            boolean foundCeiling = false;
            for( int y = 0; y < 50 && cursor.getY() < level.getMaxBuildHeight(); y++ ) {
                if( level.getBlockState( cursor.move( Direction.UP ) ).isSolid() ) {
                    foundCeiling = true;
                    break;
                }
            }
            if( !foundCeiling ) makeChain = false;
        }
        
        // Check if we can place here
        if( notSubfeature ) {
            if( !makeChain ) return false;
            
            // TODO - replace with something less bad
            if( hasNearbyTraps( level, spawnerPos, 3 ) ) return false;
            
            // Make sure the spawner block at least can be placed
            if( !predicate.test( level.getBlockState( spawnerPos ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( spawnerPos ) != null ) return false;
        }
        
        // Place the spawner
        BlockState spawnerBlock = config.spawnerProvider.getState( random, spawnerPos );
        setBlock( level, spawnerPos, spawnerBlock );
        if( spawnerBlock.getBlock() instanceof DeadlySpawnerBlock ) {
            config.spawnerSettings.initializeSpawner( level, spawnerPos, random );
        }
        
        // Place chains from the ceiling back down to the spawner
        if( makeChain ) {
            cursor.move( Direction.DOWN ); // Last cursor pos was the solid ceiling block
            while( cursor.getY() > spawnerPos.getY() ) {
                safeSetBlock( level, cursor, config.trailProvider, random, predicate );
                cursor.move( Direction.DOWN );
            }
        }
        
        return true;
    }
}