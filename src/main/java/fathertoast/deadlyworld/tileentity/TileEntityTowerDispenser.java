package fathertoast.deadlyworld.tileentity;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public
class TileEntityTowerDispenser extends TileEntity implements ITickable
{
	// Attribute tags
	private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
	private static final String TAG_CHECK_SIGHT      = "CheckSight";
	
	private static final String TAG_DAMAGE = "Damage";
	
	private static final String TAG_DELAY_MIN = "DelayMin";
	private static final String TAG_DELAY_MAX = "DelayMax";
	
	private static final String TAG_TYPE_DATA = "TypeData";
	
	// Logic tags
	private static final String TAG_DELAY = "Delay";
	
	// Attributes
	private float          activationRange;
	private boolean        checkSight;
	private NBTTagCompound typeData;
	
	private float attackDamage;
	
	private int minAttackDelay;
	private int maxAttackDelay;
	
	// Logic
	/** Whether or not this tower trap is active. Reduces the number of times we need to iterate over the player list. */
	private boolean activated;
	/** Countdown until the next activation check. */
	private int     activationDelay;
	
	/** Countdown until the next attack attempt. If this is set below 0, the countdown is reset without attempting to attack. */
	private int attackDelay = 10;
	
	public
	NBTTagCompound getOrCreateTypeData( )
	{
		if( typeData == null ) {
			typeData = new NBTTagCompound( );
		}
		return typeData;
	}
	
	public
	void initializeTowerTrap( EnumTowerType towerType, Config dimConfig, Random random )
	{
		Config.FeatureTower towerConfig = towerType.getFeatureConfig( dimConfig );
		
		// Set attributes from the config
		activationRange = towerConfig.ACTIVATION_RANGE;
		checkSight = towerConfig.CHECK_SIGHT;
		
		attackDamage = towerConfig.ATTACK_DAMAGE;
		
		minAttackDelay = towerConfig.DELAY_MIN;
		maxAttackDelay = towerConfig.DELAY_MAX;
	}
	
	private
	EnumTowerType getTowerType( )
	{
		if( pos != null && world != null ) {
			IBlockState block = world.getBlockState( pos );
			if( block.getBlock( ) == ModObjects.TOWER_DISPENSER ) {
				return block.getValue( EnumTowerType.PROPERTY );
			}
		}
		return EnumTowerType.DEFAULT;
	}
	
	@Override
	public
	void update( )
	{
		// Update activation status
		if( activationDelay > 0 ) {
			activationDelay--;
		}
		else {
			activationDelay = 4;
			activated = TrapHelper.isValidPlayerInRange( world, pos, activationRange, false, false );
		}
		
		if( world.isRemote ) {
			// Run client-side effects
			if( activated ) {
				double yPos = pos.getY( ) + world.rand.nextFloat( );
				double xPos, zPos;
				
				float faceOffset = 1.0625F;
				if( world.rand.nextBoolean( ) ) {
					faceOffset = 1 - faceOffset;
				}
				if( world.rand.nextBoolean( ) ) {
					xPos = pos.getX( ) + faceOffset;
					zPos = pos.getZ( ) + world.rand.nextFloat( );
				}
				else {
					xPos = pos.getX( ) + world.rand.nextFloat( );
					zPos = pos.getZ( ) + faceOffset;
				}
				
				world.spawnParticle( EnumParticleTypes.SMOKE_NORMAL, xPos, yPos, zPos, 0.0, 0.0, 0.0 );
			}
		}
		else {
			// Run server-side logic
			if( activated ) {
				if( attackDelay < 0 ) {
					resetTimer( );
				}
				
				if( attackDelay > 0 ) {
					// Tower is on cooldown
					attackDelay--;
				}
				else {
					// Attempt to attack the nearest player
					EntityPlayer target = TrapHelper.getNearestValidPlayerInRange( world, pos, activationRange, checkSight, false );
					if( target == null ) {
						// Failed sight check; impose a small delay so we don't spam ray traces
						attackDelay = 6 + world.rand.nextInt( 8 );
					}
					else {
						attack( target );
					}
				}
			}
		}
	}
	
	
	private
	void attack( EntityPlayer target )
	{
		resetTimer( );
		
		final Config        dimConfig = Config.getOrDefault( world );
		final EnumTowerType towerType = getTowerType( );
		
		Vec3d centerPos = new Vec3d( pos ).addVector( 0.5, 0.5, 0.5 );
		Vec3d targetPos = new Vec3d( target.posX, target.getEntityBoundingBox( ).minY + target.height / 3.0F, target.posZ );
		Vec3d vecToTarget = targetPos.subtract( centerPos );
		
		if( Math.abs( vecToTarget.x ) < 0.5 && Math.abs( vecToTarget.z ) < 0.5 ) {
			// Target is directly above or below the tower, can't hit it
			return;
		}
		double distanceH = Math.sqrt( vecToTarget.x * vecToTarget.x + vecToTarget.z * vecToTarget.z );
		
		// Determine the offset to spawn the arrow at so it doesn't clip the dispenser block
		Vec3d offset;
		if( Math.abs( vecToTarget.x ) < Math.abs( vecToTarget.z ) ) {
			offset = new Vec3d(
				vecToTarget.x / distanceH,
				0.0,
				vecToTarget.z < 0.0 ? -1.0 : 1.0
			);
		}
		else if( Math.abs( vecToTarget.x ) > Math.abs( vecToTarget.z ) ) {
			offset = new Vec3d(
				vecToTarget.x < 0.0 ? -1.0 : 1.0,
				0.0,
				vecToTarget.z / distanceH
			);
		}
		else {
			offset = new Vec3d(
				vecToTarget.x < 0.0 ? -1.0 : 1.0,
				0.0,
				vecToTarget.z < 0.0 ? -1.0 : 1.0
			);
		}
		
		// Allow the tower type to actually execute the attack
		towerType.triggerAttack( dimConfig, this, target, centerPos, offset, vecToTarget, distanceH );
		
		world.playSound( null, centerPos.x, centerPos.y, centerPos.z, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS,
		                 1.0F, 1.0F / (world.rand.nextFloat( ) * 0.4F + 0.8F) );
	}
	
	public void
	shootArrow( Vec3d center, Vec3d offset, Vec3d vecToTarget, double distanceH, float velocity, float variance, EntityArrow arrow )
	{
		final double spawnOffset = 0.6;
		
		arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
		arrow.setDamage( attackDamage / velocity );
		arrow.setLocationAndAngles( center.x + offset.x * spawnOffset, center.y, center.z + offset.z * spawnOffset, 0.0F, 0.0F );
		arrow.shoot( vecToTarget.x, vecToTarget.y + distanceH * 0.2F, vecToTarget.z, velocity, variance );
		
		world.spawnEntity( arrow );
	}
	
	private
	void resetTimer( )
	{
		if( !world.isRemote ) {
			if( maxAttackDelay <= minAttackDelay ) {
				// Spawn delay is a constant
				attackDelay = minAttackDelay;
			}
			else {
				attackDelay = minAttackDelay + world.rand.nextInt( maxAttackDelay - minAttackDelay );
			}
		}
	}
	
	@Override
	public
	NBTTagCompound writeToNBT( NBTTagCompound tag )
	{
		super.writeToNBT( tag );
		
		// Attributes
		tag.setFloat( TAG_ACTIVATION_RANGE, activationRange );
		tag.setBoolean( TAG_CHECK_SIGHT, checkSight );
		
		tag.setFloat( TAG_DAMAGE, attackDamage );
		
		tag.setInteger( TAG_DELAY_MIN, minAttackDelay );
		tag.setInteger( TAG_DELAY_MAX, maxAttackDelay );
		
		if( typeData != null ) {
			tag.setTag( TAG_TYPE_DATA, typeData );
		}
		
		// Logic
		tag.setInteger( TAG_DELAY, attackDelay );
		
		return tag;
	}
	
	@Override
	public
	void readFromNBT( NBTTagCompound tag )
	{
		super.readFromNBT( tag );
		
		// Attributes
		if( tag.hasKey( TAG_ACTIVATION_RANGE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			activationRange = tag.getFloat( TAG_ACTIVATION_RANGE );
		}
		if( tag.hasKey( TAG_CHECK_SIGHT, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			checkSight = tag.getBoolean( TAG_CHECK_SIGHT );
		}
		
		if( tag.hasKey( TAG_DAMAGE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			attackDamage = tag.getFloat( TAG_DAMAGE );
		}
		
		if( tag.hasKey( TAG_DELAY_MIN, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			minAttackDelay = tag.getInteger( TAG_DELAY_MIN );
		}
		if( tag.hasKey( TAG_DELAY_MAX, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			maxAttackDelay = tag.getInteger( TAG_DELAY_MAX );
		}
		
		if( tag.hasKey( TAG_TYPE_DATA, tag.getId( ) ) ) {
			typeData = tag.getCompoundTag( TAG_TYPE_DATA );
		}
		else {
			typeData = null;
		}
		
		// Logic
		if( tag.hasKey( TAG_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			attackDelay = tag.getInteger( TAG_DELAY );
		}
	}
	
	@Override
	public
	boolean onlyOpsCanSetNbt( ) { return true; }
}
