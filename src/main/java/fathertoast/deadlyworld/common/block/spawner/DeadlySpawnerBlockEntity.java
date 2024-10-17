package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.block.entity.SpawnerBlockEntity}.
 */
public class DeadlySpawnerBlockEntity extends BlockEntity {
    
    protected final ProgressiveDelaySpawner spawner;
    protected final SpawnerType spawnerType;
    
    public DeadlySpawnerBlockEntity( BlockPos pos, BlockState state ) {
        super( DWBlockEntities.DEADLY_SPAWNER.get(), pos, state );
        spawnerType = ((DeadlySpawnerBlock) state.getBlock()).getSpawnerType();
        spawner = new ProgressiveDelaySpawner( spawnerType, this, this::eventBroadcast );
    }
    
    private void eventBroadcast( Level level, BlockPos pos, int eventId ) {
        level.blockEvent( pos, DWBlocks.spawner( spawnerType ).get(), eventId, 0 );
    }
    
    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        spawner.load( level, worldPosition, loadTag );
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        spawner.save( saveTag );
    }
    
    public static void clientTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlySpawnerBlockEntity blockEntity ) {
        blockEntity.getSpawner().clientTick( level, pos );
    }
    
    public static void serverTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlySpawnerBlockEntity blockEntity ) {
        blockEntity.getSpawner().serverTick( (ServerLevel) level, pos );
    }
    
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();
        tag.remove( ProgressiveDelaySpawner.TAG_DYNAMIC_SPAWN_LIST );
        tag.remove( ProgressiveDelaySpawner.TAG_SPAWN_POTENTIALS );
        return tag;
    }
    
    @Override
    public boolean triggerEvent( int id, int type ) {
        return level != null && spawner.onEventTriggered( level, id ) ||
                super.triggerEvent( id, type );
    }
    
    @Override
    public boolean onlyOpCanSetNbt() { return true; }
    
    public void setEntityId( EntityType<?> entityType, RandomSource random ) {
        spawner.setEntityId( entityType, level, random, worldPosition );
    }
    
    public ProgressiveDelaySpawner getSpawner() { return spawner; }
    
    // TODO delete unused legacy code below
    
    //    @Override
    //    public SUpdateTileEntityPacket getUpdatePacket() {
    //        return new SUpdateTileEntityPacket( worldPosition, 0, getUpdateTag() );
    //    }
    
    //    // Initializing the tile entity here when it is safe to do so.
    //    @Override
    //    public void onLoad() {
    //        if( level == null ) {
    //            DeadlyWorld.LOG.error( "Failed to load deadly spawner block entity at \"{}\"", getBlockPos() );
    //            return;
    //        }
    //        if( getBlockState().getBlock() instanceof DeadlySpawnerBlock ) {
    //            DimensionConfigGroup dimConfigs = Config.getDimensionConfigs( level );
    //            SpawnerType spawnerType = ((DeadlySpawnerBlock) getBlockState().getBlock()).getSpawnerType();
    //
    //            initializeSpawner( spawnerType, dimConfigs );
    //        }
    //        else {
    //            DeadlyWorld.LOG.error( "Attempted to initialize Deadly Spawner tile entity with the wrong type of block! TileEntity:{}, Block:{}",
    //                    DeadlyWorld.toString( BlockEntityType.getKey( getType() ) ),
    //                    getBlockState().getBlock().toString() );
    //        }
    //    }
    
    //    private SpawnerType getSpawnerType() {
    //        if( level != null ) {
    //            Block block = level.getBlockState( worldPosition ).getBlock();
    //
    //            if( block instanceof DeadlySpawnerBlock ) {
    //                return ((DeadlySpawnerBlock) block).getSpawnerType();
    //            }
    //        }
    //        return SpawnerType.DEFAULT;
    //    }
    
    //    @Override
    //    public void onDataPacket( NetworkManager net, SUpdateTileEntityPacket pkt ) {
    //        if( level.isClientSide ) {
    //            super.handleUpdateTag( getBlockState(), pkt.getTag() );
    //
    //            CompoundNBT tag = pkt.getTag();
    //            setSpawnEntityFromTag( tag );
    //        }
    //    }
    
    //    private void setSpawnEntityFromTag( CompoundNBT tag ) {
    //        if( tag.contains( TAG_SPAWN_ENTITY, Constants.NBT.TAG_COMPOUND ) ) {
    //            CompoundNBT spawnEntityTag = tag.getCompound( TAG_SPAWN_ENTITY );
    //
    //            if( spawnEntityTag.contains( "id", Constants.NBT.TAG_STRING ) ) {
    //
    //                String line = spawnEntityTag.getString( "id" );
    //
    //                if( line.isEmpty() ) {
    //                    entityToSpawn = new WeightedSpawnerEntity();
    //                    cachedEntity = null;
    //                }
    //                else {
    //                    EntityType<?> entityType = EntityType.PIG;
    //                    ResourceLocation entityRegName = ResourceLocation.tryParse( line );
    //
    //                    if( entityRegName != null && ForgeRegistries.ENTITIES.containsKey( entityRegName ) ) {
    //                        entityType = ForgeRegistries.ENTITIES.getValue( entityRegName );
    //                    }
    //                    setEntityToSpawn( entityType );
    //                }
    //            }
    //        }
    //    }
    
    //    @Override
    //    public boolean triggerEvent( int id, int type ) {
    //        if( level.isClientSide ) {
    //            DeadlyWorld.LOG.warn( "Getting client event '{}:{}'", id, type );
    //
    //            if( id == EVENT_TIMER_RESET ) {
    //                spawnDelay = minSpawnDelay;
    //                return true;
    //            }
    //        }
    //        return super.triggerEvent( id, type );
    //    }
}