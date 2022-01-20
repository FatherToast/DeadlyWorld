package fathertoast.deadlyworld.common.feature.features;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.SpawnerConfig;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import fathertoast.deadlyworld.common.util.FeatureGenHelper;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class SpawnerFeature extends Feature<NoFeatureConfig> {
    
    private final Supplier<DeadlySpawnerBlock> spawnerBlockSupplier;
    
    public SpawnerFeature( Codec<NoFeatureConfig> codec, Supplier<DeadlySpawnerBlock> spawnerBlock ) {
        super( codec );
        this.spawnerBlockSupplier = spawnerBlock;
    }
    
    @Override
    public boolean place( ISeedReader seedReader, ChunkGenerator chunkGenerator, Random random, BlockPos origin, NoFeatureConfig featureConfig ) {
        SpawnerType spawnerType = this.spawnerBlockSupplier.get().getSpawnerType();
        SpawnerConfig.SpawnerTypeCategory spawnerConfig = spawnerType.getFeatureConfig( this.getDimensionConfig(seedReader) );
        double countPerChunk = spawnerConfig.countPerChunk.get();
        
        int placementCount = (int) countPerChunk;
        double additionalCountChance = countPerChunk - placementCount;
        
        if( additionalCountChance > 0.0 && random.nextDouble() < additionalCountChance ) {
            ++placementCount;
        }
        
        if( placementCount < 1 )
            return false;
        
        final int minY = spawnerConfig.heights.getMin();
        // TODO - This needs adjusting if anything is to be placed above the spawner block
        final int maxY = Math.min( spawnerConfig.heights.getMax(), seedReader.getHeight() );
        
        if( minY >= maxY ) {
            if( spawnerConfig.debugMarker.get() )
                DeadlyWorld.LOG.error( "Invalid feature heights configured for spawner type \"{}\"", spawnerType.displayName );
            return false;
        }

        for( int i = 0; i < placementCount; i++ ) {
            BlockPos currentPos = this.getFeaturePos( origin, minY, random );
            
            while( currentPos.getY() < maxY ) {

                if( seedReader.isEmptyBlock( currentPos )) {
                    // Just hit an air block, check for valid placement position
                    BlockPos pos = currentPos.immutable();

                    if( this.canBePlaced( seedReader, random, pos ) ) {
                        //TODO - Still relevant?
                        this.placeSpawner( spawnerConfig/*, replaceableBlocks*/, seedReader, random, pos );
                        return true;
                    }
                }
                // Move up one block
                currentPos = currentPos.above();
            }
        }
        return false;
    }

    private void placeSpawner( SpawnerConfig.SpawnerTypeCategory spawnerConfig, ISeedReader seedReader, Random random, BlockPos pos ) {
        DeadlySpawnerBlock spawnerBlock = this.spawnerBlockSupplier.get();
        BlockState spawnerState = spawnerBlock.defaultBlockState();
        SpawnerType spawnerType = spawnerBlock.getSpawnerType();


        // Generate glass pillar if debug marker is enabled
        if( spawnerConfig.debugMarker.get() ) {
            DeadlyWorld.LOG.info( "Generated spawner at: {}", pos );

            BlockPos glassPos = pos.above();
            BlockState glassState = Blocks.GLASS.defaultBlockState();
            
            while( glassPos.getY() < seedReader.getHeight() ) {
                seedReader.setBlock( glassPos, glassState, 18 );
                glassPos = glassPos.above();
            }
        }
        // Place loot chest
        if( random.nextFloat() < spawnerConfig.chestChance.get() ) {
            FeatureGenHelper.placeChest( pos.below(), seedReader, random, spawnerType.getChestLootTable() );
        }

        // Place and initialize spawner
        seedReader.setBlock( pos, spawnerState, 18 );
    }
    
    boolean canBePlaced( ISeedReader world, Random random, BlockPos pos ) {
        boolean airAbove = world.isEmptyBlock( pos.offset(0, 2, 0));
        boolean solidBelow = TrapHelper.isSolidBlock( world, pos.offset( 0, -1, 0 ));
        
        return airAbove && solidBelow;
    }
    
    private BlockPos getFeaturePos( BlockPos origin, int startY, Random random ) {
        return origin.offset( random.nextInt( 8 ) - random.nextInt( 8 ), startY, random.nextInt( 8 ) - random.nextInt( 8 ) );
    }
    
    private DimensionConfigGroup getDimensionConfig( ISeedReader seedReader ) {
        return Config.getDimensionConfigs( seedReader.getLevel() );
    }
}