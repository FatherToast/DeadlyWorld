package fathertoast.deadlyworld.common.tile.spawner;

import fathertoast.deadlyworld.common.registry.DWTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class FloorTrapTileEntity extends TileEntity implements ITickableTileEntity {
    
    public FloorTrapTileEntity() {
        super( DWTileEntities.FLOOR_TRAP.get() );
    }
    
    @Override
    public void tick() {
    
    }
}