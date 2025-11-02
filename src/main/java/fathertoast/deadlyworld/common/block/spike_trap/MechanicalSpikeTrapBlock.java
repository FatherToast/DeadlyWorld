package fathertoast.deadlyworld.common.block.spike_trap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings( "deprecation" )
public class MechanicalSpikeTrapBlock extends BaseSpikeTrapBlock {
    
    public static BooleanProperty PRESSED = BooleanProperty.create( "pressed" );
    
    
    public MechanicalSpikeTrapBlock( SpikeTrapType type ) {
        super( type );
        registerDefaultState( stateDefinition.any()
                .setValue( PRESSED, false )
                .setValue( FACING, Direction.UP )
        );
    }
    
    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        Direction dir = state.getValue( FACING ).getOpposite();
        
        return state.getValue( PRESSED )
                ? PRESSED_OUTLINE_SHAPES[dir.ordinal()]
                : OUTLINE_SHAPES[dir.ordinal()];
    }
    
    @Override
    public int getLightEmission( BlockState state, BlockGetter level, BlockPos pos ) {
        return state.getValue( PRESSED ) ? type.getLightLevel() : 0;
    }
    
    @Override
    public void entityInside( BlockState state, Level level, BlockPos pos, Entity entity ) {
        if( level.isClientSide ) return;
        
        checkPressed( level, pos, state );
        
        if( state.getValue( PRESSED ) && entity instanceof LivingEntity livingEntity ) {
            hurtEntityInside( level, pos, livingEntity );
        }
    }
    
    // TODO - Unique sound events with subtitles
    private void checkPressed( Level level, BlockPos pos, BlockState state ) {
        int aabbIndex = state.getValue( FACING ).getOpposite().ordinal();
        boolean pressed = getEntitiesInBox( level, PRESS_CHECK_AABBS[aabbIndex].move( pos ), LivingEntity.class ) > 0;
        
        if( !pressed )
            return;
        
        boolean previouslyPressed = state.getValue( PRESSED );
        
        if( !previouslyPressed ) {
            level.setBlock( pos, state.setValue( PRESSED, true ), Block.UPDATE_CLIENTS );
            level.playSound( null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS );
        }
        else {
            if( !level.getBlockTicks().hasScheduledTick( pos, this ) )
                level.scheduleTick( pos, this, 20 );
        }
    }
    
    protected static int getEntitiesInBox( Level level, AABB boundingBox, Class<? extends Entity> entityClass ) {
        return level.getEntitiesOfClass(
                entityClass,
                boundingBox,
                EntitySelector.NO_SPECTATORS.and( ( entity ) -> !entity.isIgnoringBlockTriggers() && !entity.isSteppingCarefully() )
        ).size();
    }
    
    @Override
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        super.tick( state, level, pos, random );
        
        int aabbIndex = state.getValue( FACING ).getOpposite().ordinal();
        boolean pressed = getEntitiesInBox( level, PRESS_CHECK_AABBS[aabbIndex].move( pos ), LivingEntity.class ) > 0;
        
        if( !pressed ) {
            level.setBlock( pos, state.setValue( PRESSED, false ), Block.UPDATE_CLIENTS );
            level.playSound( null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS );
        }
    }
    
    @Override
    @Nullable
    public BlockPathTypes getBlockPathType( BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob ) {
        return null;
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( PRESSED ) );
    }
}
