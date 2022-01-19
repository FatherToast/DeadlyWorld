package fathertoast.deadlyworld.common.tile.spawner;

import fathertoast.deadlyworld.common.registry.DWTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class MiniSpawnerTileEntity extends TileEntity implements ITickableTileEntity {

    public MiniSpawnerTileEntity() {
        super(DWTileEntities.MINI_SPAWNER.get());
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void tick() {

    }
}
