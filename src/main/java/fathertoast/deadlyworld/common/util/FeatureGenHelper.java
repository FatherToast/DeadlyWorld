package fathertoast.deadlyworld.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

public class FeatureGenHelper {
    public static void placeChest( BlockPos chestPos, Level level, RandomSource random, ResourceLocation lootTable ) {
        level.setBlock( chestPos, StructurePiece.reorient( level, chestPos, Blocks.CHEST.defaultBlockState() ), 18 );
        RandomizableContainerBlockEntity.setLootTable( level, random, chestPos, lootTable );
    }
}