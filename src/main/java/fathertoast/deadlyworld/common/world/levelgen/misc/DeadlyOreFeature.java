package fathertoast.deadlyworld.common.world.levelgen.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.config.dimension.VeinConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantFloatProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.BitSet;
import java.util.List;
import java.util.function.Function;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.levelgen.feature.OreFeature}
 * to support config-driven data.
 */
public class DeadlyOreFeature extends DeadlyFeature<DeadlyOreFeature.Configuration> {
    public record Configuration(
            List<OreConfiguration.TargetBlockState> targetStates,
            IntProvider size,
            FloatProvider exposureDiscardChance
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                Codec.list( OreConfiguration.TargetBlockState.CODEC ).fieldOf( "targets" ).forGetter( Configuration::targetStates ),
                IntProvider.CODEC.fieldOf( "size" ).forGetter( Configuration::size ),
                FloatProvider.CODEC.fieldOf( "discard_chance_on_air_exposure" ).forGetter( Configuration::exposureDiscardChance )
        ).apply( instance, Configuration::new ) );
        
        public static Configuration of( VeinConfig.VeinCategory config, List<OreConfiguration.TargetBlockState> targetStates ) {
            return new Configuration(
                    targetStates,
                    ConfigConstantIntProvider.of( config.size ),
                    ConfigConstantFloatProvider.of( config.exposureDiscardChance )
            );
        }
    }
    
    public DeadlyOreFeature() { this( Configuration.CODEC ); }
    
    public DeadlyOreFeature( Codec<Configuration> codec ) { super( codec ); }
    
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        
        final int size = config.size.sample( random );
        final float r = size / 8.0F;
        
        float angle = random.nextFloat() * (float) Math.PI;
        double x1 = pos.getX() + Math.sin( angle ) * r;
        double x2 = pos.getX() - Math.sin( angle ) * r;
        double z1 = pos.getZ() + Math.cos( angle ) * r;
        double z2 = pos.getZ() - Math.cos( angle ) * r;
        double y1 = pos.getY() + random.nextInt( 3 ) - 2;
        double y2 = pos.getY() + random.nextInt( 3 ) - 2;
        
        int i = Mth.ceil( (r + 1.0F) / 2.0F );
        int x = pos.getX() - Mth.ceil( r ) - i;
        int y = pos.getY() - 2 - i;
        int z = pos.getZ() - Mth.ceil( r ) - i;
        int xLen = 2 * (Mth.ceil( r ) + i);
        int yLen = 2 * (2 + i);
        
        for( int xi = x; xi <= x + xLen; ++xi ) {
            for( int zi = z; zi <= z + xLen; ++zi ) {
                if( y <= level.getHeight( Heightmap.Types.OCEAN_FLOOR_WG, xi, zi ) ) {
                    return doPlace( level, random, config, size,
                            x1, x2, z1, z2, y1, y2,
                            x, y, z, xLen, yLen );
                }
            }
        }
        
        return false;
    }
    
    protected boolean doPlace( WorldGenLevel level, RandomSource random, Configuration config, int size,
                               double x1, double x2, double z1, double z2, double y1, double y2,
                               int xo, int yo, int zo, int xLen, int yLen ) {
        int blocksPlaced = 0;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final float exposureDiscardChance = config.exposureDiscardChance.sample( random );
        BitSet checked = new BitSet( xLen * yLen * xLen );
        double[] data = new double[size * 4];
        
        for( int n = 0; n < size; n++ ) {
            int n4 = n * 4;
            float f = (float) n / (float) size;
            double xn = Mth.lerp( f, x1, x2 );
            double yn = Mth.lerp( f, y1, y2 );
            double zn = Mth.lerp( f, z1, z2 );
            double d3 = random.nextDouble() * size / 16.0;
            double wn = ((Mth.sin( (float) Math.PI * f ) + 1.0F) * d3 + 1.0) / 2.0;
            data[n4] = xn;
            data[n4 + 1] = yn;
            data[n4 + 2] = zn;
            data[n4 + 3] = wn;
        }
        
        for( int n = 0; n < size - 1; ++n ) {
            int n4 = n * 4;
            if( !(data[n4 + 3] <= 0.0) ) {
                for( int i = n + 1; i < size; i++ ) {
                    int i4 = i * 4;
                    if( !(data[i4 + 3] <= 0.0) ) {
                        double xn = data[n4] - data[i4];
                        double yn = data[n4 + 1] - data[i4 + 1];
                        double zn = data[n4 + 2] - data[i4 + 2];
                        double wn = data[n4 + 3] - data[i4 + 3];
                        if( wn * wn > xn * xn + yn * yn + zn * zn ) {
                            if( wn > 0.0 ) {
                                data[i4 + 3] = -1.0;
                            }
                            else {
                                data[n4 + 3] = -1.0;
                            }
                        }
                    }
                }
            }
        }
        
        try( BulkSectionAccess bulkSection = new BulkSectionAccess( level ) ) {
            for( int n = 0; n < size; n++ ) {
                int n4 = n * 4;
                double wn = data[n4 + 3];
                if( wn >= 0.0 ) {
                    double xn = data[n4];
                    double yn = data[n4 + 1];
                    double zn = data[n4 + 2];
                    int xMin = Math.max( Mth.floor( xn - wn ), xo );
                    int yMin = Math.max( Mth.floor( yn - wn ), yo );
                    int zMin = Math.max( Mth.floor( zn - wn ), zo );
                    int xMax = Math.max( Mth.floor( xn + wn ), xMin );
                    int yMax = Math.max( Mth.floor( yn + wn ), yMin );
                    int zMax = Math.max( Mth.floor( zn + wn ), zMin );
                    
                    for( int x = xMin; x <= xMax; x++ ) {
                        double wX = (x + 0.5 - xn) / wn;
                        if( wX * wX < 1.0 ) {
                            for( int y = yMin; y <= yMax; y++ ) {
                                double wY = (y + 0.5 - yn) / wn;
                                if( wX * wX + wY * wY < 1.0 && !level.isOutsideBuildHeight( y ) ) {
                                    for( int z = zMin; z <= zMax; z++ ) {
                                        double wZ = (z + 0.5 - zn) / wn;
                                        if( wX * wX + wY * wY + wZ * wZ < 1.0 ) {
                                            int zyx = x - xo + (y - yo) * xLen + (z - zo) * xLen * yLen;
                                            if( !checked.get( zyx ) ) {
                                                checked.set( zyx );
                                                cursor.set( x, y, z );
                                                if( level.ensureCanWrite( cursor ) ) {
                                                    LevelChunkSection levelSection = bulkSection.getSection( cursor );
                                                    if( levelSection != null ) {
                                                        int secX = SectionPos.sectionRelative( x );
                                                        int secY = SectionPos.sectionRelative( y );
                                                        int secZ = SectionPos.sectionRelative( z );
                                                        BlockState blockState = levelSection.getBlockState( secX, secY, secZ );
                                                        
                                                        for( OreConfiguration.TargetBlockState targetState : config.targetStates ) {
                                                            if( canPlaceOre( blockState, bulkSection::getBlockState, random, exposureDiscardChance, targetState, cursor ) ) {
                                                                levelSection.setBlockState( secX, secY, secZ,
                                                                        targetState.state, false );
                                                                blocksPlaced++;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return blocksPlaced > 0;
    }
    
    public static boolean canPlaceOre( BlockState blockState, Function<BlockPos, BlockState> blockGetter, RandomSource random, float exposureDiscardChance, OreConfiguration.TargetBlockState targetState, BlockPos.MutableBlockPos cursor ) {
        if( !targetState.target.test( blockState, random ) ) {
            return false;
        }
        else if( shouldSkipAirCheck( random, exposureDiscardChance ) ) {
            return true;
        }
        else {
            return !isAdjacentToAir( blockGetter, cursor );
        }
    }
    
    protected static boolean shouldSkipAirCheck( RandomSource random, float chance ) {
        if( chance <= 0.0F ) {
            return true;
        }
        else if( chance >= 1.0F ) {
            return false;
        }
        else {
            return random.nextFloat() >= chance;
        }
    }
}