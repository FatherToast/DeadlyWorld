package fathertoast.deadlyworld.common.block.infested;

import fathertoast.crust.api.lib.LevelEventHelper;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.InfestedBlocksConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class DeadlyInfestedBlock extends InfestedBlock {
    /**
     * This holds the 'hostBlock' for the current DeadlyInfestedBlock construction.
     * We do this hacky workaround so that we have access to the parameter during block state definition,
     * which is permanently assigned in the superclass constructor before 'hostBlock' is available.
     */
    @Nullable
    protected static Block blockForStateDef;
    
    public static DeadlyInfestedBlock factory( ResourceLocation hostBlockLoc ) {
        Block hostBlock = InfestedBlockAutoGen.getHostBlockOrThrow( hostBlockLoc );
        blockForStateDef = hostBlock;
        DeadlyInfestedBlock infestedBlock = new DeadlyInfestedBlock( hostBlock, hostBlockLoc, copyProperties( hostBlock ) );
        // Note: Properties are copied, but destroy time is fixed at half and explosion resist fixed at 0.75 by superclass
        blockForStateDef = null;
        return infestedBlock;
    }
    
    private static final String PATH_PREFIX = "infested/";
    
    /** @return The path to assign for an infested block based on the given host block resource location. */
    public static String pathFor( ResourceLocation hostBlockLoc ) {
        return PATH_PREFIX + hostBlockLoc.getNamespace() + "/" + hostBlockLoc.getPath();
    }
    
    /** @return The host block resource location parsed from an infested block resource location, or null if not an infested block. */
    @Nullable
    public static ResourceLocation hostLocFrom( ResourceLocation infestedBlockLoc ) {
        if( DeadlyWorld.MOD_ID.equals( infestedBlockLoc.getNamespace() ) && infestedBlockLoc.getPath().startsWith( PATH_PREFIX ) ) {
            String[] split = infestedBlockLoc.getPath().substring( PATH_PREFIX.length() ).split( "/", 2 );
            return ResourceLocation.fromNamespaceAndPath( split[0], split[1] );
        }
        return null;
    }
    
    /** @return An infested version of the given host block if one exists, otherwise it just returns the host block. */
    public static BlockState tryInfest( BlockState hostState ) {
        return isCompatibleHostBlock( hostState ) ? infestedStateByHost( hostState ) : hostState;
    }
    
    /**
     * Unlike the regular tryInfest method, this will also convert given infested blocks into the infested block last
     * registered to a particular host block (e.g., vanilla infested blocks will become this mod's infested blocks).
     * <p>
     * Use this anywhere the given host block could be user-defined, such as a block state provider for world gen.
     *
     * @return An infested version of the given host block if one exists, otherwise it just returns the host block.
     */
    public static BlockState tryInfestUnknown( BlockState hostState ) {
        if( hostState.getBlock() instanceof InfestedBlock infestedBlock ) // treat vanilla infested blocks like their host block
            return tryInfestUnknown( infestedBlock.hostStateByInfested( hostState ) );
        return isCompatibleHostBlock( hostState ) ? infestedStateByHost( hostState ) : hostState;
    }
    
    /** Runs the logic for "infested block cleansing". */
    public static boolean tryCleanseBlock( PlayerInteractEvent.RightClickBlock event, ServerLevel level ) {
        if( config().GENERAL.cleanseTools.isEmpty() ) return false;
        
        ItemStack heldItem = event.getItemStack();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState( pos );
        
        if( config().GENERAL.cleanseTools.get().containsOrTag( heldItem.getItem(), heldItem::is ) &&
                blockState.getBlock() instanceof InfestedBlock block ) {
            Player player = event.getEntity();
            
            // Spawn the silverfish
            if( config().GENERAL.cleanseSpawnsSilverfish.get() ) {
                Direction facing = event.getHitVec().getDirection();
                BlockPos spawnPos = pos.relative( facing );
                Silverfish silverfish = EntityType.SILVERFISH.create( level );
                if( silverfish != null ) {
                    silverfish.moveTo( spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                            facing.getAxis() == Direction.Axis.Y ? 360.0F * level.random.nextFloat() : facing.toYRot(), 0.0F );
                    level.addFreshEntity( silverfish );
                    silverfish.spawnAnim();
                    silverfish.setTarget( player );
                }
            }
            
            // Replace the infested block with its respective host block
            BlockState hostState = block.hostStateByInfested( blockState );
            level.setBlock( pos, hostState, Block.UPDATE_ALL_IMMEDIATE );
            level.gameEvent( GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of( player, hostState ) );
            LevelEventHelper.BLOCK_BREAK_FX.play( level, null, pos, hostState );
            
            // Damage the item
            if( config().GENERAL.cleanseDamage.get() > 0 ) {
                heldItem.hurtAndBreak( config().GENERAL.cleanseDamage.get(),
                        player, ( p ) -> p.broadcastBreakEvent( event.getHand() ) );
            }
            
            return true;
        }
        return false;
    }
    
    /** Flags the silverfish AI to call for help; it will try to break nearby infested blocks one second later. */
    public static void triggerCallForHelp( Silverfish silverfish ) {
        silverfish.hurt( DWDamageTypes.of( silverfish.level(), DWDamageTypes.TRIGGER_SILVERFISH ), 0.0F );
    }
    
    private static InfestedBlocksConfig config() { return Config.INFESTED_BLOCKS; }
    
    
    // Infested block implementation
    
    private final ResourceLocation hostBlockLocation;
    
    protected DeadlyInfestedBlock( Block hostBlock, ResourceLocation hostBlockLoc, BlockBehaviour.Properties properties ) {
        super( hostBlock, properties );
        hostBlockLocation = hostBlockLoc;
        registerDefaultState( toInfested( hostBlock.defaultBlockState() ) );
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder );
        // Copy the block state definition from the host
        if( blockForStateDef != null ) {
            for( Property<?> property : blockForStateDef.getStateDefinition().getProperties() ) {
                builder.add( property );
            }
        }
    }
    
    /** Used for model mirroring. */
    public ResourceLocation getHostBlockLocation() { return hostBlockLocation; }
    
    /** AT'd to modify the private super method. */
    @Override
    public void spawnInfestation( ServerLevel level, BlockPos pos ) {
        Silverfish silverfish = EntityType.SILVERFISH.create( level );
        if( silverfish != null ) {
            silverfish.moveTo( pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    // Apply random rotation
                    360.0F * level.random.nextFloat(), 0.0F );
            level.addFreshEntity( silverfish );
            silverfish.spawnAnim();
            // Set target on spawn; the hope is to prevent the silverfish from immediately hiding
            silverfish.setTarget( level.getNearestPlayer( TargetingConditions.forCombat().ignoreLineOfSight().range(
                    silverfish.getAttributeValue( Attributes.FOLLOW_RANGE ) ), silverfish ) );
            // Check for aggressive chance config option
            if( config().AUTO_GEN.aggressiveChance.rollChance( level.random ) ) triggerCallForHelp( silverfish );
        }
    }
    
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
    public float getDestroyProgress( BlockState infestedState, Player player, BlockGetter level, BlockPos pos ) {
        return super.getDestroyProgress( infestedState, player, level, pos ) *
                config().AUTO_GEN.breakSpeedMulti.getFloat();
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public float getExplosionResistance() { return config().AUTO_GEN.explosionResistMulti.getFloat(); }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public void onProjectileHit( Level level, BlockState infestedState, BlockHitResult context, Projectile projectile ) {
        if( !level.isClientSide() && config().AUTO_GEN.projBreakChance.get() > 0.0 &&
                config().AUTO_GEN.projBreakChance.rollChance( level.random ) ) {
            level.destroyBlock( context.getBlockPos(), true, projectile.getEffectSource() );
        }
    }
    
    @Override
    public void stepOn( Level level, BlockPos pos, BlockState infestedState, Entity entity ) {
        if( !level.isClientSide() && config().AUTO_GEN.stepBreakChance.get() > 0.0 && entity instanceof Player player &&
                !player.isCreative() && config().AUTO_GEN.stepBreakChance.rollChance( level.random ) ) {
            level.destroyBlock( pos, true, entity );
        }
    }
    
    
    // Host block emulation
    
    public BlockState toInfested( BlockState hostState ) { return infestedStateByHost( hostState ); }
    
    public BlockState toHost( BlockState infestedState ) { return hostStateByInfested( infestedState ); }
    
    @Override
    public MutableComponent getName() {
        return Component.translatable( config().AUTO_GEN.nameStyle.get().getLangKey(),
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
    public MapColor getMapColor( BlockState infestedState, BlockGetter level, BlockPos pos, MapColor defaultColor ) {
        return getHostBlock().getMapColor( toHost( infestedState ), level, pos, defaultColor );
    }
    
    @Override
    public SoundType getSoundType( BlockState infestedState, LevelReader level, BlockPos pos, @Nullable Entity entity ) {
        return getHostBlock().getSoundType( toHost( infestedState ), level, pos, entity );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public SoundType getSoundType( BlockState infestedState ) {
        return getHostBlock().getSoundType( toHost( infestedState ) );
    }
    
    @Override
    public int getLightEmission( BlockState infestedState, BlockGetter level, BlockPos pos ) {
        return getHostBlock().getLightEmission( toHost( infestedState ), level, pos );
    }
    
    
    // Behavior copying
    
    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState rotate( BlockState infestedState, Rotation rotation ) {
        return toInfested( getHostBlock().rotate( toHost( infestedState ), rotation ) );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public BlockState mirror( BlockState infestedState, Mirror mirror ) {
        return toInfested( getHostBlock().mirror( toHost( infestedState ), mirror ) );
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        BlockState hostState = getHostBlock().getStateForPlacement( context );
        return hostState == null ? null : toInfested( hostState );
    }
    
    @Override
    public int getFlammability( BlockState infestedState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().getFlammability( toHost( infestedState ), level, pos, direction );
    }
    
    @Override
    public boolean isFlammable( BlockState infestedState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().isFlammable( toHost( infestedState ), level, pos, direction );
    }
    
    @Override
    public int getFireSpreadSpeed( BlockState infestedState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().getFireSpreadSpeed( toHost( infestedState ), level, pos, direction );
    }
    
    @Override
    public boolean isFireSource( BlockState infestedState, LevelReader level, BlockPos pos, Direction direction ) {
        return getHostBlock().isFireSource( toHost( infestedState ), level, pos, direction );
    }
}