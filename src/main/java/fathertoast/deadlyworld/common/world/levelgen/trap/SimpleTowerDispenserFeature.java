package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.world.levelgen.TowerDispenserSettings;
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

public class SimpleTowerDispenserFeature extends DeadlyFeature<SimpleTowerDispenserFeature.Configuration> {
    
    public record Configuration(
            BlockStateProvider baseProvider,
            BlockStateProvider dispenserProvider,
            TowerDispenserSettings dispenserSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<SimpleTowerDispenserFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "base_provider" ).forGetter( SimpleTowerDispenserFeature.Configuration::baseProvider ),
                BlockStateProvider.CODEC.fieldOf( "dispenser_provider" ).forGetter( SimpleTowerDispenserFeature.Configuration::dispenserProvider ),
                TowerDispenserSettings.CODEC.fieldOf( "dispenser" ).forGetter( SimpleTowerDispenserFeature.Configuration::dispenserSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( SimpleTowerDispenserFeature.Configuration::cannotReplace )
        ).apply( instance, SimpleTowerDispenserFeature.Configuration::new ) );
    }
    
    public SimpleTowerDispenserFeature() { this( SimpleTowerDispenserFeature.Configuration.CODEC ); }
    
    public SimpleTowerDispenserFeature( Codec<SimpleTowerDispenserFeature.Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<SimpleTowerDispenserFeature.Configuration> context ) {
        final SimpleTowerDispenserFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        
        final BlockPos basePos = context.origin();
        final BlockPos dispenserPos = basePos.above();
        
        // TODO - replace with something less bad
        if( hasNearbyTraps( level, context.origin(), 3 ) ) return false;
        
        // Count height the tower needs to be through fluid to reach the ground
        final BlockPos.MutableBlockPos cursor = basePos.mutable();
        int height;
        for( height = 0; height < 9; height++ ) {
            cursor.move( Direction.DOWN );
            if( level.getBlockState( cursor ).isSolid() ) break; // Deprecated, but it's what world gen uses
        }
        if( height >= 9 ) return false; // Fluid is too deep
        
        // Make sure there is some "open" space around the tower
        // so we don't generate in super cramped places
        for( BlockPos pos : BlockPos.betweenClosed(
                basePos.offset( -1, 0, -1 ),
                basePos.offset( 1, 2, 1 ) ) ) {
            BlockState state = level.getBlockState( pos );
            
            if( state.blocksMotion() )
                return false;
        }
        
        // Make sure the tower dispenser block at least can be placed
        if( !predicate.test( level.getBlockState( dispenserPos ) ) ) return false;
        // Don't replace blocks with block entities
        if( level.getExistingBlockEntity( dispenserPos ) != null ) return false;
        
        // Place the tower dispenser
        BlockState dispenserBlock = config.dispenserProvider.getState( random, dispenserPos );
        setBlock( level, dispenserPos, dispenserBlock );
        config.dispenserSettings.initializeDispenser( level, dispenserPos, random );
        
        // Place the tower base
        BlockState baseBlock = config.baseProvider.getState( random, basePos );
        boolean hasBase = !baseBlock.isAir();
        if( hasBase ) while( cursor.getY() < basePos.getY() ) {
            cursor.move( Direction.UP );
            safeSetBlock( level, cursor, baseBlock, predicate );
        }
        
        return true;
    }
}