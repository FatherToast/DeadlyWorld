package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spike_trap.BaseSpikeTrapBlock;
import fathertoast.deadlyworld.common.config.dimension.FeatureConfig;
import fathertoast.deadlyworld.common.world.levelgen.SpikePatchSettings;
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

public class SpikeTrapPatchFeature extends DeadlyFeature<SpikeTrapPatchFeature.Configuration> {
    public record Configuration(
            BlockStateProvider spikesProvider,
            SpikePatchSettings patchSettings,
            TagKey<Block> canReplace
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "spikes_provider" ).forGetter( Configuration::spikesProvider ),
                SpikePatchSettings.CODEC.fieldOf( "patch_settings" ).forGetter( Configuration::patchSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "can_replace" ).forGetter( Configuration::canReplace )
        ).apply( instance, Configuration::new ) );
    }
    
    public SpikeTrapPatchFeature() { this( Configuration.CODEC ); }
    
    public SpikeTrapPatchFeature( Codec<Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final BlockPos origin = context.origin();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = ( state ) -> state.is( config.canReplace );
        
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        
        int xzSpread = config.patchSettings.xzSpread().sample( random );
        int ySpread = config.patchSettings.ySpread().sample( random );
        int patchSize = config.patchSettings.placementTries().sample( random );
        
        // Place spike patch
        BlockState spikes = null;
        int placedBlocks = 0;
        for( int tries = 0; tries < patchSize; ++tries ) {
            cursor.setWithOffset(
                    origin,
                    random.nextInt( xzSpread ) - random.nextInt( xzSpread ),
                    random.nextInt( ySpread ) - random.nextInt( ySpread ),
                    random.nextInt( xzSpread ) - random.nextInt( xzSpread )
            );
            
            // Check if the existing state at target can be replaced
            if( !predicate.test( level.getBlockState( cursor ) ) ) continue;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( cursor ) != null ) continue;
            
            // Make sure the location is valid for the picked spikes
            spikes = config.spikesProvider.getState( random, cursor );
            if( spikes.canSurvive( level, cursor ) ) {
                // Place spike trap
                setBlock( level, cursor, spikes );
                ++placedBlocks;
            }
        }
        
        if( placedBlocks > 0 ) {
            // Generate debug marker
            if( spikes.getBlock() instanceof BaseSpikeTrapBlock spikeTrapBlock ) {
                final FeatureConfig.FeatureTypeCategory featureConfig = spikeTrapBlock.getSpikeTrapType().getConfig( level.getLevel() );
                debugMarkerIfEnabled( level, cursor, featureConfig );
            }
            return true;
        }
        return false;
    }
}