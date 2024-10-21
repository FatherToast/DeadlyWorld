package fathertoast.deadlyworld.common.world.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Used to separate trap logic from specific implementation.
 * Usually, the entity or block entity that acts as the trap will implement this interface.
 */
public interface ITrapObject {
    void broadcastEvent( BaseTrap trap, Level level, BlockPos pos, int eventId );
    
    void spawnEffectParticle( BaseTrap trap, Level level, BlockPos pos );
}