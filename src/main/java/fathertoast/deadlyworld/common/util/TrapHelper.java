package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.config.util.WeightedPotionList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class TrapHelper {

    public static final int NBT_TYPE_PRIMITIVE = Constants.NBT.TAG_ANY_NUMERIC;
    public static final int NBT_TYPE_STRING = Constants.NBT.TAG_STRING;


    public static boolean isValidPlayerInRange(World world, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable) {
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

    public static boolean isValidTarget( Entity entity, boolean requireVulnerable ) {
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

    @SuppressWarnings("ConstantConditions")
    public static ItemStack getPotionFromTrapData( CompoundNBT trapData, WeightedPotionList potionList, Random random ) {
        final String TAG_POTION_TYPE = "PotionType";
        EffectInstance effectInstance;

        if ( trapData.contains(TAG_POTION_TYPE, Constants.NBT.TAG_COMPOUND )) {
            CompoundNBT potionData = trapData.getCompound( TAG_POTION_TYPE );

            if ( potionData.contains( "Effect", NBT_TYPE_STRING )
                    && potionData.contains( "Duration", NBT_TYPE_PRIMITIVE )
                    && potionData.contains( "Amplifier", NBT_TYPE_PRIMITIVE )) {

                Effect effect = Effects.HARM;
                int duration;
                int amplifier;

                ResourceLocation effectId = ResourceLocation.tryParse(potionData.getString("Effect"));

                if (effectId != null) {
                    if (ForgeRegistries.POTIONS.containsKey(effectId)) {
                        effect = ForgeRegistries.POTIONS.getValue(effectId);
                    }
                }
                duration = potionData.getInt("Duration");
                amplifier = potionData.getInt("Amplifier");

                effectInstance = new EffectInstance(effect, duration, amplifier);
            }
            else {
                effectInstance = new EffectInstance(Effects.HARM, 1, 0);
            }
        }
        else {
            effectInstance = potionList.next( random );
        }
        return PotionUtils.setCustomEffects( new ItemStack( Items.SPLASH_POTION ), Collections.singletonList( effectInstance ));
    }

    // Utility class, instantiating not needed
    private TrapHelper() {}
}
