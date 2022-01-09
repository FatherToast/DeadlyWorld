package fathertoast.deadlyworld.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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

    public static boolean isValidTarget( Entity entity, boolean requireVulnerable ) {
        return requireVulnerable ? EntityPredicates.ATTACK_ALLOWED.test( entity ) : EntityPredicates.NO_SPECTATORS.test( entity );
    }

    public static boolean canEntitySeeBlock( World world, BlockPos pos, Entity entity ) {
        BlockRayTraceResult result = world.clip(new RayTraceContext(
                new Vector3d( entity.getX(), entity.getY() + entity.getEyeHeight( ), entity.getZ() ),
                new Vector3d( pos.getX( ) + 0.5, pos.getY( ) + 0.5, pos.getZ( ) + 0.5 ),
                RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));


        // No colliding blocks in the path or at the destination, can see
        return result.getType() == RayTraceResult.Type.MISS ||
                        // Hit something, can see if the passed position (or the block above) is the hit
                        (result.getBlockPos( ).equals( pos ) || result.getBlockPos( ).equals( pos.offset( 0, 1, 0 ) ));
    }


    // Utility class, instantiating not needed
    private TrapHelper() {}
}
