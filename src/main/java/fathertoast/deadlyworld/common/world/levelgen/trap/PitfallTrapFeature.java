package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.unstable.PitfallTrapType;
import fathertoast.deadlyworld.common.world.levelgen.PitfallTrapSettings;
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

public class PitfallTrapFeature extends DeadlyFeature<PitfallTrapFeature.Configuration> {
    public record Configuration(
            BlockStateProvider coverProvider,
            BlockStateProvider fillProvider,
            BlockStateProvider floorProvider,
            PitfallTrapSettings pitfallTrapSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<PitfallTrapFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "cover_provider" ).forGetter( PitfallTrapFeature.Configuration::coverProvider ),
                BlockStateProvider.CODEC.fieldOf( "fill_provider" ).forGetter( PitfallTrapFeature.Configuration::fillProvider ),
                BlockStateProvider.CODEC.fieldOf( "floor_provider" ).forGetter( PitfallTrapFeature.Configuration::floorProvider ),
                PitfallTrapSettings.CODEC.fieldOf( "pitfall_trap" ).forGetter( PitfallTrapFeature.Configuration::pitfallTrapSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( PitfallTrapFeature.Configuration::cannotReplace )
        ).apply( instance, PitfallTrapFeature.Configuration::new ) );
    }
    
    public PitfallTrapFeature() { this( PitfallTrapFeature.Configuration.CODEC ); }
    
    public PitfallTrapFeature( Codec<PitfallTrapFeature.Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<PitfallTrapFeature.Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final PitfallTrapFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        
        final BlockPos origin = context.origin();
        final BlockPos buildPos = origin.below();
        final BlockPos.MutableBlockPos cursor = buildPos.mutable();
        
        final int centerX = buildPos.getX();
        final int centerY = buildPos.getY();
        final int centerZ = buildPos.getZ();
        final int radius = config.pitfallTrapSettings.pitRadius().sample( random );
        final int outerRadius = radius + 1;
        final int depth = config.pitfallTrapSettings.pitDepth().sample( random );
        final int outerDepth = depth + 1;
        
        // Check that we are building inside a solid cuboid area.
        // We want to avoid building if there are holes in the walls and whatnot.
        if( notSubfeature ) {
            for( int y = centerY; y > centerY - outerDepth; y-- ) {
                for( int x = centerX - outerRadius; x <= centerX + outerRadius; x++ ) {
                    for( int z = centerZ - outerRadius; z <= centerZ + outerRadius; z++ ) {
                        cursor.set( x, y, z );
                        
                        // In case pack creators specify a huge radius or something
                        if( !level.hasChunkAt( cursor ) ) return false;
                        
                        BlockState state = level.getBlockState( cursor );
                        if( !state.isSolid() ) return false;
                    }
                }
            }
        }
        
        final BlockState coverBlock = config.coverProvider.getState( random, buildPos );
        final BlockState floorBlock = config.floorProvider.getState( random, buildPos );
        final BlockState fillBlock = config.fillProvider.getState( random, buildPos );
        final int radSqr = radius * radius;
        
        // Build the pit
        for( int x = centerX - radius; x <= centerX + radius; x++ ) {
            for( int y = centerY; y > centerY - depth; y-- ) {
                for( int z = centerZ - radius; z <= centerZ + radius; z++ ) {
                    int dx = x - centerX;
                    int dz = z - centerZ;
                    
                    if( dx * dx + dz * dz <= radSqr ) {
                        cursor.set( x, y, z );
                        
                        // Building the top cover
                        if( y == buildPos.getY() ) {
                            safeSetUnstableBlock( level, cursor, coverBlock, predicate );
                        }
                        // Building the floor
                        else if( y == centerY - (depth - 1) ) {
                            safeSetBlock( level, cursor, floorBlock, predicate );
                        }
                        // Fill the rest with the configured filling
                        else {
                            safeSetBlock( level, cursor, fillBlock, predicate );
                        }
                    }
                }
            }
        }
        PitfallTrapType type = PitfallTrapType.getFromID( config.pitfallTrapSettings.pitfallTrapId() );
        
        if( type != null ) {
            // Generate debug marker
            debugMarkerIfEnabled( level, origin, type.getConfig( level.getLevel() ) );
        }
        return true;
    }
    
    
    // Hello :D
    // (/ o .o )/  _|_|_|_ <-- this is a spike trap
}