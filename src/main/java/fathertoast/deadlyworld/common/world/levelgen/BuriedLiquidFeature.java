package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.EnvHazardConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.datagen.worldgen.PlacementBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;

public class BuriedLiquidFeature extends DeadlyFeature<BuriedLiquidFeature.Configuration> {
    public record Configuration(
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<BuriedLiquidFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( BuriedLiquidFeature.Configuration::cannotReplace )
        ).apply( instance, BuriedLiquidFeature.Configuration::new ) );
    }

    public BuriedLiquidFeature( ) {
        super( BuriedLiquidFeature.Configuration.CODEC );
    }

    public BuriedLiquidFeature( Codec<Configuration> codec ) {
        super( codec );
    }

    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final WorldGenLevel level = context.level();
        final RandomSource random = context.random();
        final EnvHazardConfig.BuriedLiquidsCategory category = Config.getDimensionConfigs(level.getLevel().dimension()).ENV_HAZARDS.BURIED_LIQUIDS;
        final Predicate<BlockState> predicate = (state) -> !state.is( context.config().cannotReplace );

        try {
            for ( RegistryValueEntry<Block> entry : category.buriedLiquids.get().getEntries() ) {
                final BlockState liquid = ForgeRegistries.BLOCKS.getValue(entry.REG_KEY).defaultBlockState();
                final int minY = Math.max( (int) entry.VALUES[0], level.getMinBuildHeight() );
                final int maxY = Math.min( (int) entry.VALUES[1], level.getMaxBuildHeight() );
                final boolean areYEqual = minY >= maxY;
                final int placementTries = (int) entry.VALUES[2];

                final ChunkPos chunkPos = new ChunkPos( context.origin() );
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                for ( int i = 0; i < placementTries; i++ ) {
                    pos.set(
                            chunkPos.getMinBlockX() + random.nextInt(chunkPos.getMaxBlockX() - chunkPos.getMinBlockX() ),
                            areYEqual ? minY : Mth.randomBetweenInclusive( random, minY, maxY ),
                            chunkPos.getMinBlockZ() + random.nextInt(chunkPos.getMaxBlockZ() - chunkPos.getMinBlockZ() )
                    );
                    boolean canPlace = true;

                    for ( Direction dir : Direction.values() ) {
                        BlockPos neighborPos = pos.relative( dir );
                        if ( level.getExistingBlockEntity( neighborPos ) != null || !level.getBlockState( neighborPos ).isSolidRender( level, neighborPos ) ) {
                            canPlace = false;
                            break;
                        }
                    }

                    if ( canPlace ) {
                        safeSetBlock( level, pos, liquid, predicate );
                    }
                }
            }
            return true;
        }
        catch ( Exception e ) {
            DeadlyWorld.LOG.error( "Encountered a problem while generating buried liquid!" );
            e.printStackTrace();
            return false;
        }
    }
}
