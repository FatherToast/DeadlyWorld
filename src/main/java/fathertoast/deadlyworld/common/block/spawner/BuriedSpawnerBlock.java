package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.crust.api.lib.DeferredAction;
import fathertoast.crust.api.lib.LevelEventHelper;
import fathertoast.deadlyworld.common.block.IDeadlyBlock;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Turns into a buried spawner any time it receives a block update while not totally buried,
 * such as when you break one of the blocks covering it.
 */
public class BuriedSpawnerBlock extends Block {
    public BuriedSpawnerBlock() {
        super( BlockBehaviour.Properties.copy( Blocks.SPAWNER ) );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState updateShape( BlockState blockState, Direction neighborDir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos ) {
        if( neighborState.isAir() && level instanceof ServerLevel serverLevel ) {
            // Try to initialize the spawner at end of tick; we rely on the caller to actually update the level's block state if they want to
            DeferredAction.queue( () -> BuriedSpawnerBlock.tryInit( serverLevel, pos ) );
            return DWBlocks.spawner( SpawnerType.BURIED ).get().defaultBlockState();
        }
        return super.updateShape( blockState, neighborDir, neighborState, level, pos, neighborPos );
    }
    
    private static boolean tryInit( ServerLevel level, BlockPos pos ) {
        if( level.getBlockState( pos ).getBlock() instanceof IDeadlyBlock deadlyBlock ) {
            deadlyBlock.initDeadly( level, pos, level.getRandom() );
            LevelEventHelper.SMOKE_AND_FLAME.play( level, pos );
        }
        return true;
    }
}