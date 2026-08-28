package fathertoast.deadlyworld.api.registry.decoy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/** Marker interface for block entities that can have a decoy. */
public interface IDecoyProvider {
    
    /** The level object of the provider. */
    @Nullable
    Level getProviderLevel();
    
    /** The in-world block position of the provider. */
    BlockPos getProviderPos();
    
    /** The current decoy type of this provider. Can be null. */
    @Nullable
    DecoyType getDecoyType();
    
    /**
     * @return True if a decoy should be rendered.
     * Called internally.
     */
    boolean isDecoyActive();
}
