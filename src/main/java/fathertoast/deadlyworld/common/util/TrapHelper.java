package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public final class TrapHelper {
    
    /** @return True if any player within range can activate a spawner, optionally requiring line-of-sight (ray trace). */
    public static boolean isPlayerInSpawnerRange( Level level, BlockPos pos, double range, boolean checkSight ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        for( int i = 0; i < level.players().size(); i++ ) {
            Player player = level.players().get( i );
            if( canActivateSpawner( player ) && player.distanceToSqr( x, y, z ) <= rangeSq &&
                    (!checkSight || canEntitySeeBlock( level, pos, player )) ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return An arbitrary player within range that can be targeted by a trap, optionally requiring line-of-sight (ray trace).
     * Null if there are no valid targets.
     */
    @Nullable
    public static Player getTrapTargetInRange( Level level, BlockPos pos, double range, boolean checkSight ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        for( int i = 0; i < level.players().size(); i++ ) {
            Player player = level.players().get( i );
            if( canTrapTarget( player ) && player.distanceToSqr( x, y, z ) <= rangeSq &&
                    (!checkSight || canEntitySeeBlock( level, pos, player )) ) {
                return player;
            }
        }
        return null;
    }
    
    /**
     * @return The closest player within range that can be targeted by a trap, optionally requiring line-of-sight (ray trace).
     * Null if there are no valid targets.
     */
    @Nullable
    public static Player getNearestTrapTargetInRange( Level level, BlockPos pos, double range, boolean checkSight ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        Player closestPlayer = null;
        double closestDistSq = Double.POSITIVE_INFINITY;
        
        for( Player player : level.players() ) {
            double distSq = player.distanceToSqr( x, y, z );
            
            if( canTrapTarget( player ) && distSq <= rangeSq && distSq < closestDistSq &&
                    (!checkSight || canEntitySeeBlock( level, pos, player )) ) {
                closestPlayer = player;
                closestDistSq = distSq;
            }
        }
        return closestPlayer;
    }
    
    /** @return True if the entity can activate a spawner. */
    public static boolean canActivateSpawner( Entity entity ) {
        return isTangible( entity ) && (Config.GLOBAL.GENERAL.activateSpawnersVsCreative.get() || isVulnerable( entity ));
    }
    
    /** @return True if the entity can be targeted by a trap. */
    public static boolean canTrapTarget( Entity entity ) {
        return (Config.GLOBAL.GENERAL.activateTrapsInPeaceful.get() || entity.level().getDifficulty() != Difficulty.PEACEFUL) &&
                isTangible( entity ) && (Config.GLOBAL.GENERAL.activateTrapsVsCreative.get() || isVulnerable( entity ));
    }
    
    /** @return True if the entity is vulnerable (not invulnerable nor a creative mode player). */
    public static boolean isVulnerable( Entity entity ) { return !entity.isInvulnerable() && (!(entity instanceof Player) || !((Player) entity).isCreative()); }
    
    /** @return True if the entity is tangible (not spectating nor dead). */
    public static boolean isTangible( Entity entity ) { return entity.isAlive() && !entity.isSpectator(); }
    
    /** @return True if the entity has clear line-of-sight to the block position. This is a ray trace, please use responsibly. */
    public static boolean canEntitySeeBlock( Level level, BlockPos pos, Entity entity ) {
        BlockHitResult result = level.clip( new ClipContext(
                new Vec3( entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ() ),
                new Vec3( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 ),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null ) );
        
        // No colliding blocks in the path or at the destination, can see
        return result.getType() == BlockHitResult.Type.MISS ||
                // Hit something, can see if the passed position (or the block above) is the hit
                (result.getBlockPos().equals( pos ) || result.getBlockPos().equals( pos.offset( 0, 1, 0 ) ));
    }
    
    public static boolean isSolidBlock( BlockGetter level, BlockPos pos ) {
        BlockState state = level.getBlockState( pos );
        
        for( Direction direction : Direction.values() ) {
            if( !state.isFaceSturdy( level, pos, direction ) )
                return false;
        }
        return true;
    }
    
    public static void setStackPotionColor( ItemStack potionStack ) {
        List<MobEffectInstance> effects = PotionUtils.getCustomEffects( potionStack );
        if( !effects.isEmpty() ) {
            int color = PotionUtils.getColor( effects );
            potionStack.getOrCreateTag().putInt( PotionUtils.TAG_CUSTOM_POTION_COLOR, color );
        }
    }
}