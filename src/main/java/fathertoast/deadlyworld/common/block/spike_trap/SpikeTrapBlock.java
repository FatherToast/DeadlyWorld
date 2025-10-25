package fathertoast.deadlyworld.common.block.spike_trap;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings( "deprecation" )
public class SpikeTrapBlock extends Block {

    public static BooleanProperty PRESSED = BooleanProperty.create( "pressed" );
    public static EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    // DOWN
    // UP
    // NORTH
    // SOUTH
    // WEST
    // EAST
    /**
     * Contains the bounding boxes per facing to use when checking if any entities are
     * pressing this spike trap.<br><br>
     * Index matches Direction ordinal.
     */
    private static final AABB[] PRESS_CHECK_AABBS = new AABB[] {
            Block.box( 0.0D, 0.0D, 0.0D,
                    16.0D, 2.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 14.0D, 0.0D,
                    16.0D, 16.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 2.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 14.0D,
                    16.0D, 16.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D,
                    2.0D, 16.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 14.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D ).toAabbs().get( 0 )

    };
    /**
     * Contains the collision/visual shapes to use depending on facing when
     * the spike trap is in its normal state.<br><br>
     * Index matches Direction ordinal.
     */
    private static final VoxelShape[] OUTLINE_SHAPES = new VoxelShape[] {
            Block.box( 0.0D, 0.0D, 0.0D,
                    16.0D, 1.0D, 16.0D ),
            Block.box( 0.0D, 15.0D, 0.0D,
                    16.0D, 16.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 1.0D ),
            Block.box( 0.0D, 0.0D, 15.0D,
                    16.0D, 16.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D,
                    1.0D, 16.0D, 16.0D ),
            Block.box( 15.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D )

    };
    /**
     * Contains the collision/visual shapes to use depending on facing
     * when the spike trap is in its "pressed" state.<br><br>
     * Index matches Direction ordinal.
     */
    private static final VoxelShape[] PRESSED_OUTLINE_SHAPES = new VoxelShape[] {
            Block.box( 0.0D, 0.0D, 0.0D,
                    16.0D, 0.5D, 16.0D ),
            Block.box( 0.0D, 15.5D, 0.0D,
                    16.0D, 16.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 0.5D ),
            Block.box( 0.0D, 0.0D, 15.5D,
                    16.0D, 16.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D,
                    0.5D, 16.0D, 16.0D ),
            Block.box( 15.5D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D )
    };
    // DOWN
    // UP
    // NORTH
    // SOUTH
    // WEST
    // EAST

    private final SpikeTrapType type;


    public SpikeTrapBlock( SpikeTrapType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.STONE_PRESSURE_PLATE ) ) );
        this.type = type;
        registerDefaultState( stateDefinition.any()
                .setValue( PRESSED, false )
                .setValue( FACING, Direction.UP )
        );
    }

    public SpikeTrapType getSpikeTrapType() {
        return type;
    }

    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        Direction dir = state.getValue( FACING ).getOpposite();

        return state.getValue( PRESSED )
                ? PRESSED_OUTLINE_SHAPES[dir.ordinal()]
                : OUTLINE_SHAPES[dir.ordinal()];
    }

    @Override
    public BlockState updateShape( BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos ) {
        return !state.canSurvive( level, pos )
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape( state, dir, neighborState, level, pos, neighborPos );
    }

    @Override
    public boolean canSurvive( BlockState state, LevelReader level, BlockPos pos ) {
        Direction facing = state.getValue( FACING );
        BlockPos attachedPos = pos.relative( facing.getOpposite() );
        BlockState neighborState = level.getBlockState( attachedPos );

        return neighborState.isFaceSturdy( level, attachedPos, facing );
    }

    @Override
    public @Nullable BlockState getStateForPlacement( BlockPlaceContext context ) {
        Direction facing = context.getClickedFace();
        BlockState stateForPlacement = defaultBlockState().setValue( FACING, facing );

        if ( !canSurvive( stateForPlacement, context.getLevel(), context.getClickedPos() ) )
            return null;

        return stateForPlacement;
    }

    @Override
    public void entityInside( BlockState state, Level level, BlockPos pos, Entity entity ) {
        checkPressed( level, pos, state );

        if ( state.getValue( PRESSED ) && entity instanceof LivingEntity livingEntity ) {
            float damage = type.getConfig( level ).damage.getFloat();
            livingEntity.hurt( DWDamageTypes.of( level, DWDamageTypes.SPIKE_TRAP ), damage );
        }
    }

    // TODO - Unique sound events with subtitles
    private void checkPressed( Level level, BlockPos pos, BlockState state ) {
        int aabbIndex = state.getValue( FACING ).getOpposite().ordinal();
        boolean pressed = getEntitiesInBox( level, PRESS_CHECK_AABBS[aabbIndex].move( pos ), Player.class ) > 0;

        if ( !pressed )
            return;

        boolean previouslyPressed = state.getValue( PRESSED );

        if ( !previouslyPressed ) {
            level.setBlock( pos, state.setValue( PRESSED, true ), Block.UPDATE_CLIENTS );
            level.playSound( null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS );
        }
        else {
            if ( !level.getBlockTicks().hasScheduledTick( pos, this ) )
                level.scheduleTick( pos, this, 20 );
        }
    }

    protected static int getEntitiesInBox( Level level, AABB boundingBox, Class<? extends Entity> entityClass ) {
        return level.getEntitiesOfClass(
                    entityClass,
                    boundingBox,
                    EntitySelector.NO_SPECTATORS.and( (entity) -> !entity.isIgnoringBlockTriggers() && !entity.isSteppingCarefully() )
                ).size();
    }

    @Override
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        super.tick( state, level, pos, random );

        int aabbIndex = state.getValue( FACING ).getOpposite().ordinal();
        boolean pressed = getEntitiesInBox( level, PRESS_CHECK_AABBS[aabbIndex].move( pos ), Player.class ) > 0;

        if ( !pressed ) {
            level.setBlock(pos, state.setValue(PRESSED, false), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS);
        }
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( PRESSED, FACING ) );
    }
}
