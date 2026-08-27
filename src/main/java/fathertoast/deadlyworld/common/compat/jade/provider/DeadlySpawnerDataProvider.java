package fathertoast.deadlyworld.common.compat.jade.provider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class DeadlySpawnerDataProvider implements IServerDataProvider<BlockAccessor> {
    
    @Override
    public void appendServerData( CompoundTag compoundTag, BlockAccessor blockAccessor ) {
    
    }
    
    @Override
    public ResourceLocation getUid() {
        return null;
    }
}
