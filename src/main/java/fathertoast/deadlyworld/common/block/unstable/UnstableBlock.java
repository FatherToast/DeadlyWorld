package fathertoast.deadlyworld.common.block.unstable;

import com.google.common.collect.Maps;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.UnstableBlocksConfig;
import fathertoast.deadlyworld.common.core.registry.BlockAutoGen;
import fathertoast.deadlyworld.common.core.registry.IAutoGenBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings( "deprecation" )
public class UnstableBlock extends Block implements IAutoGenBlock {
    public static String BLOCK_KEY = "unstable";
    
    private static final Map<Block, Block> BLOCK_BY_ORIGIN_BLOCK = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> ORIGIN_TO_UNSTABLE_STATES = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> UNSTABLE_TO_ORIGIN_STATES = Maps.newIdentityHashMap();
    
    /** @return An unstable version of the given origin block if one exists, otherwise it just returns the origin block. */
    public static BlockState tryDestabilizing( BlockState originState ) {
        return isCompatibleOriginBlock( originState ) ? unstableStateByOrigin( originState ) : originState;
    }
    
    /** @return True if the given origin block has an unstable equivalent. */
    public static boolean isCompatibleOriginBlock( BlockState originState ) {
        return BLOCK_BY_ORIGIN_BLOCK.containsKey( originState.getBlock() );
    }
    
    private static BlockState unstableStateByOrigin( BlockState originState ) {
        return getNewStateWithProperties( ORIGIN_TO_UNSTABLE_STATES, originState,
                () -> BLOCK_BY_ORIGIN_BLOCK.get( originState.getBlock() ).defaultBlockState() );
    }
    
    private BlockState originStateByUnstable( BlockState unstableState ) {
        return getNewStateWithProperties( UNSTABLE_TO_ORIGIN_STATES, unstableState,
                () -> getOriginBlock().defaultBlockState() );
    }
    
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private static BlockState getNewStateWithProperties( Map<BlockState, BlockState> map, BlockState state, Supplier<BlockState> newState ) {
        return map.computeIfAbsent( state, ( keyState ) -> {
            BlockState valueState = newState.get();
            
            for( Property property : keyState.getProperties() ) {
                valueState = valueState.hasProperty( property )
                        ? valueState.setValue( property, keyState.getValue( property ) )
                        : valueState;
            }
            return valueState;
        } );
    }
    
    private static UnstableBlocksConfig config() { return Config.UNSTABLE_BLOCKS; }
    
    // Auto-gen block implementation
    
    private final Block originBlock;
    private final ResourceLocation originBlockLocation;
    
    public UnstableBlock( Block original, ResourceLocation originBlockLoc ) {
        super( copyProperties( original ) );
        BLOCK_BY_ORIGIN_BLOCK.put( original, this );
        originBlock = original;
        originBlockLocation = originBlockLoc;
        registerDefaultState( toAutoGen( originBlock.defaultBlockState() ) );
    }
    
    /** Called by the Block.class constructor; we defer to the auto-generation logic. */
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        BlockAutoGen.copyBlockStateDefinition( builder );
    }
    
    /** @return This auto-generated block's block state definition. */
    @Override
    public StateDefinition<Block, BlockState> getBlockStateDefinition() { return getStateDefinition(); }
    
    /** @return The origin block. */
    @Override
    public Block getOriginBlock() { return originBlock; }
    
    /** @return The origin block's resource location. Used for model lookups. */
    @Override
    public ResourceLocation getOriginBlockLocation() { return originBlockLocation; }
    
    /** @return The auto-generated block state corresponding to a specific origin block state. */
    @Override
    public BlockState toAutoGen( BlockState originState ) { return unstableStateByOrigin( originState ); }
    
    /** @return The origin block state corresponding to a specific auto-generated block state. */
    @Override
    public BlockState toOrigin( BlockState autoGenState ) { return originStateByUnstable( autoGenState ); }
    
    // Block implementation
    
    @SuppressWarnings( "deprecation" )
    @Override
    public List<ItemStack> getDrops( BlockState unstableState, LootParams.Builder builder ) {
        // Drop a loot table, if one exists TODO test this later to make sure it actually works lol
        ServerLevel level = builder.getLevel();
        LootTable lootTable = level.getServer().getLootData().getElement( LootDataType.TABLE, getLootTable() );
        if( lootTable != null ) {
            return lootTable.getRandomItems( builder
                    .withParameter( LootContextParams.BLOCK_STATE, unstableState )
                    .create( LootContextParamSets.BLOCK ) );
        }
        // Otherwise, just act like the origin block
        return getOriginBlock().getDrops( toOrigin( unstableState ), builder );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public float getDestroyProgress( BlockState unstableState, Player player, BlockGetter level, BlockPos pos ) {
        return super.getDestroyProgress( unstableState, player, level, pos ) *
                config().AUTO_GEN.breakSpeedMulti.getFloat();
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public float getExplosionResistance() { return config().AUTO_GEN.explosionResistMulti.getFloat(); }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public void onProjectileHit( Level level, BlockState unstableState, BlockHitResult context, Projectile projectile ) {
        if( !level.isClientSide() && config().AUTO_GEN.projFallChance.get() > 0.0 &&
                config().AUTO_GEN.projFallChance.rollChance( level.random ) ) {
            level.destroyBlock( context.getBlockPos(), true, projectile.getEffectSource() );
        }
    }
    
    @Override
    public void stepOn( Level level, BlockPos pos, BlockState unstableState, Entity entity ) {
        if( !level.isClientSide() && config().AUTO_GEN.stepFallChance.get() > 0.0 && entity instanceof Player player &&
                !player.isCreative() && config().AUTO_GEN.stepFallChance.rollChance( level.random ) ) {
            level.destroyBlock( pos, true, entity );
        }
    }
    
    @Override
    public void tick( BlockState unstableState, ServerLevel level, BlockPos pos, RandomSource random ) {
        level.destroyBlock( pos, true, null );
    }
    
    @Override
    public void onRemove( BlockState state, Level level, BlockPos pos, BlockState newState, boolean updateNeighbors ) {
        super.onRemove( state, level, pos, newState, updateNeighbors );
        
        if( level instanceof ServerLevel serverLevel )
            nudgeUnstableNeighbors( serverLevel, pos );
    }
    
    @Override
    @Nullable
    public PushReaction getPistonPushReaction( BlockState state ) {
        return PushReaction.DESTROY;
    }
    
    private static void nudgeUnstableNeighbors( ServerLevel level, BlockPos pos ) {
        final int ticksForScheduling = config().AUTO_GEN.neighborUpdateTicks.get();
        
        for( Direction direction : Direction.values() ) {
            BlockPos neighborPos = pos.relative( direction );
            BlockState neighborState = level.getBlockState( neighborPos );
            
            if( neighborState.getBlock() instanceof UnstableBlock block
                    && !level.getBlockTicks().hasScheduledTick( neighborPos, block ) ) {
                level.scheduleTick( neighborPos, neighborState.getBlock(), ticksForScheduling, TickPriority.NORMAL );
            }
        }
    }
    
    // Host block emulation
    
    @Override
    public MutableComponent getName() {
        return Component.translatable( config().AUTO_GEN.nameStyle.get().getLangKey( BLOCK_KEY ),
                Component.translatable( getOriginBlock().getDescriptionId() ) );
    }
    
    @Override
    public String getDescriptionId() { return getName().getString(); } // Kinda hacky, feels like it might be illegal
    
    
    // Properties copying
    
    private static BlockBehaviour.Properties copyProperties( Block originBlock ) {
        final BlockState hostState = originBlock.defaultBlockState();
        //noinspection deprecation
        return BlockBehaviour.Properties.of()
                .mapColor( originBlock.defaultMapColor() )
                .friction( originBlock.getFriction() )
                .speedFactor( originBlock.getSpeedFactor() )
                .jumpFactor( originBlock.getJumpFactor() )
                .sound( originBlock.getSoundType( hostState ) )
                .instrument( hostState.instrument() );
    }
    
    @Override
    public MapColor getMapColor( BlockState unstableState, BlockGetter level, BlockPos pos, MapColor defaultColor ) {
        return getOriginBlock().getMapColor( toOrigin( unstableState ), level, pos, defaultColor );
    }
    
    @Override
    public SoundType getSoundType( BlockState unstableState, LevelReader level, BlockPos pos, @Nullable Entity entity ) {
        return getOriginBlock().getSoundType( toOrigin( unstableState ), level, pos, entity );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public SoundType getSoundType( BlockState unstableState ) {
        return getOriginBlock().getSoundType( toOrigin( unstableState ) );
    }
    
    @Override
    public int getLightEmission( BlockState unstableState, BlockGetter level, BlockPos pos ) {
        return getOriginBlock().getLightEmission( toOrigin( unstableState ), level, pos );
    }
    
    
    // Behavior copying
    
    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState rotate( BlockState unstableState, Rotation rotation ) {
        return toAutoGen( getOriginBlock().rotate( toOrigin( unstableState ), rotation ) );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState mirror( BlockState unstableState, Mirror mirror ) {
        return toAutoGen( getOriginBlock().mirror( toOrigin( unstableState ), mirror ) );
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        BlockState originState = getOriginBlock().getStateForPlacement( context );
        return originState == null ? null : toAutoGen( originState );
    }
    
    @Override
    public int getFlammability( BlockState unstableState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getOriginBlock().getFlammability( toOrigin( unstableState ), level, pos, direction );
    }
    
    @Override
    public boolean isFlammable( BlockState unstableState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getOriginBlock().isFlammable( toOrigin( unstableState ), level, pos, direction );
    }
    
    @Override
    public int getFireSpreadSpeed( BlockState unstableState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getOriginBlock().getFireSpreadSpeed( toOrigin( unstableState ), level, pos, direction );
    }
    
    @Override
    public boolean isFireSource( BlockState unstableState, LevelReader level, BlockPos pos, Direction direction ) {
        return getOriginBlock().isFireSource( toOrigin( unstableState ), level, pos, direction );
    }
}