package fathertoast.deadlyworld.common.block.pitfall;

import com.google.common.collect.Maps;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.InfestedBlocksConfig;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;
import org.apache.logging.log4j.core.jmx.Server;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings( "deprecation" )
public class UnstableBlock extends Block implements IAutoGenBlock {
    public static String BLOCK_KEY = "unstable";

    private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> HOST_TO_INFESTED_STATES = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> INFESTED_TO_HOST_STATES = Maps.newIdentityHashMap();

    /** @return An unstable version of the given host block if one exists, otherwise it just returns the host block. */
    public static BlockState tryDestabilizing( BlockState hostState ) {
        return isCompatibleHostBlock( hostState ) ? unstableStateByHost( hostState ) : hostState;
    }

    /** @return True if the given host block has an unstable equivalent. */
    public static boolean isCompatibleHostBlock( BlockState hostState ) {
        return BLOCK_BY_HOST_BLOCK.containsKey( hostState.getBlock() );
    }

    private static BlockState unstableStateByHost( BlockState hostState ) {
        return getNewStateWithProperties( HOST_TO_INFESTED_STATES, hostState,
                () -> BLOCK_BY_HOST_BLOCK.get( hostState.getBlock() ).defaultBlockState() );
    }

    private BlockState hostStateByUnstable( BlockState unstableState ) {
        return getNewStateWithProperties( INFESTED_TO_HOST_STATES, unstableState,
                () -> getHostBlock().defaultBlockState() );
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static BlockState getNewStateWithProperties( Map<BlockState, BlockState> map, BlockState state, Supplier<BlockState> newState ) {
        return map.computeIfAbsent( state, ( keyState ) -> {
            BlockState valueState = newState.get();

            for( Property property : keyState.getProperties() ) {
                valueState = valueState.hasProperty( property )
                        ? valueState.setValue( property, keyState.getValue( property ) )
                        : valueState;
            }
            return valueState;
        });
    }

    private static UnstableBlocksConfig config() { return Config.UNSTABLE_BLOCKS; }

    // Auto-gen block implementation

    private final Block hostBlock;
    private final ResourceLocation hostBlockLocation;

    public UnstableBlock( Block hostBlck, ResourceLocation hostBlockLoc ) {
        super( copyProperties( hostBlck ) );
        BLOCK_BY_HOST_BLOCK.put( hostBlck, this );
        hostBlock = hostBlck;
        hostBlockLocation = hostBlockLoc;
        registerDefaultState( toAutoGen( hostBlock.defaultBlockState() ) );
    }

    public Block getHostBlock() {
        return hostBlock;
    }

    /** Called by the Block.class constructor; we defer to the auto-generation logic. */
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        BlockAutoGen.copyBlockStateDefinition( builder );
    }

    /** @return The origin block's resource location. Used for model lookups. */
    @Override
    public ResourceLocation getOriginBlockLocation() { return hostBlockLocation; }

    /** @return The auto-generated block state corresponding to a specific origin block state. */
    @Override
    public BlockState toAutoGen( BlockState originState ) { return unstableStateByHost( originState ); }

    /** @return The origin block state corresponding to a specific auto-generated block state. */
    @Override
    public BlockState toOrigin( BlockState autoGenState ) { return hostStateByUnstable( autoGenState ); }

    // Block implementation

    @SuppressWarnings( "deprecation" )
    @Override
    public List<ItemStack> getDrops( BlockState infestedState, LootParams.Builder builder ) {
        // Drop a loot table, if one exists TODO test this later to make sure it actually works lol
        ServerLevel level = builder.getLevel();
        LootTable lootTable = level.getServer().getLootData().getElement( LootDataType.TABLE, getLootTable() );
        if( lootTable != null ) {
            return lootTable.getRandomItems( builder
                    .withParameter( LootContextParams.BLOCK_STATE, infestedState )
                    .create( LootContextParamSets.BLOCK ) );
        }
        // Otherwise, make the loot ourselves
        ItemStack tool = builder.getOptionalParameter( LootContextParams.TOOL );
        if( tool != null && tool.getEnchantmentLevel( Enchantments.SILK_TOUCH ) > 0 ) {
            return List.of( new ItemStack( getHostBlock() ) );
        }
        return Collections.emptyList();
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
        if( !level.isClientSide() && config().AUTO_GEN.projBreakChance.get() > 0.0 &&
                config().AUTO_GEN.projBreakChance.rollChance( level.random ) ) {
            popBlock( (ServerLevel) level, context.getBlockPos(), projectile.getEffectSource() );
        }
    }

    @Override
    public void stepOn( Level level, BlockPos pos, BlockState unstableState, Entity entity ) {
        if( !level.isClientSide() && config().AUTO_GEN.stepBreakChance.get() > 0.0 && entity instanceof Player player &&
                !player.isCreative() && config().AUTO_GEN.stepBreakChance.rollChance( level.random ) ) {
            popBlock( (ServerLevel) level, pos, entity );
        }
    }

    @Override
    public void tick( BlockState unstableState, ServerLevel level, BlockPos pos, RandomSource random ) {
        popBlock( level, pos, null );
    }

    private static void popBlock( ServerLevel level, BlockPos pos, @Nullable Entity entity ) {
        BlockState state = level.getBlockState( pos );
        Block.dropResources( state, level, pos );

        level.destroyBlock( pos, true, entity );

        for ( Direction direction : Direction.values() ) {
            BlockPos neighborPos = pos.relative( direction );
            BlockState neighborState = level.getBlockState( neighborPos );

            if ( neighborState.getBlock() instanceof UnstableBlock ) {
                level.scheduleTick( neighborPos, neighborState.getBlock(), 5, TickPriority.NORMAL );
            }
        }
    }

    // Host block emulation

    @Override
    public MutableComponent getName() {
        return Component.translatable( config().AUTO_GEN.nameStyle.get().getLangKey( BLOCK_KEY ),
                Component.translatable( getHostBlock().getDescriptionId() ) );
    }

    @Override
    public String getDescriptionId() { return getName().getString(); } // Kinda hacky, feels like it might be illegal


    // Properties copying

    private static BlockBehaviour.Properties copyProperties( Block hostBlock ) {
        final BlockState hostState = hostBlock.defaultBlockState();
        //noinspection deprecation
        return BlockBehaviour.Properties.of()
                .mapColor( hostBlock.defaultMapColor() )
                .friction( hostBlock.getFriction() )
                .speedFactor( hostBlock.getSpeedFactor() )
                .jumpFactor( hostBlock.getJumpFactor() )
                .sound( hostBlock.getSoundType( hostState ) )
                .instrument( hostState.instrument() );
    }

    @Override
    public MapColor getMapColor( BlockState unstableState, BlockGetter level, BlockPos pos, MapColor defaultColor ) {
        return getHostBlock().getMapColor( toOrigin( unstableState ), level, pos, defaultColor );
    }

    @Override
    public SoundType getSoundType( BlockState unstableState, LevelReader level, BlockPos pos, @Nullable Entity entity ) {
        return getHostBlock().getSoundType( toOrigin( unstableState ), level, pos, entity );
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public SoundType getSoundType( BlockState unstableState ) {
        return getHostBlock().getSoundType( toOrigin( unstableState ) );
    }

    @Override
    public int getLightEmission( BlockState unstableState, BlockGetter level, BlockPos pos ) {
        return getHostBlock().getLightEmission( toOrigin( unstableState ), level, pos );
    }


    // Behavior copying

    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState rotate( BlockState unstableState, Rotation rotation ) {
        return toAutoGen( getHostBlock().rotate( toOrigin( unstableState ), rotation ) );
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState mirror( BlockState unstableState, Mirror mirror ) {
        return toAutoGen( getHostBlock().mirror( toOrigin( unstableState ), mirror ) );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        BlockState hostState = getHostBlock().getStateForPlacement( context );
        return hostState == null ? null : toAutoGen( hostState );
    }

    @Override
    public int getFlammability( BlockState unstableState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().getFlammability( toOrigin( unstableState ), level, pos, direction );
    }

    @Override
    public boolean isFlammable( BlockState unstableState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().isFlammable( toOrigin( unstableState ), level, pos, direction );
    }

    @Override
    public int getFireSpreadSpeed( BlockState unstableState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().getFireSpreadSpeed( toOrigin( unstableState ), level, pos, direction );
    }

    @Override
    public boolean isFireSource( BlockState unstableState, LevelReader level, BlockPos pos, Direction direction ) {
        return getHostBlock().isFireSource( toOrigin( unstableState ), level, pos, direction );
    }
}
