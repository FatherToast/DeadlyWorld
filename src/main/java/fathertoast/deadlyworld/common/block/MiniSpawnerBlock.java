package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.tile.spawner.MiniSpawnerTileEntity;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class MiniSpawnerBlock extends DeadlySpawnerBlock implements IWaterLoggable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    private static final VoxelShape[] SHAPES = {
            Block.box( 5.0, 9.0, 5.0, 12.0, 16.0, 12.0 ), // DOWN
            Block.box( 5.0, 0.0, 5.0, 12.0, 8.0, 12.0 ), // UP
            Block.box( 5.0, 4.0, 9.0, 10.0, 12.0, 16.0 ), // NORTH
            Block.box( 5.5, 4.0, 0.0, 10.5, 12.0, 5.0 ), // SOUTH
            Block.box( 11.0, 4.0, 5.5, 16.0, 12.0, 10.5 ), // WEST
            Block.box( 0.0, 4.0, 5.5, 5.0, 12.0, 10.5 ) // EAST
    };

    public MiniSpawnerBlock() {
        super(SpawnerType.MINI);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public VoxelShape getShape( BlockState state, IBlockReader world, BlockPos pos, ISelectionContext selectionContext ) {
        return SHAPES[state.getValue( FACING ).get3DDataValue()];
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new MiniSpawnerTileEntity(); }
    
    @Override
    public BlockRenderType getRenderShape( BlockState state ) { return BlockRenderType.MODEL; }
    
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
    public BlockState getStateForPlacement( BlockItemUseContext useContext ) {
        return this.defaultBlockState().setValue( FACING, useContext.getClickedFace() );
    }
    
    @Override
    protected void createBlockStateDefinition( StateContainer.Builder<Block, BlockState> stateBuilder ) {
        stateBuilder.add( FACING ).add( WATERLOGGED );
    }
}