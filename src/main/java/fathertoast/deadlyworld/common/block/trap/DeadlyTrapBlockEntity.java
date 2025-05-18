package fathertoast.deadlyworld.common.block.trap;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.util.TrapHelper;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import fathertoast.deadlyworld.common.world.logic.ITrapObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeadlyTrapBlockEntity extends BlockEntity implements ITrapObject {

    public static final String CAMO_STATE_KEY = "CamoState";

    protected final BaseTrap trapLogic;
    protected final TrapType trapType;

    private BlockState camoState;

    
    public DeadlyTrapBlockEntity( BlockPos pos, BlockState state ) {
        this( DWBlockEntities.DEADLY_TRAP.get(), pos, state );
    }
    
    public DeadlyTrapBlockEntity( BlockEntityType<?> type, BlockPos pos, BlockState state ) {
        super( type, pos, state );
        trapType = ((DeadlyTrapBlock) state.getBlock()).getTrapType();
        trapLogic = ((DeadlyTrapBlock) state.getBlock()).newTrapLogic( this );
    }

    @Override
    public void onLoad() {
        pickCamoState();
    }

    /**
     * Loops through adjacent block states and picks the first suitable one.<br>
     * Directions are shuffled so the same direction isn't necessarily always picked.
     */
    private void pickCamoState() {
        List<Direction> dirs = Arrays.asList( Direction.values() );
        Collections.shuffle( dirs );

        for ( Direction direction : dirs ) {
            BlockPos pos = getBlockPos().relative( direction );
            BlockState neighborState = level.getBlockState( pos );

            if ( neighborState.getBlock() instanceof ICamoTrap ) continue;

            if ( neighborState.isSolidRender( level, pos ) ) {
                camoState = neighborState;
                break;
            }
        }
        if ( camoState == null )
            camoState = Blocks.COBBLESTONE.defaultBlockState();
    }

    @Nonnull
    public BlockState getCamoState() {
        return camoState;
    }

    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        trapLogic.load( level, worldPosition, loadTag );

        if ( loadTag.contains( CAMO_STATE_KEY, Tag.TAG_COMPOUND ) ) {
            camoState = TrapHelper.readBlockState( loadTag.getCompound( CAMO_STATE_KEY ) );

            // NbtUtils#readBlockState returns air default state if something goes wrong.
            // Replace with default cobblestone state if so.
            if ( camoState == Blocks.AIR.defaultBlockState() ) camoState = Blocks.COBBLESTONE.defaultBlockState();
        }
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        trapLogic.save( saveTag );

        if ( camoState != null ) {
            saveTag.put( CAMO_STATE_KEY, NbtUtils.writeBlockState( camoState ) );
        }
    }

    public static void clientTick(Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlyTrapBlockEntity blockEntity ) {
        blockEntity.getTrapLogic().clientTick( level, pos );
    }
    
    public static void serverTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlyTrapBlockEntity blockEntity ) {
        blockEntity.getTrapLogic().serverTick( (ServerLevel) level, pos );
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();

        if ( camoState != null ) {
            tag.put( CAMO_STATE_KEY, NbtUtils.writeBlockState( camoState ) );
        }
        return tag;
    }

    @Override
    public void handleUpdateTag( CompoundTag tag ) {
        super.handleUpdateTag(tag);
    }

    @Override
    public boolean triggerEvent( int id, int type ) {
        return level != null && trapLogic.onEventTriggered( level, id ) ||
                super.triggerEvent( id, type );
    }
    
    @Override
    public boolean onlyOpCanSetNbt() { return true; }
    
    @Override // ITrapObject
    public void broadcastEvent( BaseTrap trap, Level level, BlockPos pos, int eventId ) {
        level.blockEvent( pos, level.getBlockState( pos ).getBlock(), eventId, 0 );
    }
    
    @Override // ITrapObject
    public void spawnEffectParticle( BaseTrap trap, Level level, BlockPos pos ) { }
    
    public BaseTrap getTrapLogic() { return trapLogic; }
}