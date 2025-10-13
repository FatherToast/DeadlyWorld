package fathertoast.deadlyworld.common.block.sea_mine;

import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class SeaMineBlock extends Block implements SimpleWaterloggedBlock {

    private static final VoxelShape SHAPE =
            Block.box( 3.0D, 3.0D, 3.0D, 13.0D, 13.0D, 13.0D );


    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ARMED = BooleanProperty.create( "armed" );

    private final SeaMineType type;

    public SeaMineBlock( SeaMineType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.SPAWNER ) ) );
        registerDefaultState( stateDefinition.any().setValue( WATERLOGGED, false ).setValue( ARMED, false ) );
        this.type = type;
    }

    public SeaMineType getSeaMineType() {
        return type;
    }

    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE;
    }

    // TODO - Make an easy-to-override method for variant mines
    @Override
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        level.scheduleTick( pos, this, 20, TickPriority.LOW );

        boolean armed = state.getValue( ARMED );

        // We are armed, explotando time
        if ( armed ) {
            level.removeBlock( pos, false );
            level.explode(
                    null,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    3.5F,
                    Level.ExplosionInteraction.BLOCK
            );
        }
        // Check for a nearby player in survival or adventure mode
        else {
            List<Player> nearbyPlayers = level.getEntitiesOfClass( Player.class, new AABB( pos ).inflate( 1.0F ) );
            boolean validNearbyTarget = false;

            for ( Player player : nearbyPlayers ) {
                if ( !player.isCreative() && !player.isSpectator() ) {
                    validNearbyTarget = true;
                    break;
                }
            }

            if ( validNearbyTarget ) {
                level.setBlock( pos, state.setValue( ARMED, true), Block.UPDATE_CLIENTS );
                level.playSound( null, pos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS );
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState( pos );
        BlockState belowState = context.getLevel().getBlockState( pos.below() );

        if ( belowState.is( Blocks.CHAIN ) && belowState.getValue( ChainBlock.AXIS ) == Direction.Axis.Y )
            return defaultBlockState().setValue( WATERLOGGED, fluidState.is( Fluids.WATER ) );

        return null;
    }

    @Override
    public void onPlace( BlockState newState, Level level, BlockPos pos, BlockState snapshotState, boolean updateNeighbors ) {
        super.onPlace( newState, level, pos, snapshotState, updateNeighbors );

        // Schedule first tick on placement
        level.scheduleTick( pos, this, 20, TickPriority.LOW );
    }

    @Override
    public FluidState getFluidState( BlockState state ) {
        return state.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( state );
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( WATERLOGGED, ARMED ) );
    }
}
