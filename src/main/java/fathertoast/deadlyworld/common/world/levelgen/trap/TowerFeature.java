package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
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

public class TowerFeature extends DeadlyFeature<TowerFeature.Configuration> {
    
    public record Configuration(
            BlockStateProvider dispenserProvider,
            BlockStateProvider baseProvider,
            TowerDispenserSettings dispenserSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<TowerFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "dispenser_provider" ).forGetter( TowerFeature.Configuration::dispenserProvider ),
                BlockStateProvider.CODEC.fieldOf( "base_provider" ).forGetter( TowerFeature.Configuration::baseProvider ),
                TowerDispenserSettings.CODEC.fieldOf( "dispenser" ).forGetter( TowerFeature.Configuration::dispenserSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( TowerFeature.Configuration::cannotReplace )
        ).apply( instance, TowerFeature.Configuration::new ) );
    }
    
    public TowerFeature() { this( TowerFeature.Configuration.CODEC ); }
    
    public TowerFeature( Codec<TowerFeature.Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<TowerFeature.Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final TowerFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        final BlockPos.MutableBlockPos basePos = context.origin().mutable();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        
        // Move up if on a lip
        if( isOnLip( level, basePos, cursor ) ) basePos.move( Direction.UP );
        
        final BlockPos dispenserPos = basePos.above();
        
        // Count height the tower needs to be through fluid to reach the ground
        cursor.set( basePos );
        int height;
        for( height = 0; height < 9; height++ ) {
            cursor.move( Direction.DOWN );
            if( level.getBlockState( cursor ).isSolid() ) break; // Deprecated, but it's what world gen uses
        }
        
        if( notSubfeature ) {
            // Make sure fluid is not too deep
            if( height >= 9 ) return false;
            
            // Make sure there is some "open" space around the tower
            // so we don't generate in super cramped places
            for( BlockPos pos : BlockPos.betweenClosed(
                    dispenserPos.getX() - 1, dispenserPos.getY(), dispenserPos.getZ() - 1,
                    dispenserPos.getX() + 1, dispenserPos.getY() + 1, dispenserPos.getZ() + 1 ) ) {
                if( level.getBlockState( pos ).blocksMotion() ) return false;
            }
            
            // TODO - replace with something less bad
            if( hasNearbyTraps( level, basePos, 3 ) ) return false;
            
            // Make sure the tower dispenser block at least can be placed
            if( !predicate.test( level.getBlockState( dispenserPos ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( dispenserPos ) != null ) return false;
        }
        
        // Place the tower dispenser
        BlockState dispenserBlock = config.dispenserProvider.getState( random, dispenserPos );
        setBlock( level, dispenserPos, dispenserBlock );
        if( dispenserBlock.getBlock() instanceof TowerDispenserBlock ) {
            config.dispenserSettings.initializeDispenser( level, dispenserPos, random );
        }
        
        // Place the tower base
        while( cursor.getY() < basePos.getY() ) {
            safeSetBlock( level, cursor.move( Direction.UP ), config.baseProvider, random, predicate );
        }
        
        return true;
    }
}