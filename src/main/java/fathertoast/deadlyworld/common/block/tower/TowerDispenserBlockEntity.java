package fathertoast.deadlyworld.common.block.tower;

import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import fathertoast.deadlyworld.common.world.logic.ITrapObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TowerDispenserBlockEntity extends BlockEntity implements ITrapObject {

    public TowerDispenserBlockEntity( BlockPos pos, BlockState state ) {
        super( DWBlockEntities.DEADLY_TRAP.get(), pos, state );
    }

    public static void clientTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, TowerDispenserBlockEntity blockEntity ) {
        // TODO
    }

    public static void serverTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, TowerDispenserBlockEntity blockEntity ) {
        // TODO
    }

    @Override
    public void broadcastEvent( BaseTrap trap, Level level, BlockPos pos, int eventId ) { }

    @Override
    public void spawnEffectParticle( BaseTrap trap, Level level, BlockPos pos ) { }
}
