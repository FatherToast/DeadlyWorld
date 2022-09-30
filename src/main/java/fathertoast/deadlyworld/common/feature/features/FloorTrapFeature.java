package fathertoast.deadlyworld.common.feature.features;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.block.FloorTrapBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.FloorTrapConfig;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapType;
import fathertoast.deadlyworld.common.util.FeatureGenHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockStateVariantBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.util.Constants;

import java.util.Random;
import java.util.function.Supplier;

public class FloorTrapFeature extends Feature<NoFeatureConfig> {

    private final Supplier<FloorTrapBlock> floorTrapBlockSupplier;

    public FloorTrapFeature(Codec<NoFeatureConfig> codec, Supplier<FloorTrapBlock> floorTrapBlock ) {
        super( codec );
        this.floorTrapBlockSupplier = floorTrapBlock;
    }

    @Override
    public boolean place(ISeedReader seedReader, ChunkGenerator chunkGenerator, Random random, BlockPos origin, NoFeatureConfig featureConfig ) {
        FloorTrapType trapType = floorTrapBlockSupplier.get().getTrapType();
        FloorTrapConfig.FloorTrapTypeCategory trapConfig = trapType.getFeatureConfig( this.getDimensionConfig(seedReader) );
        double countPerChunk = trapConfig.countPerChunk.get();

        int placementCount = (int) countPerChunk;
        double additionalCountChance = countPerChunk - placementCount;

        if( additionalCountChance > 0.0 && random.nextDouble() < additionalCountChance ) {
            ++placementCount;
        }

        if( placementCount < 1 )
            return false;

        final int minY = trapConfig.heights.getMin();
        final int maxY = Math.min( trapConfig.heights.getMax(), seedReader.getHeight() );

        if( minY >= maxY ) {
            if( trapConfig.debugMarker.get() )
                DeadlyWorld.LOG.error( "Invalid feature heights configured for spawner type \"{}\"", trapType.getDisplayName() );
            return false;
        }

        for( int i = 0; i < placementCount; i++ ) {
            BlockPos currentPos = getFeaturePos( origin, minY, random );

            while( currentPos.getY() < maxY ) {

                if( seedReader.isEmptyBlock( currentPos )) {
                    // Just hit an air block, check for valid placement position
                    BlockPos pos = currentPos.immutable();

                    if( canBePlaced( seedReader, pos ) ) {
                        placeTrap( trapConfig, trapType, seedReader, random, pos );
                        return true;
                    }
                }
                // Move up one block
                currentPos = currentPos.above();
            }
        }
        return false;
    }

    private void placeTrap( FloorTrapConfig.FloorTrapTypeCategory trapConfig, FloorTrapType trapType, ISeedReader seedReader, Random random, BlockPos pos ) {
        FloorTrapBlock trapBlock = floorTrapBlockSupplier.get();
        BlockState state = trapBlock.defaultBlockState();


        // Generate glass pillar if debug marker is enabled
        if( trapConfig.debugMarker.get() ) {
            DeadlyWorld.LOG.info( "Generated floor trap at: {}", pos );

            BlockPos glassPos = pos;
            BlockState glassState = Blocks.GLASS.defaultBlockState();

            while( glassPos.getY() < seedReader.getHeight() ) {
                seedReader.setBlock( glassPos, glassState, 18 );
                glassPos = glassPos.above();
            }
        }
        // Place loot chest
        if( random.nextFloat() < trapConfig.chestChance.get() ) {
            FeatureGenHelper.placeChest( pos.below(2), seedReader, random, trapType.getChestLootTable() );
        }
        // Place floor trap
        seedReader.setBlock( pos.below(), state, 18 );
    }

    public boolean canBePlaced(ISeedReader world, BlockPos position ) {
        BlockPos above = position.above();
        BlockPos below = position.below();
        BlockPos veryAbove = position.offset(0, 2, 0);

              // Lets not try to generate below bedrock
        return below.getY() > 1 && !world.getBlockState( veryAbove ).isCollisionShapeFullBlock( world, veryAbove ) &&
                world.getBlockState( above ).isAir( world, above ) &&
                world.getBlockState( below ).isCollisionShapeFullBlock( world, below );
    }

    private BlockPos getFeaturePos( BlockPos origin, int startY, Random random ) {
        return origin.offset( random.nextInt( 16 ), startY, random.nextInt( 16 ));
    }

    private DimensionConfigGroup getDimensionConfig(ISeedReader seedReader ) {
        return Config.getDimensionConfigs( seedReader.getLevel() );
    }
}
