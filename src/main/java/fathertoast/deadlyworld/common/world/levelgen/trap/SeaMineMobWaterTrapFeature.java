package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.trap.PotionTrapBlock;
import fathertoast.deadlyworld.common.world.levelgen.PotionFloorTrapSettings;
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

public class SeaMineMobWaterTrapFeature extends DeadlyFeature<SeaMineMobWaterTrapFeature.Configuration> {
    public record Configuration(
            BlockStateProvider trapProvider,
            PotionFloorTrapSettings trapSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<SeaMineMobWaterTrapFeature.Configuration> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "trap_provider" ).forGetter( SeaMineMobWaterTrapFeature.Configuration::trapProvider ),
                PotionFloorTrapSettings.CODEC.fieldOf( "trap" ).forGetter( SeaMineMobWaterTrapFeature.Configuration::trapSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( SeaMineMobWaterTrapFeature.Configuration::cannotReplace )
        ).apply( instance, SeaMineMobWaterTrapFeature.Configuration::new ) );
    }

    public SeaMineMobWaterTrapFeature() { this( SeaMineMobWaterTrapFeature.Configuration.CODEC ); }

    public SeaMineMobWaterTrapFeature( Codec<SeaMineMobWaterTrapFeature.Configuration> codec ) { super( codec ); }

    @Override
    public boolean place( FeaturePlaceContext<SeaMineMobWaterTrapFeature.Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final PotionFloorTrapFeature.Configuration config = context.config();
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
            // TODO - replace with something less bad
            if( hasNearbyTraps( level, trapPos, 3 ) ) return false;

            // Make sure the trap block can be placed
            if( !predicate.test( level.getBlockState( trapPos ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( trapPos ) != null ) return false;
        }

        // Place the trap
        BlockState trapBlock = config.trapProvider.getState( random, trapPos );
        setBlock( level, trapPos, trapBlock );
        if( trapBlock.getBlock() instanceof PotionTrapBlock) {
            config.trapSettings.initializeTrap( level, trapPos, random );
        }

        return true;
    }
}
