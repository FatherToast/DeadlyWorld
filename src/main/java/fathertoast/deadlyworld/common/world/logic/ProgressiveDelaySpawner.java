package fathertoast.deadlyworld.common.world.logic;

import fathertoast.crust.api.config.common.value.EntityList;
import fathertoast.crust.api.lib.LevelEventHelper;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.SpawnerConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Heavily modifies mob spawner logic to initially spawn at the max rate and slow down to the
 * min rate the longer it is active. This can be disabled by config/nbt.
 * This change is for the following goals:
 * - Reduces random difficulty spikes
 * - Makes spawners more difficult to rush
 * - Allows spawner encounters to be "beaten" by fighting enough
 * - Reduces mob farm potential (important since we generate so many spawners)
 * <p>
 * Adds some extra functionality as well, just because:
 * - Ignore light levels
 * - Optional line-of-sight check
 * - Can be given a limited number of spawns
 * - Better "spawn potentials" implementation
 */
public class ProgressiveDelaySpawner extends BaseSpawner {
    private static final int EVENT_TIMER_RESET = 1;
    
    // Settings tags
    public static final String TAG_CHECK_SIGHT = "CheckSight";
    public static final String TAG_DELAY_PROGRESSION = "DelayProgression";
    public static final String TAG_DELAY_RECOVERY = "DelayRecovery";
    // Logic tags
    public static final String TAG_DELAY_BUILDUP = "DelayBuildup";
    public static final String TAG_SPAWNS_REMAINING = "SpawnsRemaining";
    public static final String TAG_DYNAMIC_SPAWN_LIST = "DynamicSpawnList";
    // Vanilla tags
    public static final String TAG_SPAWN_POTENTIALS = "SpawnPotentials";
    
    
    private final SpawnerType spawnerType;
    @Nullable
    private final Entity mobileEntity;
    @Nullable
    private final BlockEntity blockEntity;
    private final TriConsumer<Level, BlockPos, Integer> eventBroadcaster;
    
    // Settings
    /** True if line of sight is required for spawning. */
    protected boolean checkSight;
    /** How many ticks to increase the delay time after each spawn batch (+/-10% variation applied). If 0, vanilla delay is used. */
    protected int spawnDelayProgression;
    /** The amount the delay time decreases each tick while no players are within range. */
    protected float spawnDelayRecovery;
    
    // Logic
    /** Number of entities this spawner can spawn. If negative, spawns are unlimited. */
    protected int spawnsRemaining;
    /** Weighted list of entities to pick from for each spawn batch, if present. */
    @Nullable
    protected EntityList dynamicSpawnList;
    
    /** Whether this spawner is active. Reduces the number of times we need to iterate over the player list. */
    protected boolean activated;
    /** Countdown until the next activation check. */
    protected int activationDelay;
    
    /** The spawn delay previously set; the core of the progressive delay logic. */
    protected float spawnDelayBuildup;
    
    @SuppressWarnings( "unused" ) // For possible future use
    public ProgressiveDelaySpawner( SpawnerType type, Entity entity, TriConsumer<Level, BlockPos, Integer> eventBroadcast ) {
        spawnerType = type;
        mobileEntity = entity;
        blockEntity = null;
        eventBroadcaster = eventBroadcast;
    }
    
    public ProgressiveDelaySpawner( SpawnerType type, BlockEntity block, TriConsumer<Level, BlockPos, Integer> eventBroadcast ) {
        spawnerType = type;
        mobileEntity = null;
        blockEntity = block;
        eventBroadcaster = eventBroadcast;
    }
    
    @Nullable
    public Level getLevel() { return blockEntity != null ? blockEntity.getLevel() : mobileEntity != null ? mobileEntity.level() : null; }
    
    public void initializeSpawner( Level level, BlockPos pos, RandomSource random ) {
        final SpawnerConfig.SpawnerTypeCategory spawnerConfig = spawnerType.getFeatureConfig( Config.getDimensionConfigs( level ) );
        
        // Set attributes from the config
        requiredPlayerRange = spawnerConfig.activationRange.get();
        checkSight = spawnerConfig.checkSight.get();
        maxNearbyEntities = spawnerConfig.maxNearbyEntities.get();
        
        minSpawnDelay = spawnerConfig.delay.getMin();
        maxSpawnDelay = spawnerConfig.delay.getMax();
        spawnDelayProgression = spawnerConfig.delayProgression.get();
        spawnDelayRecovery = (float) spawnerConfig.delayRecovery.get();
        
        spawnCount = spawnerConfig.spawnCount.get();
        spawnRange = spawnerConfig.spawnRange.get();
        
        // Initialize logic
        spawnsRemaining = spawnerConfig.maxSpawns.get();
        if( spawnsRemaining == 0 )
            spawnsRemaining = -1; // 0 would have no meaning in the config, but here it means "disabled"
        if( random.nextFloat() < spawnerConfig.dynamicChance.get() ) {
            dynamicSpawnList = spawnerConfig.spawnList.get();
        }
        else {
            dynamicSpawnList = null;
        }
        
        EntityType<?> toSpawn = EntityType.ZOMBIE;//spawnerConfig.spawnList.get().next( random );
        setEntityId( toSpawn, level, random,
                blockEntity == null ? BlockPos.ZERO : blockEntity.getBlockPos() );
    }
    
    /** Increments the number of mobs this is allowed to spawn, if it has limited spawns. */
    public void addSpawn() { if( spawnsRemaining >= 0 ) spawnsRemaining++; }
    
    @Override // Overridden just to allow nullable entity type
    public void setEntityId( @Nullable EntityType<?> entityType, @Nullable Level level, RandomSource random, BlockPos pos ) {
        if( entityType == null ) nextSpawnData = null;
        else super.setEntityId( entityType, level, random, pos );
    }
    
    @Override
    public void clientTick( Level level, BlockPos pos ) {
        if( spawnsRemaining == 0 ) return;
        updateActivationStatus( level, pos );//TODO Legacy logic, is this needed?
        super.clientTick( level, pos );
    }
    
    @Override
    public void serverTick( ServerLevel level, BlockPos pos ) {
        if( spawnsRemaining == 0 ) return;
        updateActivationStatus( level, pos );
        
        if( activated ) {
            if( spawnDelay < 0 ) {
                delay( level, pos, false );
            }
            
            if( spawnDelay > 0 ) {
                DeadlyWorld.LOG.error( "Delay: {}", spawnDelay );
                // Spawner is on cooldown
                spawnDelay--;
            }
            else if( checkSight && !TrapHelper.isValidPlayerInRange( level, pos, requiredPlayerRange, true, true ) ) {
                // Failed sight check; impose a small delay, so we don't spam ray traces
                spawnDelay = 6 + level.random.nextInt( 10 );
                DeadlyWorld.LOG.error( "Failed sight check?" );
            }
            else {
                DeadlyWorld.LOG.error( "le spawn" );
                // Attempt spawning
                doSpawn( level, pos );
            }
        }
        else if( spawnDelayBuildup > minSpawnDelay ) {
            // Decrement the progressive delay buildup
            spawnDelayBuildup = Math.max( spawnDelayBuildup - spawnDelayRecovery, minSpawnDelay );
        }
    }
    
    private void updateActivationStatus( Level level, BlockPos pos ) {
        if( activationDelay > 0 ) {
            activationDelay--;
        }
        else {
            activationDelay = 4;
            activated = TrapHelper.isValidPlayerInRange( level, pos, requiredPlayerRange, false, false );
            DeadlyWorld.LOG.error( "Active: {}", activated );
        }
    }
    
    private void doSpawn( ServerLevel level, BlockPos pos ) {
        DeadlyWorld.LOG.error( "Doing spawn" );
        final DimensionConfigGroup dimConfigs = Config.getDimensionConfigs( level );
        RandomSource random = level.getRandom();
        
        // Decide what kind of entity to spawn (mostly moved out of the loop)
        SpawnData spawnData = getOrCreateNextSpawnData( level, random, pos );
        CompoundTag entityTag = spawnData.getEntityToSpawn();
        Optional<EntityType<?>> optional = EntityType.by( entityTag );
        if( optional.isEmpty() ) {
            delay( level, pos, false );
            return;
        }
        EntityType<?> entityType = optional.get();
        
        // Spawn a batch of mobs
        int spawns = 0;
        for( int i = 0; i < spawnCount; i++ ) {
            DeadlyWorld.LOG.error( "Spawn attempt {}", i );
            // Pick random location
            ListTag posTag = entityTag.getList( "Pos", Tag.TAG_DOUBLE );
            int coords = posTag.size();
            double x = coords >= 1 ? posTag.getDouble( 0 ) : pos.getX() + 0.5 + (random.nextDouble() - random.nextDouble()) * spawnRange;
            double y = coords >= 2 ? posTag.getDouble( 1 ) : pos.getY() + random.nextInt( 3 ) - 1;
            double z = coords >= 3 ? posTag.getDouble( 2 ) : pos.getZ() + 0.5 + (random.nextDouble() - random.nextDouble()) * spawnRange;
            
            // Check if the entity type is okay to spawn here
            if( !level.noCollision( entityType.getAABB( x, y, z ) ) )
                continue;
            BlockPos spawnPos = BlockPos.containing( x, y, z );
            if( spawnData.getCustomSpawnRules().isPresent() ) {
                if( !entityType.getCategory().isFriendly() && level.getDifficulty() == Difficulty.PEACEFUL ) {
                    delay( level, pos, spawns > 0 );
                    return; // This isn't going to change... cancel spawn batch
                }
                
                // Commented out to ignore light level check; we can make this a config option later if desired
                //SpawnData.CustomSpawnRules customSpawnRules = spawnData.getCustomSpawnRules().get();
                //if( !customSpawnRules.blockLightLimit().isValueInRange( level.getBrightness( LightLayer.BLOCK, spawnPos ) ) ||
                //        !customSpawnRules.skyLightLimit().isValueInRange( level.getBrightness( LightLayer.SKY, spawnPos ) ) )
                //    continue;
            }
            else if( !SpawnPlacements.checkSpawnRules( entityType, level, MobSpawnType.SPAWNER, spawnPos, level.getRandom() ) ) {
                continue; // Can't spawn here, but maybe another spot works
            }
            
            // Create the entity
            Entity entity = EntityType.loadEntityRecursive( entityTag, level, ( passenger ) -> {
                passenger.moveTo( x, y, z, passenger.getYRot(), passenger.getXRot() );
                return passenger;
            } );
            if( entity == null ) {
                delay( level, pos, spawns > 0 );
                return; // Yikes
            }
            
            // Do max nearby entities check
            if( maxNearbyEntities > 0 ) {
                int nearbyEntities = level.getEntitiesOfClass( entity.getClass(), new AABB(
                        pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
                ).inflate( spawnRange ) ).size();
                if( nearbyEntities >= maxNearbyEntities ) {
                    delay( level, pos, spawns > 0 );
                    return; // We're done with this spawn batch
                }
            }
            
            // Actually spawn the entity
            entity.moveTo( entity.getX(), entity.getY(), entity.getZ(), random.nextFloat() * 360.0F, 0.0F );
            if( entity instanceof Mob mob ) {
                if( !ForgeEventFactory.checkSpawnPositionSpawner( mob, level, MobSpawnType.SPAWNER, spawnData, this ) )
                    continue;
                
                MobSpawnEvent.FinalizeSpawn event = ForgeEventFactory.onFinalizeSpawnSpawner( mob, level,
                        level.getCurrentDifficultyAt( mob.blockPosition() ), null, entityTag, this );
                if( event != null && spawnData.getEntityToSpawn().size() == 1 && NBTHelper.containsString( spawnData.getEntityToSpawn(), Entity.ID_TAG ) ) {
                    // These are expected for parity with vanilla code
                    //noinspection deprecation, OverrideOnly
                    mob.finalizeSpawn( level, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag() );
                }
            }
            if( entity instanceof LivingEntity living ) {
                // Apply configured attribute modifiers, potions, etc.
                spawnerType.initEntity( living, dimConfigs, level, pos );
            }
            if( !level.tryAddFreshEntityWithPassengers( entity ) ) {
                delay( level, pos, spawns > 0 );
                return; // Uh what, probably not good
            }
            
            // Tell client that we did the thing
            LevelEventHelper.SMOKE_AND_FLAME.play( level, pos );
            level.gameEvent( entity, GameEvent.ENTITY_PLACE, spawnPos );
            if( entity instanceof Mob ) ((Mob) entity).spawnAnim();
            
            // Keep track of successful spawns
            spawns++;
            DeadlyWorld.LOG.error( "Successfully spawned #{}", spawns );
            if( spawnsRemaining > 0 ) {
                spawnsRemaining--;
                if( spawnsRemaining <= 0 ) {
                    disableSpawner();
                    break;
                }
            }
        }
        
        // Nothing went horribly wrong; only delay if we actually spawned something
        if( spawns > 0 ) {
            delay( level, pos, true );
        }
    }
    
    protected void delay( Level level, BlockPos pos, boolean incrProgressiveDelay ) {
        if( spawnsRemaining == 0 ) return;
        
        if( maxSpawnDelay <= minSpawnDelay ) {
            // Spawn delay is a constant
            spawnDelay = minSpawnDelay;
        }
        else if( spawnDelayProgression <= 0 ) {
            // Progressive delay is disabled, use vanilla logic
            spawnDelay = minSpawnDelay + level.random.nextInt( maxSpawnDelay - minSpawnDelay );
        }
        else {
            // Reset timer based on progressive delay
            if( spawnDelayBuildup < minSpawnDelay )
                spawnDelayBuildup = minSpawnDelay;
            
            spawnDelay = (int) spawnDelayBuildup;
            
            if( incrProgressiveDelay ) {
                spawnDelayBuildup = Math.min( maxSpawnDelay,
                        spawnDelayBuildup + spawnDelayProgression * (1.0F + 0.2F * (level.random.nextFloat() - 0.5F)) );
            }
        }
        
        if( dynamicSpawnList != null /*&& !dynamicSpawnList.isEmpty()*/ ) {
            EntityType<?> nextType = EntityType.ZOMBIE;//dynamicSpawnList.next( level.random );
            
            if( nextType == null ) {
                DeadlyWorld.LOG.warn( "Failed to fetch next random entity entry in a weighted entity list. Could the total weight be 0?" );
            }
            else {
                setEntityId( nextType, level, level.random, pos );
            }
        }
        else {
            spawnPotentials.getRandom( level.random ).ifPresent( ( spawnData ) ->
                    setNextSpawnData( level, pos, spawnData.getData() ) );
        }
        broadcastEvent( level, pos, EVENT_TIMER_RESET );
    }
    
    public void disableSpawner() {
        spawnsRemaining = 0;
        activated = false;
    }
    
    @Override
    public void load( @Nullable Level level, BlockPos pos, CompoundTag loadTag ) {
        if( NBTHelper.containsNumber( loadTag, TAG_CHECK_SIGHT ) )
            checkSight = loadTag.getBoolean( TAG_CHECK_SIGHT );
        if( NBTHelper.containsNumber( loadTag, TAG_DELAY_PROGRESSION ) )
            spawnDelayProgression = loadTag.getShort( TAG_DELAY_PROGRESSION );
        if( NBTHelper.containsNumber( loadTag, TAG_DELAY_RECOVERY ) )
            spawnDelayRecovery = loadTag.getFloat( TAG_DELAY_RECOVERY );
        
        if( NBTHelper.containsNumber( loadTag, TAG_DELAY_BUILDUP ) )
            spawnDelayBuildup = loadTag.getFloat( TAG_DELAY_BUILDUP );
        if( NBTHelper.containsNumber( loadTag, TAG_SPAWNS_REMAINING ) )
            spawnsRemaining = loadTag.getShort( TAG_SPAWNS_REMAINING );
        //TODO load dynamicSpawnList
        
        //        if( dynamicSpawnList != null && !dynamicSpawnList.isEmpty() ) {
        //            CompoundNBT spawnListTag = new CompoundNBT();
        //            int tagIndex = 0;
        //
        //            for( EntityEntry entry : dynamicSpawnList.getAllEntries() ) {
        //                // Assuming the first index of the
        //                // entry's value set is the weight.
        //                String stringEntry = entry.TYPE.get().getRegistryName().toString() + " " + entry.VALUES[0];
        //                spawnListTag.putString( String.valueOf( tagIndex ), stringEntry );
        //                ++tagIndex;
        //            }
        //            compound.put( TAG_DYNAMIC_SPAWN_LIST, spawnListTag );
        //        }
        
        super.load( level, pos, loadTag );
    }
    
    @Override
    public CompoundTag save( CompoundTag saveTag ) {
        saveTag.putBoolean( TAG_CHECK_SIGHT, checkSight );
        saveTag.putShort( TAG_DELAY_PROGRESSION, (short) spawnDelayProgression );
        saveTag.putFloat( TAG_DELAY_RECOVERY, spawnDelayRecovery );
        
        saveTag.putFloat( TAG_DELAY_BUILDUP, spawnDelayBuildup );
        saveTag.putShort( TAG_SPAWNS_REMAINING, (short) spawnsRemaining );
        //TODO save dynamicSpawnList
        
        //        if( tag.contains( TAG_DYNAMIC_SPAWN_LIST, Constants.NBT.TAG_COMPOUND ) ) {
        //            CompoundNBT spawnListTag = tag.getCompound( TAG_DYNAMIC_SPAWN_LIST );
        //
        //            dynamicSpawnList = WeightedEntityList.loadFromNBT( spawnListTag );
        //        }
        
        return super.save( saveTag );
    }
    
    @Override
    public void setNextSpawnData( @Nullable Level level, BlockPos pos, SpawnData spawnData ) {
        super.setNextSpawnData( level, pos, spawnData );
        if( blockEntity != null && level != null ) {
            BlockState state = level.getBlockState( pos );
            level.sendBlockUpdated( pos, state, state, Block.UPDATE_NONE );
        }
    }
    
    @Override
    protected SpawnData getOrCreateNextSpawnData( @Nullable Level level, RandomSource random, BlockPos pos ) {
        if( nextSpawnData == null ) {
            setNextSpawnData( level, pos, spawnPotentials.getRandom( random ).map( WeightedEntry.Wrapper::getData ).orElseGet( SpawnData::new ) );
        }
        return nextSpawnData;
    }
    
    @Override
    public void broadcastEvent( Level level, BlockPos pos, int eventId ) { eventBroadcaster.accept( level, pos, eventId ); }
    
    @Override
    @Nullable
    public Entity getSpawnerEntity() { return mobileEntity; }
    
    @Override
    @Nullable
    public BlockEntity getSpawnerBlockEntity() { return blockEntity; }
}