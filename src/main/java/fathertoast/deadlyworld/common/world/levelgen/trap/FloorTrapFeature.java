package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapBlock;
import fathertoast.deadlyworld.common.world.levelgen.FloorTrapSettings;
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

public class FloorTrapFeature extends DeadlyFeature<FloorTrapFeature.Configuration> {
    public record Configuration(
            BlockStateProvider trapProvider,
            FloorTrapSettings trapSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<FloorTrapFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "trap_provider" ).forGetter( FloorTrapFeature.Configuration::trapProvider ),
                FloorTrapSettings.CODEC.fieldOf( "trap" ).forGetter( FloorTrapFeature.Configuration::trapSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( FloorTrapFeature.Configuration::cannotReplace )
        ).apply( instance, FloorTrapFeature.Configuration::new ) );
    }
    
    public FloorTrapFeature() { this( FloorTrapFeature.Configuration.CODEC ); }
    
    public FloorTrapFeature( Codec<FloorTrapFeature.Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<FloorTrapFeature.Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final FloorTrapFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        final BlockPos.MutableBlockPos trapPos = context.origin().mutable();
        
        // Move up if on a lip
        if( isOnLip( level, trapPos ) ) trapPos.move( Direction.UP );
        
        // Put trap in the floor
        trapPos.move( Direction.DOWN );
        
        // Check if we can place here
        if( notSubfeature ) {
            if( hasNearbyTraps( level, trapPos, 3 ) ) return false;
            
            // Make sure the trap block can be placed
            if( !predicate.test( level.getBlockState( trapPos ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( trapPos ) != null ) return false;
        }
        
        // Place the trap
        BlockState trapBlock = config.trapProvider.getState( random, trapPos );
        setBlock( level, trapPos, trapBlock );
        if( trapBlock.getBlock() instanceof FloorTrapBlock ) {
            config.trapSettings.initializeTrap( level, trapPos, random );
        }
        
        return true;
    }
}