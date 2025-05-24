package fathertoast.deadlyworld.common.world.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Used to separate tower dispenser logic from specific implementation.
 * Usually, the entity or block entity that acts as the tower dispenser will implement this interface.
 */
public interface ITowerObject {

    void broadcastEvent( BaseTower tower, Level level, BlockPos pos, int eventId );

    void spawnEffectParticle( BaseTower trap, Level level, BlockPos pos );
}