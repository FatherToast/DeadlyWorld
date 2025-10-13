package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.FloorTrapConfig;
import fathertoast.deadlyworld.common.config.dimension.SeaMineConfig;
import fathertoast.deadlyworld.common.world.levelgen.SeaMineSettings;
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

public class SeaMineFeature extends DeadlyFeature<SeaMineFeature.Configuration> {

    public record Configuration(
            BlockStateProvider mineProvider,
            BlockStateProvider trailProvider,
            SeaMineSettings seaMineSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<SeaMineFeature.Configuration> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "mine_provider" ).forGetter( SeaMineFeature.Configuration::mineProvider ),
                BlockStateProvider.CODEC.fieldOf( "trail_provider" ).forGetter( SeaMineFeature.Configuration::trailProvider ),
                SeaMineSettings.CODEC.fieldOf( "sea_mine" ).forGetter( SeaMineFeature.Configuration::seaMineSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( SeaMineFeature.Configuration::cannotReplace )
        ).apply( instance, SeaMineFeature.Configuration::new ) );
    }

    public SeaMineFeature() { this( SeaMineFeature.Configuration.CODEC ); }

    public SeaMineFeature( Codec<SeaMineFeature.Configuration> codec ) { super( codec ); }

    @Override
    public boolean place( FeaturePlaceContext<SeaMineFeature.Configuration> context ) {
        final SeaMineFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );

        BlockPos origin = context.origin();

        // Place the mine
        BlockState seaMine = config.mineProvider.getState( random, context.origin() );
        setBlock( level, context.origin(), seaMine );

        if( seaMine.getBlock() instanceof SeaMineBlock seaMineBlock ) {
            final SeaMineConfig.SeaMineCategory seaMineConfig = seaMineBlock.getSeaMineType().getFeatureConfig( Config.getDimensionConfigs( level.getLevel() ) );
            debugMarkerIfEnabled( level, origin, seaMineConfig );
        }
        return true;
    }
}
