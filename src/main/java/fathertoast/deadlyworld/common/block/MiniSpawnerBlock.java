package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.tile.spawner.MiniSpawnerTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class MiniSpawnerBlock extends ContainerBlock implements IWaterLoggable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape[] SHAPES = {
            Block.box(5.0D, 9.0D, 5.0D, 12.0D, 16.0D, 12.0D), // DOWN
            Block.box(5.0D, 0.0D, 5.0D, 12.0D, 8.0D, 12.0D), // UP
            Block.box(5.0D, 4.0D, 9.0D, 10.0D, 12.0D, 16.0D), // NORTH
            Block.box(5.5D, 4.0D, 0.0D, 10.5D, 12.0D, 5.0D), // SOUTH
            Block.box(11.0D, 4.0D, 5.5D, 16.0D, 12.0D, 10.5D), // WEST
            Block.box(0.0D, 4.0D, 5.5D, 5.0D, 12.0D, 10.5D) // EAST
    };


    public MiniSpawnerBlock() {
        super(AbstractBlock.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(5.0F)
                .harvestTool(ToolType.PICKAXE)
                .noOcclusion()
                .noDrops()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(WATERLOGGED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext selectionContext) {
        return SHAPES[state.getValue(FACING).get3DDataValue()];
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new MiniSpawnerTileEntity();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext useContext) {
        return this.defaultBlockState().setValue(FACING, useContext.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING).add(WATERLOGGED);
    }
}
