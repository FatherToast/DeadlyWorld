package fathertoast.deadlyworld.common.block.sea_mine;

import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.BlocksConfig;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.field.WeightedPotionList;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings( "deprecation" )
public class SeaMineBlock extends Block implements SimpleWaterloggedBlock {

    private static final VoxelShape SHAPE =
            Block.box( 3.0D, 3.0D, 3.0D, 13.0D, 13.0D, 13.0D );


    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ARMED = BooleanProperty.create( "armed" );

    private static final Predicate<Player> DEFAULT_PREDICATE = (player) -> !player.isSpectator() && !player.isCreative();
    private static final Predicate<Player> VS_CREATIVE_PREDICATE = (player) -> !player.isSpectator();

    private final SeaMineType type;

    public SeaMineBlock( SeaMineType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.SPAWNER ) ) );
        registerDefaultState( stateDefinition.any().setValue( WATERLOGGED, false ).setValue( ARMED, false ) );
        this.type = type;
    }

    public SeaMineType getSeaMineType() {
        return type;
    }

    public float getExplosionPower() {
        return Config.BLOCKS.get( type ).explosionPower.getFloat();
    }

    private WeightedPotionList getPotionList( ) {
        return Config.BLOCKS.get( type ).potions.get();
    }

    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE;
    }

    @Override
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        level.scheduleTick( pos, this, 20, TickPriority.LOW );

        boolean armed = state.getValue( ARMED );

        // We are armed, explotando time
        if ( armed ) {
            level.removeBlock( pos, false );
        }
        // Check for a nearby player in survival or adventure mode
        else {
            // Do not arm the mine in peaceful mode unless configured to
            if ( level.getDifficulty() == Difficulty.PEACEFUL && !Config.MAIN.GENERAL.activateTrapsInPeaceful.get() )
                return;

            List<Player> nearbyPlayers = level.getEntitiesOfClass( Player.class, new AABB( pos ).inflate( 1.0F ) );
            boolean validNearbyTarget = false;
            Predicate<Player> predicate = Config.MAIN.GENERAL.activateTrapsVsCreative.get()
                    ? VS_CREATIVE_PREDICATE
                    : DEFAULT_PREDICATE;

            for ( Player player : nearbyPlayers ) {
                if ( predicate.test( player ) ) {
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
    public void onRemove( BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean updateNeighbors ) {
        super.onRemove( oldState, level, pos, newState, updateNeighbors );

        if ( !newState.is( oldState.getBlock() ) ) {
            explode(level, pos, level.random);
        }
    }

    @Override
    public InteractionResult use( BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult ) {
        if ( level.isClientSide )
            return InteractionResult.SUCCESS;

        level.removeBlock( pos, false );
        return InteractionResult.CONSUME;
    }

    @Override
    public void onProjectileHit( Level level, BlockState state, BlockHitResult hitResult, Projectile projectile ) {
        BlockPos hitPos = hitResult.getBlockPos();

        if (!level.isClientSide && projectile.mayInteract( level, hitPos )
                && projectile.getType().is( EntityTypeTags.IMPACT_PROJECTILES )
                && projectile.getDeltaMovement().length() > 0.1D ) {

            level.destroyBlock( hitPos, false );
        }
    }

    @Override
    public FluidState getFluidState( BlockState state ) {
        return state.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( state );
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( WATERLOGGED, ARMED ) );
    }

    private void explode( Level level, BlockPos pos, RandomSource random ) {
        level.explode(
                null,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                getExplosionPower(),
                Level.ExplosionInteraction.BLOCK
        );
        // Pick a potion and apply it to all creatures caught in the blast
        if ( getPotionList().isEmpty() ) return;

        final MobEffectInstance potion = getPotionList().next( random );

        if ( potion == null ) return;

        List<LivingEntity> nearbyCreatures = level.getEntitiesOfClass( LivingEntity.class, new AABB( pos ).inflate( getExplosionPower() ) );
        nearbyCreatures.forEach( (livingEntity) ->
                livingEntity.addEffect( new MobEffectInstance( potion ) ) );
    }
}
