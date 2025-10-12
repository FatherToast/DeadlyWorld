package fathertoast.deadlyworld.common.block.trap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SeaMineBlock extends Block implements SimpleWaterloggedBlock {

    private static final VoxelShape SHAPE = Block.box( 3.0D, 3.0D, 3.0D, 13.0D, 13.0D, 13.0D );

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    public SeaMineBlock( Properties properties ) {
        super( properties );
        registerDefaultState( stateDefinition.any().setValue( WATERLOGGED, false ) );
    }

    @Override
    @SuppressWarnings( "deprecation" )
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState( pos );

        return defaultBlockState().setValue( WATERLOGGED, fluidState.is( Fluids.WATER ) );
    }

    @Override
    @SuppressWarnings( "deprecation" )
    public FluidState getFluidState( BlockState state ) {
        return state.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( state );
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( WATERLOGGED ) );
    }
}
