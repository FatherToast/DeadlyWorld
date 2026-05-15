package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import net.minecraft.core.BlockPos;
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

public class SilverfishNestFeature extends DeadlyFeature<SilverfishNestFeature.Configuration> {
    public record Configuration(
            BlockStateProvider spawnerProvider,
            BlockStateProvider nestProvider,
            SpawnerSettings spawnerSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "spawner_provider" ).forGetter( Configuration::spawnerProvider ),
                BlockStateProvider.CODEC.fieldOf( "nest_provider" ).forGetter( Configuration::nestProvider ),
                SpawnerSettings.CODEC.fieldOf( "spawner" ).forGetter( Configuration::spawnerSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( Configuration::cannotReplace )
        ).apply( instance, Configuration::new ) );
    }
    
    public SilverfishNestFeature() { this( Configuration.CODEC ); }
    
    public SilverfishNestFeature( Codec<Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        
        if( notSubfeature ) {
            if( hasNearbyTraps( level, context.origin(), 3 ) ) return false;
            
            // Make sure the spawner block at least can be placed
            if( !predicate.test( level.getBlockState( context.origin() ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( context.origin() ) != null ) return false;
        }
        
        // Place the spawner
        BlockState spawnerBlock = config.spawnerProvider.getState( random, context.origin() );
        setBlock( level, context.origin(), spawnerBlock );
        if( spawnerBlock.getBlock() instanceof DeadlySpawnerBlock ) {
            config.spawnerSettings.initializeSpawner( level, context.origin(), random );
        }
        
        // Place the nest covering
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for( int y = -1; y <= 1; y++ ) {
            for( int x = -1; x <= 1; x++ ) {
                for( int z = -1; z <= 1; z++ ) {
                    int abs = Math.abs( x ) + Math.abs( y ) + Math.abs( z );
                    if( abs != 0 && abs <= 2 ) {
                        safeSetInfestedBlock( level, cursor.setWithOffset( context.origin(), x, y, z ),
                                config.nestProvider, random, predicate );
                    }
                }
            }
        }
        
        return true;
    }
}