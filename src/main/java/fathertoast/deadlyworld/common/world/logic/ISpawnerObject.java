package fathertoast.deadlyworld.common.world.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * Used to separate spawner logic from specific implementation.
 * Usually, the entity or block entity that acts as the spawner will implement this interface.
 */
public interface ISpawnerObject {

    /**
     * Called from {@link ProgressiveDelaySpawner#doSpawn(ServerLevel, BlockPos)}
     * after remaining spawns have been calculated. This is a good place to do
     * any manual server-to-client sync.<br><br>
     * Only relevant if the implementing class is an entity.
     */
    default void entitySync( ProgressiveDelaySpawner spawner, ServerLevel level, BlockPos pos ) { }

    void broadcastEvent( ProgressiveDelaySpawner spawner, Level level, BlockPos pos, int eventId );
    
    void spawnEffectParticle( ProgressiveDelaySpawner spawner, Level level, BlockPos pos );
}