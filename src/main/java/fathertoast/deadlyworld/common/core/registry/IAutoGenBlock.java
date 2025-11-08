package fathertoast.deadlyworld.common.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * Represents a block that can be automatically generated based on another block (the "origin" block).
 * It will copy its block state and inventory models from the origin block; the rest depends on implementation.
 * <p>
 * If you implement this interface, it is expected that you extend {@link Block}.
 */
public interface IAutoGenBlock {
    /** @return This auto-generated block's block state definition. */
    StateDefinition<Block, BlockState> getBlockStateDefinition();
    
    /** @return The origin block. */
    Block getOriginBlock();
    
    /** @return The origin block's resource location. Used for model lookups. */
    ResourceLocation getOriginBlockLocation();
    
    /** @return The auto-generated block state corresponding to a specific origin block state. */
    BlockState toAutoGen( BlockState originState );
    
    /** @return The origin block state corresponding to a specific auto-generated block state. */
    BlockState toOrigin( BlockState autoGenState );
}