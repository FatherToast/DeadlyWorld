package fathertoast.deadlyworld.common.tile.spawner;

import fathertoast.deadlyworld.common.registry.DWTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class MiniSpawnerTileEntity extends TileEntity implements ITickableTileEntity {
    
    //TODO Maybe we can make this inherit almost everything from the base deadly spawner TE?
    //  I think the only differences are cosmetic, right? In this class, we can just offset (& shrink?) the particle effects by block state,
    //  and the smaller & offset spinning entity will be handled in the TE renderer.
    
    public MiniSpawnerTileEntity() {
        super( DWTileEntities.MINI_SPAWNER.get() );
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public void tick() {
    
    }
}