package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
        public static final Codec<SpikeTrapPatchFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "spikes_provider" ).forGetter( SpikeTrapPatchFeature.Configuration::spikesProvider ),
                SpikePatchSettings.CODEC.fieldOf( "patch_settings" ).forGetter( SpikeTrapPatchFeature.Configuration::patchSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "can_replace" ).forGetter( SpikeTrapPatchFeature.Configuration::canReplace )
        ).apply( instance, SpikeTrapPatchFeature.Configuration::new ) );
    }
    
    public SpikeTrapPatchFeature() {
        this( SpikeTrapPatchFeature.Configuration.CODEC );
    }
    
    public SpikeTrapPatchFeature( Codec<SpikeTrapPatchFeature.Configuration> codec ) {
        super( codec );
    }
    
    @Override
    public boolean place( FeaturePlaceContext<SpikeTrapPatchFeature.Configuration> context ) {
        final SpikeTrapPatchFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final BlockPos origin = context.origin();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = ( state ) -> state.is( config.canReplace );
        
        
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        
        int placedBlocks = 0;
        int xzSpread = config.patchSettings.xzSpread().sample( random );
        int ySpread = config.patchSettings.ySpread().sample( random );
        int patchSize = config.patchSettings.placementTries().sample( random );
        
        // Place spike patch
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
            
            // Place spike trap
            BlockState spikes = config.spikesProvider.getState( random, cursor );
            
            // Make sure the location is valid for the picked spikes
            if( spikes.canSurvive( level, cursor ) ) {
                setBlock( level, cursor, config.spikesProvider, random );
                ++placedBlocks;
            }
        }
        
        // Generate debug marker
        if( placedBlocks > 0 && config.patchSettings.debugMarker().get() )
            debugMarker( level, origin );
        
        return placedBlocks > 0;
    }
}
