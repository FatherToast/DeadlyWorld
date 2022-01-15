package fathertoast.deadlyworld.tileentity;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

import java.util.Random;

public
class TileEntityFloorTrap extends TileEntity implements ITickable
{
	private static final int    TO_TRIGGER_DELAY     = 10;
	// Attribute tags
	private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
	private static final String TAG_CHECK_SIGHT      = "CheckSight";
	private static final String TAG_TYPE_DATA        = "TypeData";
	
	// Logic tags
	private static final String TAG_DELAY = "Delay";
	
	// Attributes
	private float          activationRange;
	private boolean        checkSight;
	private NBTTagCompound typeData;
	
	// Logic
	/** Count until the trap triggers after being tripped. -1 if the trap has not been tripped. */
	private int triggerDelay = -1;
	
	public
	void resetTrap( ) { triggerDelay = -1; }
	
	public
	void tripTrap( ) { triggerDelay = 0; }
	
	public
	void tripTrapRandom( ) { triggerDelay = world.rand.nextInt( TO_TRIGGER_DELAY ); }
	
	public
	void disableTrap( int duration ) { triggerDelay = -1 - duration; }
	
	public
	void disableTrap( ) { triggerDelay = TO_TRIGGER_DELAY; }
	
	public
	NBTTagCompound getOrCreateTypeData( )
	{
		if( typeData == null ) {
			typeData = new NBTTagCompound( );
		}
		return typeData;
	}
	
	public
	void initializeFloorTrap( EnumFloorTrapType trapType, Config dimConfig, Random random )
	{
		Config.FeatureFloorTrap trapConfig = trapType.getFeatureConfig( dimConfig );
		
		// Set attributes from the config
		activationRange = trapConfig.ACTIVATION_RANGE;
		checkSight = trapConfig.CHECK_SIGHT;
	}
	
	private
	EnumFloorTrapType getTrapType( )
	{
		if( pos != null && world != null ) {
			IBlockState block = world.getBlockState( pos );
			if( block.getBlock( ) == ModObjects.FLOOR_TRAP ) {
				return block.getValue( EnumFloorTrapType.PROPERTY );
			}
		}
		return EnumFloorTrapType.TNT;
	}
	
	public
	EntityPlayer getTarget( ) {
		return TrapHelper.getNearestValidPlayerInRange( world, pos.up( ), activationRange, checkSight, true );
	}
	
	@Override
	public
	void update( )
	{
		if( !world.isRemote ) {
			// Run server-side logic
			if( triggerDelay == -1 ) {
				// Check if trap should be tripped
				if( TrapHelper.isValidPlayerInRange( world, pos.up( ), activationRange, checkSight, true ) ) {
					tripTrap( );
					world.playSound( null, pos.getX( ) + 0.5, pos.getY( ) + 1, pos.getZ( ) + 0.5,
					                 SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F );
				}
			}
			else {
				// Trap has been tripped or reset with a delay
				triggerDelay++;
				
				// Trigger trap
				if( triggerDelay == TO_TRIGGER_DELAY ) {
					triggerTrap( );
				}
			}
		}
	}
	
	private
	void triggerTrap( )
	{
		final Config            dimConfig = Config.getOrDefault( world );
		final EnumFloorTrapType trapType  = getTrapType( );
		
		trapType.triggerTrap( dimConfig, this );
	}
	
	@Override
	public
	NBTTagCompound writeToNBT( NBTTagCompound tag )
	{
		super.writeToNBT( tag );
		
		// Attributes
		tag.setFloat( TAG_ACTIVATION_RANGE, activationRange );
		tag.setBoolean( TAG_CHECK_SIGHT, checkSight );
		if( typeData != null ) {
			tag.setTag( TAG_TYPE_DATA, typeData );
		}
		
		// Logic
		tag.setInteger( TAG_DELAY, triggerDelay );
		
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
		if( tag.hasKey( TAG_TYPE_DATA, tag.getId( ) ) ) {
			typeData = tag.getCompoundTag( TAG_TYPE_DATA );
		}
		else {
			typeData = null;
		}
		
		// Logic
		if( tag.hasKey( TAG_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			triggerDelay = tag.getInteger( TAG_DELAY );
		}
	}
	
	@Override
	public
	boolean onlyOpsCanSetNbt( ) { return true; }
}
