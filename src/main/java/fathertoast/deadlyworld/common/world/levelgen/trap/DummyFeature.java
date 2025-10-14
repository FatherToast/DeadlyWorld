package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * This feature exists solely to fill an optional with a non-null configured feature to enable
 * generating any DW feature as a 'subfeature', which changes the placement logic to force-place.
 *
 * @see DeadlyFeature#placeSubfeature(ServerLevel, BlockPos, ResourceLocation, ConfiguredFeature)
 * @see DeadlyFeature#placeSubfeature(ServerLevel, BlockPos, ConfiguredFeature, ConfiguredFeature)
 */
public class DummyFeature extends DeadlyFeature<DummyFeature.Configuration> {
    public record Configuration() implements FeatureConfiguration {
        public static final Codec<DummyFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance
                .stable( new DummyFeature.Configuration() ) );
    }
    
    /** The singleton dummy configured feature. */
    public static final ConfiguredFeature<DummyFeature.Configuration, DummyFeature> CONFIGURED_INSTANCE =
            new ConfiguredFeature<>( new DummyFeature(), new DummyFeature.Configuration() );
    
    public DummyFeature() { this( DummyFeature.Configuration.CODEC ); }
    
    public DummyFeature( Codec<DummyFeature.Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<DummyFeature.Configuration> context ) { return false; }
}