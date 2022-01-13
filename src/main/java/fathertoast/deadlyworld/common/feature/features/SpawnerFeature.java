package fathertoast.deadlyworld.common.feature.features;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.SpawnerConfig;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import fathertoast.deadlyworld.common.util.FeatureGenHelper;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
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
       // boolean inWall = true;
        BlockPos currentPos = origin.offset(random.nextInt(3), 0, random.nextInt(3));

        while(currentPos.getY() < 50) {
            if(seedReader.getBlockState(currentPos).isAir(seedReader, currentPos)) {
                // Just hit a floor block, check if the spot is valid for placement
                if(this.canBePlaced(seedReader, random, currentPos)) {
                    this.placeSpawner(getDimensionConfig(seedReader)/*, replaceableBlocks*/, seedReader, random, currentPos);
                    return true;
                }
            }
            currentPos = currentPos.above();
        }
        return false;
    }

    private void placeSpawner(DimensionConfigGroup dimConfig, ISeedReader seedReader, Random random, BlockPos pos) {
        DeadlySpawnerBlock spawnerBlock = this.spawnerBlockSupplier.get();
        BlockState spawnerState = spawnerBlock.defaultBlockState();
        SpawnerType spawnerType = spawnerBlock.getSpawnerType();
        SpawnerConfig.SpawnerTypeCategory spawnerTypeCategory = spawnerType.getFeatureConfig(dimConfig);

        seedReader.setBlock(pos, spawnerState, 11);
        DeadlySpawnerBlock.initTileEntity(seedReader, spawnerType, pos, dimConfig);

        if(random.nextFloat() < spawnerTypeCategory.chestChance.get()) {
            FeatureGenHelper.placeChest(pos.below(), seedReader, random, spawnerType.getLootTableId());
        }
    }

    boolean canBePlaced(ISeedReader world, Random random, BlockPos pos) {
        boolean airAbove = world.getBlockState(pos.offset( 0, 2, 0 )).isAir(world, pos);
        boolean solidBelow = TrapHelper.isSolidBlock(world, pos.offset( 0, -1, 0));

        return airAbove && solidBelow;
    }

    private DimensionConfigGroup getDimensionConfig(ISeedReader seedReader) {
        return Config.getDimensionConfigs(seedReader.getLevel());
    }
}
