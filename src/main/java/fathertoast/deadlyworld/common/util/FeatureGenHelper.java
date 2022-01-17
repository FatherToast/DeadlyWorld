package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import java.util.Random;

public class FeatureGenHelper {
    
    public static final int UPDATE_FLAGS = 18;
    
    public static void placeChest( BlockPos chestPos, ISeedReader world, Random random, ResourceLocation lootTable ) {
        world.setBlock( chestPos, Blocks.CHEST.defaultBlockState(), 4 );//TODO give appropriate direction
        TileEntity chestInventory = world.getBlockEntity( chestPos );
        
        if( chestInventory instanceof ChestTileEntity ) {
            ((ChestTileEntity) chestInventory).setLootTable( lootTable, random.nextLong() );
        }
        else {
            DeadlyWorld.LOG.error( "Failed to fetch chest tile entity at [{}]! Expect an empty chest. :(", chestPos );
        }
    }
}