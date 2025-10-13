package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.SeaMineConfig;
import fathertoast.deadlyworld.common.world.levelgen.SeaMineSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class SeaMineFeature extends DeadlyFeature<SeaMineFeature.Configuration> {

    public record Configuration(
            BlockStateProvider mineProvider,
            BlockStateProvider trailProvider,
            SeaMineSettings seaMineSettings
    ) implements FeatureConfiguration {
        public static final Codec<SeaMineFeature.Configuration> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "mine_provider" ).forGetter( SeaMineFeature.Configuration::mineProvider ),
                BlockStateProvider.CODEC.fieldOf( "trail_provider" ).forGetter( SeaMineFeature.Configuration::trailProvider ),
                SeaMineSettings.CODEC.fieldOf( "sea_mine" ).forGetter( SeaMineFeature.Configuration::seaMineSettings )
        ).apply( instance, SeaMineFeature.Configuration::new ) );
    }

    public SeaMineFeature() { this( SeaMineFeature.Configuration.CODEC ); }

    public SeaMineFeature( Codec<SeaMineFeature.Configuration> codec ) { super( codec ); }

    @Override
    public boolean place( FeaturePlaceContext<SeaMineFeature.Configuration> context ) {
        final SeaMineFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final SeaMineSettings settings = config.seaMineSettings();

        BlockPos origin = context.origin();
        int waterAbove = 0;

        // Count how many blocks of water sources are above
        // in an uninterrupted chain
        for ( int yOffset = 1;; yOffset++ ) {
            BlockState state = level.getBlockState( origin.above( yOffset ) );

            if ( state.is( Blocks.WATER ) && state.getFluidState().isSource() ) {
                ++waterAbove;
                continue;
            }
            break;
        }
        // The total height of blocks we are building,
        // counting chains and the mine

        final int minDist = settings.distanceFromBottom().getMinValue();
        final int maxDist = settings.distanceFromBottom().getMaxValue();
        final int totalHeight = Math.min( (maxDist - minDist) + random.nextInt( minDist + 1 ), waterAbove );

        if ( totalHeight < minDist ) {
            return false;
        }

        // Place chains
        for ( int yOffset = 0; yOffset < totalHeight; yOffset++ ) {
            BlockPos pos = origin.above( yOffset );
            setBlock( level, pos, config.trailProvider.getState( random, pos ) );
        }

        // Place the mine
        BlockPos minePos = origin.above( totalHeight );
        BlockState seaMine = config.mineProvider.getState( random, minePos );
        setBlock( level, minePos, seaMine );

        // Generate debug marker
        if( seaMine.getBlock() instanceof SeaMineBlock seaMineBlock ) {
            final SeaMineConfig.SeaMineCategory seaMineConfig = seaMineBlock.getSeaMineType().getFeatureConfig( Config.getDimensionConfigs( level.getLevel() ) );
            debugMarkerIfEnabled( level, minePos, seaMineConfig );
        }
        return true;
    }
}
