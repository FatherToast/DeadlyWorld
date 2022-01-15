package fathertoast.deadlyworld.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionUtils;
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
	
	private static final String TAG_POTION_COLOR = "CustomPotionColor";
	
	/** Checks if there is any player in range matching the specifications. */
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
	EntityPlayer getNearestValidPlayerInRange( World world, BlockPos pos, double range, boolean checkSight, boolean requireVulnerable )
	{
		double x = pos.getX( ) + 0.5;
		double y = pos.getY( );
		double z = pos.getZ( ) + 0.5;
		
		double       rangeSq       = range * range;
		EntityPlayer closestPlayer = null;
		double       closestDistSq = Double.POSITIVE_INFINITY;
		for( int i = 0; i < world.playerEntities.size( ); i++ ) {
			EntityPlayer player = world.playerEntities.get( i );
			double       distSq = player.getDistanceSq( x, y, z );
			if( isValidTarget( player, requireVulnerable ) &&
			    distSq <= rangeSq && distSq < closestDistSq &&
			    (!checkSight || canEntitySeeBlock( world, pos, player ))
			) {
				closestPlayer = player;
				closestDistSq = distSq;
			}
		}
		return closestPlayer;
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
	
	public static
	boolean isAnySideOpen( World world, BlockPos position )
	{
		return world.isAirBlock( position.add( -1, 0, 0 ) ) ||
		       world.isAirBlock( position.add( 1, 0, 0 ) ) ||
		       world.isAirBlock( position.add( 0, 0, -1 ) ) ||
		       world.isAirBlock( position.add( 0, 0, 1 ) );
	}
	
	public static
	void setPotionColorFromEffects( ItemStack potionStack )
	{
		NBTTagCompound tag = potionStack.getTagCompound( );
		if( tag == null ) {
			tag = new NBTTagCompound( );
			potionStack.setTagCompound( tag );
		}
		tag.setInteger( TAG_POTION_COLOR, PotionUtils.getPotionColorFromEffectList( PotionUtils.getEffectsFromStack( potionStack ) ) );
	}
	
	private
	TrapHelper( ) { }
}
