package fathertoast.deadlyworld.common.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 *
 */
public interface IAutoGenBlock {
    /** @return This auto-generated block's block state definition. The Block.class implementation is fine as-is. */
    StateDefinition<Block, BlockState> getStateDefinition();
    
    /** @return The origin block's resource location. Used for model lookups. */
    ResourceLocation getOriginBlockLocation();
    
    /** @return The auto-generated block state corresponding to a specific origin block state. */
    BlockState toAutoGen( BlockState originState );
    
    /** @return The origin block state corresponding to a specific auto-generated block state. */
    BlockState toOrigin( BlockState autoGenState );
}