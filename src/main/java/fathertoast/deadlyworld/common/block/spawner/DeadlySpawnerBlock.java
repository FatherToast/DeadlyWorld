package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.IDeadlyBlock;
import fathertoast.deadlyworld.common.block.entity.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.block.SpawnerBlock}.
 */
public class DeadlySpawnerBlock extends BaseEntityBlock implements IDeadlyBlock {
    /**
     * Modified copy-paste of the spawner portion of {@link SpawnEggItem#useOn(UseOnContext)}.
     */
    public static boolean spawnEggUseOn( PlayerInteractEvent.RightClickBlock event, ServerLevel level ) {
        ItemStack heldItem = event.getItemStack();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState( pos );
        
        if( heldItem.getItem() instanceof SpawnEggItem egg && blockState.getBlock() instanceof DeadlySpawnerBlock &&
                level.getBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity spawnerBlockEntity ) {
            Player player = event.getEntity();
            
            EntityType<?> spawnEntity = egg.getType( heldItem.getTag() );
            spawnerBlockEntity.setEntityId( spawnEntity, level.getRandom() );
            spawnerBlockEntity.getSpawnerLogic().addSpawn(); // Let it spawn an extra mob, why not
            spawnerBlockEntity.setChanged();
            level.sendBlockUpdated( pos, blockState, blockState, Block.UPDATE_ALL_IMMEDIATE );
            level.gameEvent( player, GameEvent.BLOCK_CHANGE, pos );
            
            if( !player.getAbilities().instabuild ) { // idk why the vanilla method doesn't need this, but we do
                heldItem.shrink( 1 );
            }
            return true;
        }
        return false;
    }
    
    private final SpawnerType spawnerType;
    
    public DeadlySpawnerBlock( SpawnerType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.SPAWNER ) ) );
        spawnerType = type;
    }
    
    public final SpawnerType getSpawnerType() { return spawnerType; }
    
    @Override
    public void initDeadly( ServerLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity spawnerBlockEntity ) {
            spawnerBlockEntity.getSpawnerLogic().initializeSpawner( level, pos, random );
        }
    }
    
    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) { return new DeadlySpawnerBlockEntity( pos, state ); }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return getTicker( level, type, DWBlockEntities.DEADLY_SPAWNER.get() );
    }
    
    @Nullable
    public <T extends BlockEntity, V extends DeadlySpawnerBlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockEntityType<T> type, BlockEntityType<V> expectedType ) {
        return createTickerHelper( type, expectedType,
                level.isClientSide ? DeadlySpawnerBlockEntity::clientTick : DeadlySpawnerBlockEntity::serverTick );
    }
    
    /**
     * Usually, mimics spawn when the block is left-clicked/hit by the player, but
     * if for some reason it shouldn't be detected, we fall back to checking when
     * the block is destroyed.
     *
     * @see #attack(BlockState, Level, BlockPos, Player)
     */
    @Override
    public void playerWillDestroy( Level level, BlockPos pos, BlockState state, Player player ) {
        if( !level.isClientSide && level.getExistingBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity spawnerBlockEntity ) {
            if( createSpawnerMimic( (ServerLevel) level, pos, true, spawnerBlockEntity ) ) {
                level.removeBlock( pos, false );
                return;
            }
        }
        super.playerWillDestroy( level, pos, state, player );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public void attack( BlockState state, Level level, BlockPos pos, Player player ) {
        super.attack( state, level, pos, player );
        
        if( !level.isClientSide && level.getExistingBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity spawnerBlockEntity ) {
            if( createSpawnerMimic( (ServerLevel) level, pos, true, spawnerBlockEntity ) ) {
                level.removeBlock( pos, false );
            }
        }
    }
    
    @Override
    public void onBlockExploded( BlockState state, Level level, BlockPos pos, Explosion explosion ) {
        super.onBlockExploded( state, level, pos, explosion );
        
        if( !level.isClientSide && level.getExistingBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity spawnerBlockEntity ) {
            createSpawnerMimic( (ServerLevel) level, pos, true, spawnerBlockEntity );
        }
    }
    
    /**
     * Attempts to spawn a spawner mimic at the given location, with the same
     * spawner logic as the provided deadly spawner block entity.
     *
     * @param pos                The block position to spawn the mimic at.
     * @param clearAbove         If true, the block at the position above the given block pos will be destroyed
     *                           to prevent the mimic from getting stuck.
     * @param spawnerBlockEntity The spawner block entity that this mimic should inherit its
     *                           spawner logic from.
     * @return True if a mimic was successfully spawned.
     */
    protected final boolean createSpawnerMimic( ServerLevel level, BlockPos pos, boolean clearAbove, DeadlySpawnerBlockEntity spawnerBlockEntity ) {
        if( !spawnerBlockEntity.getSpawnerLogic().isMimic() ) return false;
        
        SpawnerMimic spawnerMimic = getMimicType().create( level );
        
        if( spawnerMimic == null ) return false;
        
        ForgeEventFactory.onFinalizeSpawn( spawnerMimic, level, level.getCurrentDifficultyAt( pos ),
                MobSpawnType.TRIGGERED, null, null );
        
        spawnerMimic.setPos( pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5 );
        
        ProgressiveDelaySpawner oldSpawner = spawnerBlockEntity.getSpawnerLogic();
        ProgressiveDelaySpawner newSpawner = new ProgressiveDelaySpawner( oldSpawner.getSpawnerType(), spawnerMimic, spawnerMimic );
        newSpawner.load( level, pos, oldSpawner.save( new CompoundTag() ) );
        spawnerMimic.setSpawner( newSpawner );
        
        level.addFreshEntity( spawnerMimic );
        
        if( spawnerMimic.isAddedToWorld() ) {
            // Destroy above block for mimics
            // taller than one block, if clearAbove
            if( clearAbove && spawnerMimic.getBoundingBox().getYsize() > 1.0 ) {
                BlockState aboveState = level.getBlockState( pos.above() );
                // Only destroy if there is collision
                if( aboveState.blocksMotion() )
                    level.destroyBlock( pos.above(), false );
            }
            
            // Funny sound
            spawnerMimic.playSound( DWSoundEvents.MIMIC_APPEAR.get() );
            
            // Poof cloud
            level.sendParticles(
                    ParticleTypes.CLOUD,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    10,
                    level.random.nextGaussian(),
                    level.random.nextGaussian(),
                    level.random.nextGaussian(),
                    0.1
            );
            return true;
        }
        return false;
    }
    
    /**
     * @return The spawner mimic entity type to spawn when creating
     * a mimic for this spawner block.
     */
    protected EntityType<? extends SpawnerMimic> getMimicType() {
        return DWEntities.SPAWNER_MIMIC.get();
    }
    
    @Override
    public int getExpDrop( BlockState state, LevelReader level, RandomSource random, BlockPos pos, int fortune, int silkTouch ) {
        return 15 + random.nextInt( 15 ) + random.nextInt( 15 );
    }
    
    @Override
    public RenderShape getRenderShape( BlockState state ) { return RenderShape.MODEL; }
    
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag mode ) {
        super.appendHoverText( itemStack, level, tooltip, mode );
        
        Optional<Component> entityDisplayName = getSpawnEntityDisplayName( itemStack );
        if( entityDisplayName.isPresent() ) {
            tooltip.add( entityDisplayName.get() );
        }
        else {
            tooltip.add( CommonComponents.EMPTY );
            tooltip.add( Component.translatable( "block.minecraft.spawner.desc1" ).withStyle( ChatFormatting.GRAY ) );
            tooltip.add( CommonComponents.space().append( Component.translatable( "block.minecraft.spawner.desc2" ).withStyle( ChatFormatting.BLUE ) ) );
        }
    }
    
    private Optional<Component> getSpawnEntityDisplayName( ItemStack itemStack ) {
        CompoundTag tag = BlockItem.getBlockEntityData( itemStack );
        if( tag != null && NBTHelper.containsCompound( tag, BaseSpawner.SPAWN_DATA_TAG ) ) {
            ResourceLocation entityId = ResourceLocation.tryParse( tag.getCompound( BaseSpawner.SPAWN_DATA_TAG )
                    .getCompound( SpawnData.ENTITY_TAG ).getString( Entity.ID_TAG ) );
            if( entityId != null ) {
                return ForgeRegistries.ENTITY_TYPES.getDelegate( entityId ).map( ( entityType ) ->
                        Component.translatable( entityType.get().getDescriptionId() ).withStyle( ChatFormatting.GRAY ) );
            }
        }
        return Optional.empty();
    }
}