package fathertoast.deadlyworld.common.tile.spawner;

import fathertoast.deadlyworld.common.registry.DWTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class DeadlyTrapTileEntity extends TileEntity implements ITickableTileEntity {

    public DeadlyTrapTileEntity() {
        super(DWTileEntities.DEADLY_TRAP.get());
    }

    @Override
    public void tick() {

    }
}
