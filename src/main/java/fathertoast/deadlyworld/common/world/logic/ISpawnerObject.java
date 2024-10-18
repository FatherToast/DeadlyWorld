package fathertoast.deadlyworld.common.world.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Used to separate spawner logic from specific implementation.
 * Usually, the entity or block entity that acts as the spawner will implement this interface.
 */
public interface ISpawnerObject {
    void broadcastEvent( ProgressiveDelaySpawner spawner, Level level, BlockPos pos, int eventId );
    
    void spawnEffectParticle( ProgressiveDelaySpawner spawner, Level level, BlockPos pos );
}