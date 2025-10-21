package fathertoast.deadlyworld.common.block.chest;

import fathertoast.deadlyworld.common.block.entity.MiniChestBlockEntity;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class MiniChestBlock extends ChestBlock {
    
    private static final VoxelShape SHAPE = Block.box( 5.0, 0.0, 5.0, 11.0, 6.0, 11.0 );
    
    
    public MiniChestBlock( Properties properties ) {
        super( properties, DWBlockEntities.MINI_CHEST::get );//TODO Make sound type SoundType.WOOD, but mini
        registerDefaultState( stateDefinition.any().setValue( FACING, Direction.NORTH ).setValue( WATERLOGGED, false ) );
    }
    
    @Override
    public BlockState updateShape( BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos ) {
        // Mini chests don't join; just return state.
        return state;
    }
    
    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE;
    }
    
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        FluidState fluidstate = context.getLevel().getFluidState( context.getClickedPos() );
        
        return defaultBlockState().setValue( FACING, facing ).setValue( WATERLOGGED, fluidstate.getType() == Fluids.WATER );
    }
    
    @Nullable
    @Override
    protected Direction candidatePartnerFacing( BlockPlaceContext context, Direction direction ) {
        // Mini chests do not join; return the given direction parameter.
        return direction;
    }
    
    @Override
    public void setPlacedBy( Level level, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack itemStack ) {
        if( itemStack.hasCustomHoverName() ) {
            BlockEntity blockEntity = level.getBlockEntity( pos );
            
            if( blockEntity instanceof MiniChestBlockEntity miniChest ) {
                miniChest.setCustomName( itemStack.getHoverName() );
            }
        }
    }
    
    @Override
    public void onRemove( BlockState state, Level level, BlockPos pos, BlockState newState, boolean notifyNeighbors ) {
        if( !state.is( newState.getBlock() ) ) {
            BlockEntity blockEntity = level.getBlockEntity( pos );
            
            if( blockEntity instanceof Container container ) {
                Containers.dropContents( level, pos, container );
                level.updateNeighbourForOutputSignal( pos, this );
            }
            super.onRemove( state, level, pos, newState, notifyNeighbors );
        }
    }
    
    @Nullable
    @Override
    public MenuProvider getMenuProvider( BlockState state, Level level, BlockPos pos ) {
        BlockEntity blockEntity = level.getExistingBlockEntity( pos );
        
        return blockEntity instanceof MenuProvider menuProvider ? menuProvider : null;
    }
    
    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
        return new MiniChestBlockEntity( pos, state );
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return level.isClientSide ? createTickerHelper( type, blockEntityType(), ChestBlockEntity::lidAnimateTick ) : null;
    }
    
    // Chest is smol and is not blocked by above blocks, only meows
    public static boolean isChestBlockedAt( LevelAccessor level, BlockPos pos ) {
        return isCatSittingOnChest( level, pos );
    }
    
    private static boolean isCatSittingOnChest( LevelAccessor level, BlockPos pos ) {
        List<Cat> catList = level.getEntitiesOfClass( Cat.class, new AABB(
                pos.getX(),
                pos.getY() + 0.5,
                pos.getZ(),
                pos.getX() + 1,
                pos.getY() + 1,
                pos.getZ() + 1
        ) );
        
        if( !catList.isEmpty() ) {
            for( Cat cat : catList ) {
                if( cat.isInSittingPose() ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState mirror( BlockState state, Mirror mirror ) {
        return state.rotate( mirror.getRotation( state.getValue( FACING ) ) );
    }
    
    @Override
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        BlockEntity blockEntity = level.getBlockEntity( pos );
        
        if( blockEntity instanceof MiniChestBlockEntity miniChest ) {
            miniChest.recheckOpen();
        }
    }
}