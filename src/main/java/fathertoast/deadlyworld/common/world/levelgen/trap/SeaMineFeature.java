package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.WaterTrapConfig;
import fathertoast.deadlyworld.common.world.levelgen.SeaMineSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.ticks.TickPriority;

public class SeaMineFeature extends DeadlyFeature<SeaMineFeature.Configuration> {
    public record Configuration(
            BlockStateProvider mineProvider,
            BlockStateProvider trailProvider,
            SeaMineSettings seaMineSettings
    ) implements FeatureConfiguration {
        public static final Codec<SeaMineFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "mine_provider" ).forGetter( SeaMineFeature.Configuration::mineProvider ),
                BlockStateProvider.CODEC.fieldOf( "trail_provider" ).forGetter( SeaMineFeature.Configuration::trailProvider ),
                SeaMineSettings.CODEC.fieldOf( "sea_mine" ).forGetter( SeaMineFeature.Configuration::seaMineSettings )
        ).apply( instance, SeaMineFeature.Configuration::new ) );
    }
    
    public SeaMineFeature() { this( SeaMineFeature.Configuration.CODEC ); }
    
    public SeaMineFeature( Codec<SeaMineFeature.Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<SeaMineFeature.Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final SeaMineSettings settings = config.seaMineSettings();
        final BlockPos.MutableBlockPos cursor = context.origin().mutable();
        
        // Count the max chain length we can build
        final int maxDist = settings.distanceFromBottom().getMaxValue();
        int dist = 0;
        while( dist < maxDist ) {
            BlockState state = level.getBlockState( cursor.move( Direction.UP ) );
            if( state.is( Blocks.WATER ) && state.getFluidState().isSource() || state.is( Blocks.BUBBLE_COLUMN ) )
                dist++;
            else break;
        }
        
        // Pick a valid chain length, if possible
        final int minDist = settings.distanceFromBottom().getMinValue();
        if( dist < minDist ) {
            if( notSubfeature ) return false;
        }
        else {
            dist = minDist + random.nextInt( dist - minDist + 1 );
        }
        
        // Place chains
        cursor.set( context.origin() );
        while( dist > 0 ) {
            safeSetBlock( level, cursor, config.trailProvider, random, null );
            cursor.move( Direction.UP );
            dist--;
        }
        
        // Place the mine
        BlockState seaMine = config.mineProvider.getState( random, cursor );
        setBlock( level, cursor, seaMine );
        // Schedule first tick; Block.onPlace() is not called for blocks placed during world gen
        level.scheduleTick( cursor, seaMine.getBlock(), 20, TickPriority.LOW );
        
        // Generate debug marker
        if( seaMine.getBlock() instanceof SeaMineBlock seaMineBlock ) {
            final WaterTrapConfig.SeaMineCategory seaMineConfig = seaMineBlock.getSeaMineType().getConfig( level.getLevel() );
            debugMarkerIfEnabled( level, cursor, seaMineConfig );
        }
        return true;
    }
}