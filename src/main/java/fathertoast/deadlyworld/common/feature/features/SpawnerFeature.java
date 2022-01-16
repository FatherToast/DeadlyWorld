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
import net.minecraft.world.gen.feature.BlockBlobFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.function.Supplier;

public class SpawnerFeature extends Feature<NoFeatureConfig> {

    private final Supplier<DeadlySpawnerBlock> spawnerBlockSupplier;

    public SpawnerFeature(Codec<NoFeatureConfig> codec, Supplier<DeadlySpawnerBlock> spawnerBlock) {
        super(codec);
        this.spawnerBlockSupplier = spawnerBlock;
    }

    @Override
    public boolean place(ISeedReader seedReader, ChunkGenerator chunkGenerator, Random random, BlockPos origin, NoFeatureConfig featureConfig) {
        DimensionConfigGroup dimensionConfig = this.getDimensionConfig(seedReader);
        SpawnerType spawnerType = this.spawnerBlockSupplier.get().getSpawnerType();
        SpawnerConfig.SpawnerTypeCategory spawnerConfig = spawnerType.getFeatureConfig(dimensionConfig);
        double countPerChunk = spawnerConfig.countPerChunk.get();

        int placementCount = (int)countPerChunk;
        double additionalCountChance = countPerChunk - placementCount;

        if (additionalCountChance > 0.0 && random.nextDouble() < additionalCountChance) {
            ++placementCount;
        }

        if (placementCount < 1)
            return false;

        final int minY = spawnerConfig.heights.getMin();
        // TODO - This needs adjusting if anything is to be placed above the spawner block
        final int maxY = Math.min(spawnerConfig.heights.getMax(), seedReader.getHeight());

        if (minY >= maxY) {
            if (spawnerConfig.debugMarker.get())
                DeadlyWorld.LOG.error("Invalid feature heights configured for spawner type \"{}\"", spawnerType.displayName );
            return false;
        }

        for (int i = 0; i < placementCount; i++) {
            BlockPos currentPos = this.getFeaturePos(origin, minY, random);

            while(currentPos.getY() < maxY) {
                if(seedReader.getBlockState(currentPos).isAir(seedReader, currentPos)) {
                    // Just hit an air block, check for valid placement position
                    if(this.canBePlaced(seedReader, random, currentPos)) {
                                                                        //TODO - Still relevant?
                        this.placeSpawner(dimensionConfig, spawnerConfig/*, replaceableBlocks*/, seedReader, random, currentPos);
                        return true;
                    }
                }
                // Move up one block
                currentPos = currentPos.above();
            }
        }
        return false;
    }

    private void placeSpawner(DimensionConfigGroup dimConfig, SpawnerConfig.SpawnerTypeCategory spawnerConfig, ISeedReader seedReader, Random random, BlockPos pos) {
        DeadlySpawnerBlock spawnerBlock = this.spawnerBlockSupplier.get();
        BlockState spawnerState = spawnerBlock.defaultBlockState();
        SpawnerType spawnerType = spawnerBlock.getSpawnerType();

        // Generate glass pillar if debug marker is enabled
        if (spawnerConfig.debugMarker.get()) {
            DeadlyWorld.LOG.info("Generated spawner at: {}", pos);

            BlockPos glassPos = pos.above();
            BlockState glassState = Blocks.GLASS.defaultBlockState();

            while(glassPos.getY() < seedReader.getHeight()) {
                seedReader.setBlock(glassPos, glassState, 11);
                glassPos = glassPos.above();
            }
        }
        // Place and initialize spawner
        seedReader.setBlock(pos, spawnerState, 11);
        DeadlySpawnerBlock.initTileEntity(seedReader, spawnerType, pos, dimConfig);

        // Place loot chest
        if(random.nextFloat() < spawnerConfig.chestChance.get()) {
            FeatureGenHelper.placeChest(pos.below(), seedReader, random, spawnerType.getLootTableId());
        }
    }

    boolean canBePlaced(ISeedReader world, Random random, BlockPos pos) {
        boolean airAbove = world.getBlockState(pos.offset( 0, 2, 0 )).isAir(world, pos);
        boolean solidBelow = TrapHelper.isSolidBlock(world, pos.offset( 0, -1, 0));

        return airAbove && solidBelow;
    }

    private BlockPos getFeaturePos(BlockPos origin, int startY, Random random) {
        return origin.offset(random.nextInt(8) - random.nextInt(8), startY, random.nextInt(8) - random.nextInt(8));
    }

    private DimensionConfigGroup getDimensionConfig(ISeedReader seedReader) {
        return Config.getDimensionConfigs(seedReader.getLevel());
    }
}