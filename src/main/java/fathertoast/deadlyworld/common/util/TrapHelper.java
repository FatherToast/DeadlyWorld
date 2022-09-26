package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.util.WeightedPotionList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class TrapHelper {

    public static final Predicate<Entity> ATTACK_ALLOWED_PEACEFUL = (entity) -> !(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative();

    public static final int NBT_TYPE_PRIMITIVE = Constants.NBT.TAG_ANY_NUMERIC;
    public static final int NBT_TYPE_STRING = Constants.NBT.TAG_STRING;


    public static boolean isValidPlayerInRange( World world, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        double rangeSq = range * range;
        for( int i = 0; i < world.players().size( ); i++ ) {
            PlayerEntity player = world.players().get( i );
            if( isValidTarget(player, requireVulnerable) &&
                    player.distanceToSqr(x, y, z) <= rangeSq &&
                    (!checkSight || canEntitySeeBlock( world, pos, player ))
            )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidTrapPlayerInRange( World world, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        double rangeSq = range * range;
        for( int i = 0; i < world.players().size( ); i++ ) {
            PlayerEntity player = world.players().get( i );
            if( isValidTrapTarget( player, requireVulnerable ) &&
                    player.distanceToSqr(x, y, z) <= rangeSq &&
                    (!checkSight || canEntitySeeBlock( world, pos, player ))
            )
            {
                return true;
            }
        }
        return false;
    }

    public static PlayerEntity getNearestValidPlayerInRange( World world, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;

        double rangeSq = range * range;
        PlayerEntity closestPlayer = null;
        double closestDistSq = Double.POSITIVE_INFINITY;

        for (PlayerEntity player : world.players()) {
            double distSq = player.distanceToSqr( x, y, z );

            if( isValidTarget( player, requireVulnerable ) &&
                    distSq <= rangeSq && distSq < closestDistSq &&
                    (!checkSight || canEntitySeeBlock( world, pos, player ))) {
                closestPlayer = player;
                closestDistSq = distSq;
            }
        }
        return closestPlayer;
    }

    /** Used for traps/devices that doesn't spawn any monsters. */
    public static PlayerEntity getNearestTrapValidPlayerInRange( World world, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable ) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;

        double rangeSq = range * range;
        PlayerEntity closestPlayer = null;
        double closestDistSq = Double.POSITIVE_INFINITY;

        for (PlayerEntity player : world.players()) {
            double distSq = player.distanceToSqr( x, y, z );

            if( isValidTrapTarget( player, requireVulnerable ) &&
                    distSq <= rangeSq && distSq < closestDistSq &&
                    (!checkSight || canEntitySeeBlock( world, pos, player ))) {
                closestPlayer = player;
                closestDistSq = distSq;
            }
        }
        return closestPlayer;
    }

    public static boolean isValidTrapTarget( Entity entity, boolean requireVulnerable ) {
        final boolean allowInPeaceful = Config.GENERAL.GENERAL.activateTrapsInPeaceful.get();

        return requireVulnerable
                ? ( allowInPeaceful ? ATTACK_ALLOWED_PEACEFUL.test( entity ) : EntityPredicates.ATTACK_ALLOWED.test( entity ))
                : EntityPredicates.NO_SPECTATORS.test( entity );
    }

    public static boolean isValidTarget ( Entity entity, boolean requireVulnerable ) {
        return requireVulnerable ? EntityPredicates.ATTACK_ALLOWED.test( entity ) : EntityPredicates.NO_SPECTATORS.test( entity );
    }


    public static boolean canEntitySeeBlock( World world, BlockPos pos, Entity entity ) {
        BlockRayTraceResult result = world.clip( new RayTraceContext(
                new Vector3d( entity.getX(), entity.getY() + entity.getEyeHeight( ), entity.getZ() ),
                new Vector3d( pos.getX( ) + 0.5, pos.getY( ) + 0.5, pos.getZ( ) + 0.5 ),
                RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null ));


        // No colliding blocks in the path or at the destination, can see
        return result.getType() == RayTraceResult.Type.MISS ||
                        // Hit something, can see if the passed position (or the block above) is the hit
                        (result.getBlockPos( ).equals( pos ) || result.getBlockPos( ).equals( pos.offset( 0, 1, 0 ) ));
    }

    public static boolean isSolidBlock( ISeedReader world, BlockPos pos ) {
        BlockState state = world.getBlockState( pos );

        for ( Direction direction : Direction.values() ) {
            if ( !state.isFaceSturdy( world, pos, direction ))
                return false;
        }
        return true;
    }

    public static void setStackPotionColor(ItemStack potionStack) {
        List<EffectInstance> effects = PotionUtils.getCustomEffects(potionStack);

        if (!effects.isEmpty()) {
            int color = PotionUtils.getColor(effects);

            potionStack.getOrCreateTag().putInt("CustomPotionColor", color);
        }
    }

    // Utility class, instantiating not needed
    private TrapHelper() {}
}
