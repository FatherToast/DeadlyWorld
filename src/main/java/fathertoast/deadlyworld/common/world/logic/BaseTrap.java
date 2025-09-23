package fathertoast.deadlyworld.common.world.logic;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.DecoyType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.SpawnerConfig;
import fathertoast.deadlyworld.common.config.dimension.TrapConfig;
import fathertoast.deadlyworld.common.core.registry.DWDecoyTypes;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.common.util.TrapHelper;
import fathertoast.deadlyworld.common.world.levelgen.DeadlyFeature;
import fathertoast.deadlyworld.common.world.levelgen.settings.FloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.settings.SpawnerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;

/**
 * The base logic for Deadly World's traps.
 */
public abstract class BaseTrap {

    public enum State {
        /** The trap has exhausted its ammo/activations and is incapable of functioning. */
        DISABLED,
        /** The trap is on cooldown, temporarily incapable of functioning. */
        RESETTING,
        /** The trap is waiting to be tripped. */
        READY,
        /** The trap has been tripped and is now functioning. */
        TRIGGERING
    }
    
    // Settings tags
    public static final String TAG_ACTIVATION_RANGE = "RequiredPlayerRange";
    public static final String TAG_CHECK_SIGHT = "CheckSight";
    public static final String TAG_MIN_RESET_TIME = "MinResetTime";
    public static final String TAG_MAX_RESET_TIME = "MaxResetTime";
    public static final String TAG_MAX_TRIGGER_DELAY = "MaxTriggerDelay";
    public static final String TAG_DECOY_TYPE = "DecoyType";
    // Logic tags
    public static final String TAG_TRIGGERS_REMAINING = "TriggersRemaining";
    public static final String TAG_DELAY = "Delay";
    
    @Nullable
    private final Entity mobileEntity;
    @Nullable
    private final BlockEntity blockEntity;
    /** This is usually either the mobileEntity or blockEntity, but not required to be. */
    private final ITrapObject trapObject;
    
    // Settings
    /** True if line of sight is required to trip this trap. */
    protected boolean checkSight;
    /** The maximum distance at which players trip this trap. */
    protected double activationRange;
    /** The minimum ticks this trap takes to reset (become able to trip again after triggering). */
    protected int minResetTime;
    /** The maximum ticks this trap takes to reset (become able to trip again after triggering). */
    protected int maxResetTime;
    /** The maximum ticks this trap takes to trigger after tripping. */
    protected int maxTriggerDelay;
    /** The decoy type for this trap. Can be null */
    @Nullable
    protected DecoyType decoyType;

    // Logic
    protected final TrapType trapType;

    /** The entity that tripped this trap. Usually (but not always) non-null when triggering and null in all other states. */
    @Nullable
    protected Entity tripEntity;
    /** Number of times this trap can trigger. If negative, triggers are unlimited. */
    protected int triggersRemaining;
    /**
     * Counts up each tick until it hits -1 where the trap waits until to be tripped (which sets this >= 0),
     * then counts up to the max trigger delay and triggers the trap (which sets this < 0).
     */
    protected int delay = -1;
    
    /** Countdown until the next trip check. Reduces the frequency of ray tracing and player list iteration. */
    protected int tripCheckDelay;
    
    @SuppressWarnings( "unused" ) // For possible future use
    public <T extends Entity & ITrapObject> BaseTrap( TrapType trapType, T entity ) { this( trapType, entity, entity ); }
    
    public BaseTrap( TrapType trapType, Entity entity, ITrapObject trapObj ) { this( trapType, entity, null, trapObj ); }
    
    public <T extends BlockEntity & ITrapObject> BaseTrap( TrapType trapType, T block ) { this( trapType, block, block ); }
    
    public BaseTrap( TrapType trapType, BlockEntity block, ITrapObject trapObj ) { this( trapType, null, block, trapObj ); }
    
    protected BaseTrap( TrapType trapType, @Nullable Entity entity, @Nullable BlockEntity block, ITrapObject trapObj ) {
        this.trapType = trapType;
        mobileEntity = entity;
        blockEntity = block;
        trapObject = trapObj;
    }
    
    /** @return The current state of this trap. */
    public State getState() {
        if( triggersRemaining == 0 ) return State.DISABLED;
        if( delay == -1 ) return State.READY;
        return delay < -1 ? State.RESETTING : State.TRIGGERING;
    }
    
    @Nullable
    public Level getLevel() { return blockEntity != null ? blockEntity.getLevel() : mobileEntity != null ? mobileEntity.level() : null; }

    public void initializeTrap( WorldGenLevel level, BlockPos pos, RandomSource random, FloorTrapSettings trapSettings ) {
        final TrapConfig.TrapTypeCategory trapConfig = trapType.getFeatureConfig( Config.getDimensionConfigs( level.getLevel() ) );
        DeadlyFeature.debugMarkerIfEnabled( level, pos, trapConfig );

        initializeTrap( getLevel(), pos, random,
                trapSettings.requiredPlayerRange().sample( random ),
                trapSettings.checkSightChance().sample( random ),
                trapSettings.resetTime().getMinValue(),
                trapSettings.resetTime().getMaxValue(),
                trapSettings.triggersRemaining().sample( random ),
                roll( random, trapSettings.decoyChance().sample( random ) )
        );
    }

    public void initializeTrap( @Nullable Level level, BlockPos pos, RandomSource random ) {
        final TrapConfig.TrapTypeCategory trapConfig = trapType.getFeatureConfig( Config.getDimensionConfigs( level ) );
        initializeTrap( level, pos, random,
                trapConfig.activationRange.get(), (float) trapConfig.checkSightChance.get(),
                trapConfig.resetTime.getMin(), trapConfig.resetTime.getMax(), trapConfig.triggersRemaining.get(), trapConfig.decoyChance.rollChance( random )
        );
    }

    public void initializeTrap( @Nullable Level level, BlockPos pos, RandomSource random, double activationRange,
                                float checkSightChance, int minResetTime, int maxResetTime, int triggersRemaining, boolean spawnDecoy ) {
        this.checkSight = roll( random, checkSightChance );
        this.activationRange = activationRange;
        this.minResetTime = minResetTime;
        this.maxResetTime = maxResetTime;
        this.triggersRemaining = triggersRemaining;

        if ( spawnDecoy ) {
            // Pick a decoy suitable for the dimension we are in if level is not null
            if ( level != null ) {
                ResourceKey<Level> dimension = level.dimension();
                ITag<DecoyType> tag;

                if ( dimension.equals( Level.OVERWORLD ) ) {
                    tag = DWRegistries.DECOY_TYPE_REGISTRY.get().tags().getTag( DWTags.DecoyTypes.OVERWORLD );
                }
                else if ( dimension.equals( Level.NETHER ) ) {
                    tag = DWRegistries.DECOY_TYPE_REGISTRY.get().tags().getTag( DWTags.DecoyTypes.THE_NETHER );
                }
                else {
                    tag = DWRegistries.DECOY_TYPE_REGISTRY.get().tags().getTag( DWTags.DecoyTypes.ANY_DIMENSION );
                }
                this.decoyType = tag.getRandomElement( random ).orElse( DWDecoyTypes.PIG.get() );
            }
            else {
                this.decoyType = DWDecoyTypes.getRandomType(random);
            }
        }
    }

    protected static boolean roll( RandomSource random, float chance ) { return chance >= 1.0F || chance > 0.0F && random.nextFloat() < chance; }

    public double getActivationRange() { return activationRange; }
    
    public void clientTick( Level level, BlockPos pos ) { }
    
    public void serverTick( ServerLevel level, BlockPos pos ) {
        switch( getState() ) {
            case DISABLED -> disabledTick( level, pos );
            case READY -> readyTick( level, pos );
            case RESETTING -> resettingTick( level, pos );
            case TRIGGERING -> triggeringTick( level, pos );
        }
    }
    
    /** Called each server tick while this trap is disabled. */
    protected void disabledTick( ServerLevel level, BlockPos pos ) { }
    
    /** Called each server tick while this trap is resetting. */
    protected void resettingTick( ServerLevel level, BlockPos pos ) { delay++; }
    
    /** Called each server tick while this trap is ready. */
    protected void readyTick( ServerLevel level, BlockPos pos ) {
        if( tripCheckDelay > 0 ) {
            tripCheckDelay--;
            return;
        }
        
        Entity target = findTripTarget( level, pos );
        if( target == null ) {
            // Impose a longer delay if we use ray traces
            tripCheckDelay = checkSight ? 4 + level.random.nextInt( 7 ) : 2 + level.random.nextInt( 4 );
        }
        else {
            tripTrap( level, pos, target );
        }
    }
    
    /** @return A target that meets the conditions to trip this trap, or null if none is found. */
    @Nullable
    protected Entity findTripTarget( ServerLevel level, BlockPos pos ) {
        return TrapHelper.getTrapTargetInRange( level, pos, activationRange, checkSight );
    }

    @Nullable
    public Entity getTripTarget() {
        return tripEntity;
    }
    
    /** Called each server tick while this trap is triggering. */
    protected void triggeringTick( ServerLevel level, BlockPos pos ) {
        delay++;
        if( delay < maxTriggerDelay ) return;
        
        // Try to grab a target if we don't have one for whatever reason
        if( tripEntity == null ) tripEntity = findTripTarget( level, pos );
        
        triggerTrap( level, pos );
        
        if( triggersRemaining > 0 ) {
            triggersRemaining--;
            if( triggersRemaining <= 0 ) {
                disableTrap();
                return;
            }
        }
        resetTrap( level.random );
    }
    
    /** Disables this trap. */
    public void disableTrap() {
        triggersRemaining = 0;
        resetTrap( null );
    }
    
    /**
     * Resets the trap with a randomized duration (between minimum and maximum reset times).
     * If the random is null, the duration will be the maximum reset time.
     */
    public void resetTrap( @Nullable RandomSource random ) {
        delay = -1 - (random == null || maxResetTime <= minResetTime ? maxResetTime :
                minResetTime + random.nextInt( maxResetTime - minResetTime ));
        tripEntity = null;
    }
    
    /** Trips this trap. */
    public void tripTrap( ServerLevel level, BlockPos pos, @Nullable Entity target ) {
        delay = maxTriggerDelay <= 1 ? 0 : level.random.nextInt( maxTriggerDelay );
        tripEntity = target;
    }
    
    /** Triggers this trap. */
    public abstract void triggerTrap( ServerLevel level, BlockPos pos );
    
    public void load( @Nullable Level level, BlockPos pos, CompoundTag loadTag ) {
        if( NBTHelper.containsNumber( loadTag, TAG_ACTIVATION_RANGE ) )
            activationRange = loadTag.getFloat( TAG_ACTIVATION_RANGE );
        if( NBTHelper.containsNumber( loadTag, TAG_CHECK_SIGHT ) )
            checkSight = loadTag.getBoolean( TAG_CHECK_SIGHT );
        if( NBTHelper.containsNumber( loadTag, TAG_MIN_RESET_TIME ) )
            minResetTime = loadTag.getShort( TAG_MIN_RESET_TIME );
        if( NBTHelper.containsNumber( loadTag, TAG_MAX_RESET_TIME ) )
            maxResetTime = loadTag.getShort( TAG_MAX_RESET_TIME );
        if( maxResetTime < minResetTime )
            maxResetTime = minResetTime;
        if( NBTHelper.containsNumber( loadTag, TAG_MAX_TRIGGER_DELAY ) )
            maxTriggerDelay = loadTag.getShort( TAG_MAX_TRIGGER_DELAY );
        if ( NBTHelper.containsString( loadTag, TAG_DECOY_TYPE ) ) {
            ResourceLocation typeId = ResourceLocation.tryParse( loadTag.getString( TAG_DECOY_TYPE ) );

            if ( typeId != null )
                decoyType = DWRegistries.DECOY_TYPE_REGISTRY.get().getValue( typeId );
        }
        
        if( NBTHelper.containsNumber( loadTag, TAG_TRIGGERS_REMAINING ) )
            triggersRemaining = loadTag.getShort( TAG_TRIGGERS_REMAINING );
        if( NBTHelper.containsNumber( loadTag, TAG_DELAY ) )
            delay = loadTag.getShort( TAG_DELAY );
    }
    
    public CompoundTag save( CompoundTag saveTag ) {
        saveTag.putFloat( TAG_ACTIVATION_RANGE, (float) activationRange );
        saveTag.putBoolean( TAG_CHECK_SIGHT, checkSight );
        saveTag.putShort( TAG_MIN_RESET_TIME, (short) minResetTime );
        saveTag.putShort( TAG_MAX_RESET_TIME, (short) maxResetTime );
        saveTag.putShort( TAG_MAX_TRIGGER_DELAY, (short) maxTriggerDelay );

        if ( decoyType != null ) {
            ResourceLocation typeId = DWRegistries.DECOY_TYPE_REGISTRY.get().getKey( decoyType );
            if ( typeId != null )
                saveTag.putString( TAG_DECOY_TYPE, typeId.toString() );
        }
        
        saveTag.putShort( TAG_TRIGGERS_REMAINING, (short) triggersRemaining );
        saveTag.putShort( TAG_DELAY, (short) delay );
        
        return saveTag;
    }

    public void writeToUpdateTag( CompoundTag updateTag ) {
        if ( decoyType != null ) {
            ResourceLocation typeId = DWRegistries.DECOY_TYPE_REGISTRY.get().getKey( decoyType );
            if ( typeId != null )
                updateTag.putString( TAG_DECOY_TYPE, typeId.toString() );
        }
    }
    
    public void broadcastEvent( Level level, BlockPos pos, int eventId ) {
        trapObject.broadcastEvent( this, level, pos, eventId );
    }
    
    public boolean onEventTriggered( Level level, int eventId ) {
        return false;
    }
    
    @Nullable
    public Entity getSpawnerEntity() { return mobileEntity; }
    
    @Nullable
    public BlockEntity getSpawnerBlockEntity() { return blockEntity; }

    @Nullable
    public DecoyType getDecoyType() {
        return decoyType;
    }
}