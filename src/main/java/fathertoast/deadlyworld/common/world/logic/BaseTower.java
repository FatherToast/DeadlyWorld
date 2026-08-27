package fathertoast.deadlyworld.common.world.logic;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.config.dimension.TowerConfig;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.util.TrapHelper;
import fathertoast.deadlyworld.common.world.levelgen.TowerDispenserSettings;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * The base logic for Deadly World's tower dispensers.
 */
public abstract class BaseTower {
    
    public enum State {
        /** The tower dispenser has exhausted its ammo/activations and is incapable of functioning. */
        DISABLED,
        /** The tower dispenser is on cooldown, temporarily incapable of functioning. */
        RESETTING,
        /** The tower dispenser is waiting to be activated. */
        READY,
        /** The tower dispenser is now active and firing. */
        ACTIVE
    }
    
    // Settings tags
    public static final String TAG_ACTIVATION_RANGE = "RequiredPlayerRange";
    public static final String TAG_CHECK_SIGHT = "CheckSight";
    public static final String TAG_MIN_ATTACK_DELAY = "MinAttackDelay";
    public static final String TAG_MAX_ATTACK_DELAY = "MaxAttackDelay";
    public static final String TAG_ATTACK_DAMAGE = "AttackDamage";
    public static final String TAG_PROJECTILE_SPEED = "ProjectileSpeed";
    public static final String TAG_PROJECTILE_VARIANCE = "ProjectileVariance";
    
    // Logic tags
    public static final String TAG_DELAY = "Delay";
    
    
    @Nullable
    private final Entity mobileEntity;
    @Nullable
    private final BlockEntity blockEntity;
    /** This is usually either the mobileEntity or blockEntity, but not required to be. */
    private final ITowerObject towerObject;
    
    // Settings
    /** True if line of sight is required to activate this tower dispenser. */
    protected boolean checkSight;
    /** The maximum distance at which players activates this tower dispenser. */
    protected double activationRange;
    /** The minimum ticks this tower dispenser takes to reset (become able to attack again after triggering). */
    protected int minAttackDelay;
    /** The maximum ticks this tower dispenser takes to reset (become able to attack again after triggering). */
    protected int maxAttackDelay;
    /** The amount of damage this tower's projectiles should deal to targets. -1 for towers that don't use this field. */
    protected float attackDamage;
    /** The speed to apply to the projectiles launched by this tower. */
    protected float projectileSpeed;
    /** The inaccuracy/offset of this tower's projectiles. */
    protected float projectileVariance;
    
    // Logic
    protected final TowerType towerType;
    /** The entity that activated this tower dispenser. Usually (but not always) non-null when triggering and null in all other states. */
    @Nullable
    protected Entity towerTarget;
    protected boolean disabled = false;
    
    /**
     * Counts up each tick until it hits -1 where the tower dispenser waits until to be activated (which sets this >= 0),
     * then counts up to the max trigger delay and triggers the tower dispenser (which sets this < 0).
     */
    protected int delay = -1;
    
    protected int targetCheckDelay = 0;
    
    @SuppressWarnings( "unused" ) // For possible future use
    public <T extends Entity & ITowerObject> BaseTower( TowerType towerType, T entity ) { this( towerType, entity, entity ); }
    
    public BaseTower( TowerType towerType, Entity entity, ITowerObject trapObj ) { this( towerType, entity, null, trapObj ); }
    
    public <T extends BlockEntity & ITowerObject> BaseTower( TowerType towerType, T block ) { this( towerType, block, block ); }
    
    public BaseTower( TowerType towerType, BlockEntity block, ITowerObject trapObj ) { this( towerType, null, block, trapObj ); }
    
    protected BaseTower( TowerType towerType, @Nullable Entity entity, @Nullable BlockEntity block, ITowerObject towerObj ) {
        this.towerType = towerType;
        mobileEntity = entity;
        blockEntity = block;
        towerObject = towerObj;
    }
    
    /** @return The current state of this tower dispenser. */
    public State getState() {
        if( disabled ) return State.DISABLED;
        if( delay == -1 ) return State.READY;
        return delay < -1 ? State.RESETTING : State.ACTIVE;
    }
    
    @Nullable
    public Level getLevel() { return blockEntity != null ? blockEntity.getLevel() : mobileEntity != null ? mobileEntity.level() : null; }
    
    public void initializeTower( WorldGenLevel level, BlockPos pos, RandomSource random, TowerDispenserSettings settings ) {
        final TowerConfig.TowerTypeCategory towerConfig = towerType.getConfig( level.getLevel() );
        DeadlyFeature.debugMarkerIfEnabled( level, pos, towerConfig );
        
        initializeTower( level.getLevel(), pos, random,
                settings.requiredPlayerRange().sample( random ),
                settings.checkSightChance().sample( random ),
                settings.attackDelay().getMinValue(),
                settings.attackDelay().getMaxValue(),
                settings.attackDamage().sample( random ),
                settings.projectileSpeed().sample( random ),
                settings.projectileVariance().sample( random )
        );
    }
    
    public void initializeTower( Level level, BlockPos pos, RandomSource random ) {
        final TowerConfig.TowerTypeCategory towerConfig = towerType.getConfig( level );
        initializeTower( level, pos, random,
                towerConfig.activationRange.get(), towerConfig.checkSightChance.getFloat(),
                towerConfig.attackDelay.getMin(), towerConfig.attackDelay.getMax(), towerConfig.attackDamage == null ? -1.0F : towerConfig.attackDamage.getFloat(),
                towerConfig.projectileSpeed.getFloat(), towerConfig.projectileVariance.getFloat()
        );
    }
    
    public void initializeTower( Level level, BlockPos pos, RandomSource random, double activationRange,
                                 float checkSightChance, int minAttackDelay, int maxAttackDelay, float attackDamage,
                                 float projectileSpeed, float projectileVariance ) {
        this.checkSight = roll( random, checkSightChance );
        this.activationRange = activationRange;
        this.minAttackDelay = minAttackDelay;
        this.maxAttackDelay = maxAttackDelay;
        this.attackDamage = attackDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileVariance = projectileVariance;
    }
    
    protected static boolean roll( RandomSource random, float chance ) { return chance >= 1.0F || chance > 0.0F && random.nextFloat() < chance; }
    
    public double getActivationRange() { return activationRange; }
    
    public void clientTick( Level level, BlockPos pos ) {}
    
    public void serverTick( ServerLevel level, BlockPos pos ) {
        switch( getState() ) {
            case DISABLED -> disabledTick( level, pos );
            case READY -> readyTick( level, pos );
            case RESETTING -> resettingTick( level, pos );
            case ACTIVE -> activeTick( level, pos );
        }
    }
    
    /** Called each server tick while this tower is disabled. */
    protected void disabledTick( ServerLevel level, BlockPos pos ) {}
    
    /** Called each server tick while this tower is resetting. */
    protected void resettingTick( ServerLevel level, BlockPos pos ) { delay++; }
    
    /** Called each server tick while this tower is ready. */
    protected void readyTick( ServerLevel level, BlockPos pos ) {
        if( targetCheckDelay > 0 ) {
            targetCheckDelay--;
            return;
        }
        
        Entity target = findTarget( level, pos );
        if( target == null ) {
            // Impose a longer delay if we use ray traces
            targetCheckDelay = checkSight ? 4 + level.random.nextInt( 7 ) : 2 + level.random.nextInt( 4 );
        }
        else {
            delay = maxAttackDelay <= 1 ? 0 : level.random.nextInt( maxAttackDelay );
            towerTarget = target;
        }
    }
    
    /** @return A target that meets the conditions to activate this tower, or null if none is found. */
    @Nullable
    protected Entity findTarget( ServerLevel level, BlockPos pos ) {
        return TrapHelper.getNearestTrapTargetInRange( level, pos, activationRange, checkSight );
    }
    
    @Nullable
    public Entity getTarget() {
        return towerTarget;
    }
    
    /** Called each server tick while this tower is activating. */
    protected void activeTick( ServerLevel level, BlockPos pos ) {
        delay++;
        if( delay < maxAttackDelay ) return;
        
        // Try to grab a target if we don't have one for whatever reason
        if( towerTarget == null ) towerTarget = findTarget( level, pos );
        // Something is not right, abort
        if( towerTarget == null ) return;
        
        // Calculate misc vectors
        Vec3 centerPos = new Vec3( pos.getX(), pos.getY(), pos.getZ() ).add( 0.5D, 0.5D, 0.5D );
        Vec3 targetPos = new Vec3( towerTarget.getX(), towerTarget.getBoundingBox().minY + towerTarget.getBbHeight() / 3.0F, towerTarget.getZ() );
        Vec3 vecToTarget = targetPos.subtract( centerPos );
        
        if( Math.abs( vecToTarget.x ) < 0.5D && Math.abs( vecToTarget.z ) < 0.5D ) {
            // Target is directly above or below the tower, can't hit it
            return;
        }
        double distanceH = Math.sqrt( vecToTarget.x * vecToTarget.x + vecToTarget.z * vecToTarget.z );
        
        // Determine the offset to spawn the arrow at so it doesn't clip the dispenser block
        Vec3 offset = getOffset( vecToTarget, distanceH );
        
        activateTower( level, pos, towerTarget, centerPos, offset, vecToTarget, distanceH );
        newAttackDelay( level.random );
        level.playSound( null, pos, DWSoundEvents.TOWER_DISPENSER_SHOOT.get(), SoundSource.BLOCKS, 1.0F, 1.0F );
    }
    
    private static Vec3 getOffset( Vec3 vecToTarget, double distanceH ) {
        Vec3 offset;
        if( Math.abs( vecToTarget.x ) < Math.abs( vecToTarget.z ) ) {
            offset = new Vec3(
                    vecToTarget.x / distanceH,
                    0.0,
                    vecToTarget.z < 0.0 ? -1.0 : 1.0
            );
        }
        else if( Math.abs( vecToTarget.x ) > Math.abs( vecToTarget.z ) ) {
            offset = new Vec3(
                    vecToTarget.x < 0.0 ? -1.0 : 1.0,
                    0.0,
                    vecToTarget.z / distanceH
            );
        }
        else {
            offset = new Vec3(
                    vecToTarget.x < 0.0 ? -1.0 : 1.0,
                    0.0,
                    vecToTarget.z < 0.0 ? -1.0 : 1.0
            );
        }
        return offset;
    }
    
    /** Disables this tower dispenser. */
    public void disableTower() {
        disabled = true;
        newAttackDelay( null );
    }
    
    /**
     * Resets the tower dispenser with a randomized duration (between minimum and maximum reset times).
     * If the random is null, the duration will be the maximum reset time.
     */
    public void newAttackDelay( @Nullable RandomSource random ) {
        delay = -1 - (random == null || maxAttackDelay <= minAttackDelay ? maxAttackDelay :
                minAttackDelay + random.nextInt( maxAttackDelay - minAttackDelay ));
        towerTarget = null;
    }
    
    /** Activates this tower. */
    public abstract void activateTower( ServerLevel level, BlockPos pos, Entity target,
                                        Vec3 center, Vec3 offset, Vec3 vecToTarget, double distance );
    
    /** Helper method for shooting arrows. */
    public void shootArrow( Vec3 center, Vec3 offset, Vec3 vecToTarget, double distanceH, float velocity, float variance, AbstractArrow arrow ) {
        final double spawnOffset = 0.6;
        
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        arrow.setBaseDamage( attackDamage / velocity );
        arrow.setPos( center.x + offset.x * spawnOffset, center.y, center.z + offset.z * spawnOffset );
        arrow.shoot( vecToTarget.x, vecToTarget.y + distanceH * 0.2F, vecToTarget.z, velocity, variance );
        
        getLevel().addFreshEntity( arrow );
    }
    
    public void load( @Nullable Level level, BlockPos pos, CompoundTag loadTag ) {
        if( NBTHelper.containsNumber( loadTag, TAG_ACTIVATION_RANGE ) )
            activationRange = loadTag.getFloat( TAG_ACTIVATION_RANGE );
        if( NBTHelper.containsNumber( loadTag, TAG_CHECK_SIGHT ) )
            checkSight = loadTag.getBoolean( TAG_CHECK_SIGHT );
        if( NBTHelper.containsNumber( loadTag, TAG_MIN_ATTACK_DELAY ) )
            minAttackDelay = loadTag.getShort( TAG_MIN_ATTACK_DELAY );
        if( NBTHelper.containsNumber( loadTag, TAG_MAX_ATTACK_DELAY ) )
            maxAttackDelay = loadTag.getShort( TAG_MAX_ATTACK_DELAY );
        if( maxAttackDelay < minAttackDelay ) maxAttackDelay = minAttackDelay;
        
        if( NBTHelper.containsNumber( loadTag, TAG_ATTACK_DAMAGE ) )
            attackDamage = loadTag.getShort( TAG_ATTACK_DAMAGE );
        if( NBTHelper.containsNumber( loadTag, TAG_PROJECTILE_SPEED ) )
            projectileSpeed = loadTag.getShort( TAG_PROJECTILE_SPEED );
        if( NBTHelper.containsNumber( loadTag, TAG_PROJECTILE_VARIANCE ) )
            projectileVariance = loadTag.getShort( TAG_PROJECTILE_VARIANCE );
        if( NBTHelper.containsNumber( loadTag, TAG_DELAY ) )
            delay = loadTag.getShort( TAG_DELAY );
    }
    
    public CompoundTag save( CompoundTag saveTag ) {
        saveTag.putFloat( TAG_ACTIVATION_RANGE, (float) activationRange );
        saveTag.putBoolean( TAG_CHECK_SIGHT, checkSight );
        saveTag.putShort( TAG_MIN_ATTACK_DELAY, (short) minAttackDelay );
        saveTag.putShort( TAG_MAX_ATTACK_DELAY, (short) maxAttackDelay );
        saveTag.putFloat( TAG_ATTACK_DAMAGE, attackDamage );
        saveTag.putFloat( TAG_PROJECTILE_SPEED, projectileSpeed );
        saveTag.putFloat( TAG_PROJECTILE_VARIANCE, projectileVariance );
        saveTag.putShort( TAG_DELAY, (short) delay );
        
        return saveTag;
    }
    
    public void broadcastEvent( Level level, BlockPos pos, int eventId ) {
        towerObject.broadcastEvent( this, level, pos, eventId );
    }
    
    public boolean onEventTriggered( Level level, int eventId ) {
        return false;
    }
}