package fathertoast.deadlyworld.common.item;

import fathertoast.deadlyworld.common.block.entity.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.util.MimicHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * An item that can be used to turn non-chest compatible blocks into mimics with right click.
 */
public class MimicCoreItem extends SimpleFoiledItem {
    
    public MimicCoreItem( Properties builder ) { super( builder ); }
    
    @Override
    public InteractionResult useOn( UseOnContext context ) {
        Level level = context.getLevel();
        
        final BlockPos pos = context.getClickedPos();
        final BlockState blockState = level.getBlockState( pos );
        
        boolean success = false;
        if( blockState.is( Blocks.JUKEBOX ) ) {
            // TODO Temporary; would be fun to make the mimic trigger on next disc played
            //  and have the mimic chase down the player with sweet tunes
            if( !(level instanceof ServerLevel serverLevel) ||
                    MimicHelper.spawnJukeboxMimicFrom( serverLevel, pos, false ) ) {
                success = true;
            }
        }
        else if( blockState.getBlock() instanceof DeadlySpawnerBlock ) {
            if( level.isClientSide ) success = true;
            else if( level.getBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity spawnerBlockEntity &&
                    !spawnerBlockEntity.getSpawnerLogic().isMimic() ) {
                spawnerBlockEntity.getSpawnerLogic().setMimic( true );
                spawnerBlockEntity.setChanged();
                level.sendBlockUpdated( pos, blockState, blockState, DeadlySpawnerBlock.UPDATE_ALL );
                level.gameEvent( context.getPlayer(), GameEvent.BLOCK_CHANGE, pos );
                success = true;
            }
        }
        
        if( success ) {
            if( level.isClientSide ) return InteractionResult.SUCCESS;
            
            context.getItemInHand().shrink( 1 );
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}