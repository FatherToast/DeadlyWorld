package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.trap.DeadlyTrapBlock;
import fathertoast.deadlyworld.common.block.trap.PotionTrapBlock;
import fathertoast.deadlyworld.common.world.levelgen.settings.FloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.settings.PotionFloorTrapSettings;
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

public class PotionFloorTrapFeature extends DeadlyFeature<PotionFloorTrapFeature.Configuration> {
    public record Configuration(
            BlockStateProvider trapProvider,
            PotionFloorTrapSettings trapSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<PotionFloorTrapFeature.Configuration> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "trap_provider" ).forGetter( PotionFloorTrapFeature.Configuration::trapProvider ),
                PotionFloorTrapSettings.CODEC.fieldOf( "trap" ).forGetter( PotionFloorTrapFeature.Configuration::trapSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( PotionFloorTrapFeature.Configuration::cannotReplace )
        ).apply( instance, PotionFloorTrapFeature.Configuration::new ) );
    }

    public PotionFloorTrapFeature() { this( PotionFloorTrapFeature.Configuration.CODEC ); }

    public PotionFloorTrapFeature( Codec<PotionFloorTrapFeature.Configuration> codec ) { super( codec ); }

    @Override
    public boolean place( FeaturePlaceContext<PotionFloorTrapFeature.Configuration> context ) {
        final PotionFloorTrapFeature.Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );

        // Offset by one below to place the trap in the ground
        BlockPos below = context.origin().below();

        // Make sure the spawner block at least can be placed
        if( !predicate.test( level.getBlockState( below ) ) ) return false;

        // Place the trap
        BlockState trapBlock = config.trapProvider.getState( random, below );
        setBlock( level, below, trapBlock);

        if( trapBlock.getBlock() instanceof PotionTrapBlock ) {
            config.trapSettings.initializeTrap( level, below, random );
        }
        return true;
    }
}
