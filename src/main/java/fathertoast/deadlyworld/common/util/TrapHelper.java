package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
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
import java.util.function.Predicate;

public class TrapHelper {
    
    public static final Predicate<Entity> ATTACK_ALLOWED_PEACEFUL = ( entity ) -> !(entity instanceof Player) ||
            !entity.isSpectator() && !((Player) entity).isCreative();
    
    public static boolean isValidPlayerInRange( Level level, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        for( int i = 0; i < level.players().size(); i++ ) {
            Player player = level.players().get( i );
            if( isValidTarget( player, requireVulnerable ) &&
                    player.distanceToSqr( x, y, z ) <= rangeSq &&
                    (!checkSight || canEntitySeeBlock( level, pos, player ))
            ) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidTrapPlayerInRange( Level level, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        for( int i = 0; i < level.players().size(); i++ ) {
            Player player = level.players().get( i );
            if( isValidTrapTarget( player, requireVulnerable ) &&
                    player.distanceToSqr( x, y, z ) <= rangeSq &&
                    (!checkSight || canEntitySeeBlock( level, pos, player ))
            ) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    public static Player getNearestValidPlayerInRange( Level level, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        Player closestPlayer = null;
        double closestDistSq = Double.POSITIVE_INFINITY;
        
        for( Player player : level.players() ) {
            double distSq = player.distanceToSqr( x, y, z );
            
            if( isValidTarget( player, requireVulnerable ) &&
                    distSq <= rangeSq && distSq < closestDistSq &&
                    (!checkSight || canEntitySeeBlock( level, pos, player )) ) {
                closestPlayer = player;
                closestDistSq = distSq;
            }
        }
        return closestPlayer;
    }
    
    /** Used for traps/devices that don't spawn any monsters. */
    @Nullable
    public static Player getNearestTrapValidPlayerInRange( Level level, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        Player closestPlayer = null;
        double closestDistSq = Double.POSITIVE_INFINITY;
        
        for( Player player : level.players() ) {
            double distSq = player.distanceToSqr( x, y, z );
            
            if( isValidTrapTarget( player, requireVulnerable ) &&
                    distSq <= rangeSq && distSq < closestDistSq &&
                    (!checkSight || canEntitySeeBlock( level, pos, player )) ) {
                closestPlayer = player;
                closestDistSq = distSq;
            }
        }
        return closestPlayer;
    }
    
    public static boolean isValidTrapTarget( Entity entity, boolean requireVulnerable ) {
        final boolean allowInPeaceful = Config.GENERAL.GENERAL.activateTrapsInPeaceful.get();
        return requireVulnerable
                ? (allowInPeaceful ? ATTACK_ALLOWED_PEACEFUL.test( entity ) : EntitySelector.NO_CREATIVE_OR_SPECTATOR.test( entity ))
                : EntitySelector.NO_SPECTATORS.test( entity );
    }
    
    public static boolean isValidTarget( Entity entity, boolean requireVulnerable ) {
        return requireVulnerable ? EntitySelector.NO_CREATIVE_OR_SPECTATOR.test( entity ) : EntitySelector.NO_SPECTATORS.test( entity );
    }
    
    
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
    
    // Utility class, instantiating not needed
    private TrapHelper() { }
}