package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class MiniSpawnerBlock extends DeadlySpawnerBlock implements SimpleWaterloggedBlock {
    
    private static final VoxelShape[] SHAPES = {
            Block.box( 4.0, 8.0, 4.0, 12.0, 16.0, 12.0 ), // DOWN
            Block.box( 4.0, 0.0, 4.0, 12.0, 08.0, 12.0 ), // UP
            Block.box( 4.0, 4.0, 8.0, 12.0, 12.0, 16.0 ), // NORTH
            Block.box( 4.0, 4.0, 0.0, 12.0, 12.0, 08.0 ), // SOUTH
            Block.box( 8.0, 4.0, 4.0, 16.0, 12.0, 12.0 ), // WEST
            Block.box( 0.0, 4.0, 4.0, 08.0, 12.0, 12.0 )  // EAST
    };
    
    public MiniSpawnerBlock() {
        super( SpawnerType.MINI );
        registerDefaultState( stateDefinition.any().setValue( FACING, Direction.UP ).setValue( WATERLOGGED, false ) );
    }
    
    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) { return new MiniSpawnerBlockEntity( pos, state ); }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return getTicker( level, type, DWBlockEntities.MINI_SPAWNER.get() );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public VoxelShape getShape( BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext ) {
        return SHAPES[state.getValue( FACING ).get3DDataValue()];
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public FluidState getFluidState( BlockState state ) {
        return state.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( state );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public BlockState rotate( BlockState state, Rotation rotation ) {
        return state.setValue( FACING, rotation.rotate( state.getValue( FACING ) ) );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public BlockState mirror( BlockState state, Mirror mirror ) {
        return state.rotate( mirror.getRotation( state.getValue( FACING ) ) );
    }
    
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext useContext ) {
        return defaultBlockState().setValue( FACING, useContext.getClickedFace() );
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> stateBuilder ) {
        stateBuilder.add( FACING ).add( WATERLOGGED );
    }
}