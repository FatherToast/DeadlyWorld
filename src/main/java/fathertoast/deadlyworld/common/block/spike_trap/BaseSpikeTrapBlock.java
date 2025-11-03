package fathertoast.deadlyworld.common.block.spike_trap;

import fathertoast.deadlyworld.common.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings( "deprecation" )
public class BaseSpikeTrapBlock extends Block {
    
    public static EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    
    /**
     * Contains the bounding boxes per facing to use when checking if any entities are
     * pressing this spike trap.<br><br>
     * Index matches Direction ordinal.
     */
    protected static final AABB[] PRESS_CHECK_AABBS = new AABB[] {
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
    protected static final VoxelShape[] OUTLINE_SHAPES = new VoxelShape[] {
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
    protected static final VoxelShape[] PRESSED_OUTLINE_SHAPES = new VoxelShape[] {
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
    
    protected final SpikeTrapType type;
    
    
    public BaseSpikeTrapBlock( SpikeTrapType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.STONE_PRESSURE_PLATE ) ) );
        this.type = type;
        registerDefaultState( stateDefinition.any()
                .setValue( FACING, Direction.UP )
        );
    }
    
    public SpikeTrapType getSpikeTrapType() {
        return type;
    }
    
    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        Direction dir = state.getValue( FACING ).getOpposite();
        return OUTLINE_SHAPES[dir.ordinal()];
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
    @Nullable
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        Direction facing = context.getClickedFace();
        BlockState stateForPlacement = defaultBlockState().setValue( FACING, facing );
        
        if( !canSurvive( stateForPlacement, context.getLevel(), context.getClickedPos() ) )
            return null;
        
        return stateForPlacement;
    }
    
    @Override
    public void entityInside( BlockState state, Level level, BlockPos pos, Entity entity ) {
        if( !level.isClientSide && entity instanceof LivingEntity livingEntity ) {
            hurtEntityInside( level, pos, livingEntity );
        }
    }
    
    protected void hurtEntityInside( Level level, BlockPos pos, LivingEntity entity ) {
        type.hurtEntity( level, pos, entity );
    }
    
    @Override
    @Nullable
    public BlockPathTypes getBlockPathType( BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob ) {
        return BlockPathTypes.DAMAGE_OTHER;
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( FACING ) );
    }
}
