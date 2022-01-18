package fathertoast.deadlyworld.common.util;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.Random;

public class FeatureGenHelper {
    

    public static void placeChest( BlockPos chestPos, ISeedReader world, Random random, ResourceLocation lootTable ) {
        world.setBlock( chestPos, StructurePiece.reorient(world, chestPos, Blocks.CHEST.defaultBlockState()), 18 );
        LockableLootTileEntity.setLootTable( world, random, chestPos, lootTable );
    }
}