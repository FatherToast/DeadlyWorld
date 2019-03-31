package fathertoast.deadlyworld.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@SuppressWarnings( { "unused", "WeakerAccess" } )
public
class TrapHelper
{
	public static final int NBT_TYPE_PRIMITIVE = 99;
	public static final int NBT_TYPE_STRING    = new NBTTagString( ).getId( );
	
	public static
	boolean isValidPlayerInRange( World world, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable )
	{
		double x = pos.getX( ) + 0.5;
		double y = pos.getY( );
		double z = pos.getZ( ) + 0.5;
		
		double rangeSq = range * range;
		for( int i = 0; i < world.playerEntities.size( ); i++ ) {
			EntityPlayer player = world.playerEntities.get( i );
			if( isValidTarget( player, requireVulnerable ) &&
			    player.getDistanceSq( x, y, z ) <= rangeSq &&
			    (!checkSight || canEntitySeeBlock( world, pos, player ))
			) {
				return true;
			}
		}
		return false;
	}
	
	public static
	boolean isValidTarget( Entity entity, boolean requireVulnerable )
	{
		return requireVulnerable ? EntitySelectors.CAN_AI_TARGET.apply( entity ) : EntitySelectors.NOT_SPECTATING.apply( entity );
	}
	
	public static
	boolean canEntitySeeBlock( World world, BlockPos pos, Entity entity )
	{
		RayTraceResult result = world.rayTraceBlocks(
			new Vec3d( entity.posX, entity.posY + entity.getEyeHeight( ), entity.posZ ),
			new Vec3d( pos.getX( ) + 0.5, pos.getY( ) + 0.5, pos.getZ( ) + 0.5 ),
			false, true, false
		);
		return
			// No collidable blocks in the path or at the destination, can see
			result == null ||
			// Hit something, can see if the passed position (or the block above) is the hit
			result.getBlockPos( ) != null && (result.getBlockPos( ).equals( pos ) || result.getBlockPos( ).equals( pos.add( 0, 1, 0 ) ));
	}
	
	private
	TrapHelper( ) { }
}
