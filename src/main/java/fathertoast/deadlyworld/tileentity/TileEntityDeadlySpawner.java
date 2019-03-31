package fathertoast.deadlyworld.tileentity;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public
class TileEntityDeadlySpawner extends TileEntity implements ITickable
{
	private static final int EVENT_TIMER_RESET = 1;
	
	// Attribute tags
	private static final String TAG_DYNAMIC_SPAWN_LIST = "DynamicSpawnList";
	
	private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
	private static final String TAG_CHECK_SIGHT      = "CheckSight";
	
	private static final String TAG_DELAY_MIN         = "DelayMin";
	private static final String TAG_DELAY_MAX         = "DelayMax";
	private static final String TAG_DELAY_PROGRESSIVE = "DelayProgressive";
	
	private static final String TAG_SPAWN_COUNT = "SpawnCount";
	private static final String TAG_SPAWN_RANGE = "SpawnRange";
	
	// Logic tags
	private static final String TAG_SPAWN_ENTITY  = "SpawnEntity";
	private static final String TAG_DELAY         = "Delay";
	private static final String TAG_DELAY_BUILDUP = "DelayBuildup";
	
	// Attributes
	private WeightedRandomConfig dynamicSpawnList;
	
	private float   activationRange;
	private boolean checkSight;
	
	private int minSpawnDelay;
	private int maxSpawnDelay;
	private int progressiveSpawnDelay;
	
	private int   spawnCount;
	private float spawnRange;
	
	// Logic
	private Class< ? extends Entity > entityToSpawn = EntityPig.class;
	
	/** Whether or not this spawner is active. Reduces the number of times we need to iterate over the player list. */
	private boolean activated;
	/** Countdown until the next activation check. */
	private int     activationDelay;
	
	/** Countdown until the next spawn attempt. If this is set below 0, the countdown is reset without attempting to spawn. */
	private int    spawnDelay = 10;
	/** The spawn delay previously set; the core of the progressive delay logic. */
	private double spawnDelayBuildup;
	
	// Client logic
	/** Cached instance of the entity to render inside the spawner. */
	private Entity cachedEntity;
	/** The rotation of the mob inside the mob spawner */
	private double mobRotation;
	/** the previous rotation of the mob inside the mob spawner */
	private double prevMobRotation;
	
	public
	void initializeSpawner( EnumSpawnerType spawnerType, Config dimConfig )
	{
		Config.FeatureSpawner spawnerConfig = spawnerType.getFeatureConfig( dimConfig );
		
		// Set attributes from the config
		if( world.rand.nextFloat( ) < spawnerConfig.DYNAMIC_CHANCE ) {
			dynamicSpawnList = spawnerConfig.SPAWN_LIST;
		}
		else {
			dynamicSpawnList = null;
		}
		
		activationRange = spawnerConfig.ACTIVATION_RANGE;
		checkSight = spawnerConfig.CHECK_SIGHT;
		
		minSpawnDelay = spawnerConfig.DELAY_MIN;
		maxSpawnDelay = spawnerConfig.DELAY_MAX;
		progressiveSpawnDelay = spawnerConfig.DELAY_PROGRESSIVE;
		
		spawnCount = spawnerConfig.SPAWN_COUNT;
		spawnRange = spawnerConfig.SPAWN_RANGE;
		
		// Initialize logic
		setEntityToSpawn( spawnerConfig.SPAWN_LIST.nextItem( world.rand ) );
		
	}
	
	private
	void setEntityToSpawn( ResourceLocation registryName )
	{
		entityToSpawn = EntityList.getClass( registryName );
		if( entityToSpawn == null ) {
			DeadlyWorldMod.log( ).warn(
				"Spawner received non-registered entity name '{}'" +
				" - This is probably caused by an error or change in the config for DIM_{} (expect to see pig spawners)",
				registryName, world.provider.getDimension( )
			);
			entityToSpawn = EntityPig.class;
		}
		cachedEntity = null;
	}
	
	private
	EnumSpawnerType getSpawnerType( )
	{
		if( pos != null && world != null ) {
			IBlockState block = world.getBlockState( pos );
			if( block.getBlock( ) == ModObjects.DEADLY_SPAWNER ) {
				return block.getValue( EnumSpawnerType.PROPERTY );
			}
		}
		return EnumSpawnerType.LONE;
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
				double xPos = pos.getX( ) + world.rand.nextFloat( );
				double yPos = pos.getY( ) + world.rand.nextFloat( );
				double zPos = pos.getZ( ) + world.rand.nextFloat( );
				world.spawnParticle( EnumParticleTypes.SMOKE_NORMAL, xPos, yPos, zPos, 0.0, 0.0, 0.0 );
				world.spawnParticle( EnumParticleTypes.FLAME, xPos, yPos, zPos, 0.0, 0.0, 0.0 );
				
				if( spawnDelay > 0 ) {
					spawnDelay--;
				}
				
				prevMobRotation = mobRotation;
				mobRotation = (mobRotation + 1000.0F / (spawnDelay + 200.0F)) % 360.0;
			}
		}
		else {
			// Run server-side logic
			if( activated ) {
				if( spawnDelay < 0 ) {
					resetTimer( false );
				}
				
				if( spawnDelay > 0 ) {
					// Spawner is on cooldown
					spawnDelay--;
				}
				else if( checkSight && !TrapHelper.isValidPlayerInRange( world, pos, activationRange, true, false ) ) {
					// Failed sight check; impose a small delay so we don't spam ray traces
					spawnDelay = 6 + world.rand.nextInt( 10 );
				}
				else {
					// Attempt spawning
					doSpawn( );
				}
			}
			else if( spawnDelayBuildup > minSpawnDelay ) {
				// Decrement the progressive delay buildup
				spawnDelayBuildup -= progressiveSpawnDelay * Config.get( ).GENERAL.PROGRESSIVE_RECOVERY;
			}
		}
	}
	
	
	private
	void doSpawn( )
	{
		final Config             dimConfig          = Config.getOrDefault( world );
		final EnumSpawnerType    spawnerType        = getSpawnerType( );
		final DifficultyInstance difficultyInstance = world.getDifficultyForLocation( pos );
		
		boolean success = false;
		
		for( int i = 0; i < spawnCount; i++ ) {
			double xSpawn = pos.getX( ) + 0.5 + (world.rand.nextDouble( ) - world.rand.nextDouble( )) * spawnRange;
			double ySpawn = pos.getY( ) + world.rand.nextInt( 3 ) - 1;
			double zSpawn = pos.getZ( ) + 0.5 + (world.rand.nextDouble( ) - world.rand.nextDouble( )) * spawnRange;
			
			// Try to create the entity to spawn
			final Entity entity;
			try {
				entity = entityToSpawn.getConstructor( World.class ).newInstance( world );
			}
			catch( Exception ex ) {
				DeadlyWorldMod.log( ).error( "Encountered exception while constructing entity '{}'", entityToSpawn, ex );
				break;
			}
			
			// Do max nearby entities check
			int nearbyEntities = world.getEntitiesWithinAABB(
				entity.getClass( ),
				new AxisAlignedBB(
					pos.getX( ), pos.getY( ), pos.getZ( ),
					pos.getX( ) + 1, pos.getY( ) + 1, pos.getZ( ) + 1
				).grow( spawnRange )
			).size( );
			if( nearbyEntities >= spawnCount * 2 ) {
				break;
			}
			
			// Initialize the entity
			entity.setLocationAndAngles( xSpawn, ySpawn, zSpawn, world.rand.nextFloat( ) * 360.0F, 0.0F );
			if( entity instanceof EntityLiving ) {
				EntityLiving entityLiving = (EntityLiving) entity;
				if( !canSpawnNearLocation( entityLiving, xSpawn, ySpawn, zSpawn ) ) {
					continue;
				}
				entityLiving.onInitialSpawn( difficultyInstance, null );
				spawnerType.initializeEntity( entityLiving, dimConfig, world, pos );
			}
			
			world.spawnEntity( entity );
			world.playEvent( 2004, pos, 0 );
			success = true;
			
			if( entity instanceof EntityLiving ) {
				((EntityLiving) entity).spawnExplosionParticle( );
			}
		}
		
		resetTimer( success );
	}
	
	private
	boolean canSpawnNearLocation( EntityLiving entity, final double x, final double y, final double z )
	{
		return entity.isNotColliding( ) ||
		       trySpawnOffsets( entity, x, y, z, EnumFacing.UP, EnumFacing.DOWN ) ||
		       trySpawnOffsets( entity, x, y, z, EnumFacing.HORIZONTALS ) || trySpawnOffsets( entity, x, y + 1, z, EnumFacing.HORIZONTALS );
	}
	
	private
	boolean trySpawnOffsets( EntityLiving entity, final double x, final double y, final double z, EnumFacing... facings )
	{
		for( EnumFacing facing : facings ) {
			entity.setPosition( x + facing.getFrontOffsetX( ), y + facing.getFrontOffsetY( ), z + facing.getFrontOffsetZ( ) );
			if( entity.isNotColliding( ) ) {
				return true;
			}
		}
		return false;
	}
	
	private
	void resetTimer( boolean incrProgressiveDelay )
	{
		if( !world.isRemote ) {
			if( maxSpawnDelay <= minSpawnDelay ) {
				// Spawn delay is a constant
				spawnDelay = minSpawnDelay;
			}
			else if( progressiveSpawnDelay <= 0 ) {
				// Progressive delay is disabled, use vanilla logic
				spawnDelay = minSpawnDelay + world.rand.nextInt( maxSpawnDelay - minSpawnDelay );
			}
			else {
				// Reset timer based on progressive delay
				if( spawnDelayBuildup < minSpawnDelay ) {
					spawnDelayBuildup = minSpawnDelay;
				}
				
				spawnDelay = (int) spawnDelayBuildup;
				
				if( incrProgressiveDelay ) {
					spawnDelayBuildup = Math.min(
						maxSpawnDelay,
						spawnDelayBuildup + progressiveSpawnDelay * (1.0 + 0.1 * (world.rand.nextDouble( ) - 0.5))
					);
				}
			}
			
			if( dynamicSpawnList != null ) {
				setEntityToSpawn( dynamicSpawnList.nextItem( world.rand ) );
				
				IBlockState block = world.getBlockState( pos );
				world.notifyBlockUpdate( pos, block, block, 4 );
			}
			
			world.addBlockEvent( pos, ModObjects.DEADLY_SPAWNER, EVENT_TIMER_RESET, 0 );
		}
	}
	
	@Override
	public
	NBTTagCompound writeToNBT( NBTTagCompound tag )
	{
		super.writeToNBT( tag );
		
		// Attributes
		tag.setString( TAG_DYNAMIC_SPAWN_LIST, dynamicSpawnList == null ? "" : dynamicSpawnList.toString( ) );
		
		tag.setBoolean( TAG_CHECK_SIGHT, checkSight );
		
		tag.setInteger( TAG_DELAY_MAX, maxSpawnDelay );
		tag.setInteger( TAG_DELAY_PROGRESSIVE, progressiveSpawnDelay );
		
		tag.setInteger( TAG_SPAWN_COUNT, spawnCount );
		tag.setFloat( TAG_SPAWN_RANGE, spawnRange );
		
		// Logic
		tag.setDouble( TAG_DELAY_BUILDUP, spawnDelayBuildup );
		
		return writeNBTSentToClient( tag );
	}
	
	private
	NBTTagCompound writeNBTSentToClient( NBTTagCompound tag )
	{
		// Attributes
		tag.setFloat( TAG_ACTIVATION_RANGE, activationRange );
		
		tag.setInteger( TAG_DELAY_MIN, minSpawnDelay );
		
		// Logic
		tag.setString( TAG_SPAWN_ENTITY, entityToSpawn == EntityPig.class ? "" : EntityList.getKey( entityToSpawn ).toString( ) );
		tag.setInteger( TAG_DELAY, spawnDelay );
		
		return tag;
	}
	
	@Override
	public
	void readFromNBT( NBTTagCompound tag )
	{
		super.readFromNBT( tag );
		
		// Attributes
		if( tag.hasKey( TAG_DYNAMIC_SPAWN_LIST, TrapHelper.NBT_TYPE_STRING ) ) {
			String line = tag.getString( TAG_DYNAMIC_SPAWN_LIST );
			if( line == null || line.isEmpty( ) ) {
				dynamicSpawnList = null;
			}
			else {
				dynamicSpawnList = new WeightedRandomConfig( line );
			}
		}
		
		if( tag.hasKey( TAG_ACTIVATION_RANGE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			activationRange = tag.getFloat( TAG_ACTIVATION_RANGE );
		}
		if( tag.hasKey( TAG_CHECK_SIGHT, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			checkSight = tag.getBoolean( TAG_CHECK_SIGHT );
		}
		
		if( tag.hasKey( TAG_DELAY_MIN, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			minSpawnDelay = tag.getInteger( TAG_DELAY_MIN );
		}
		if( tag.hasKey( TAG_DELAY_MAX, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			maxSpawnDelay = tag.getInteger( TAG_DELAY_MAX );
		}
		if( tag.hasKey( TAG_DELAY_PROGRESSIVE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			progressiveSpawnDelay = tag.getInteger( TAG_DELAY_PROGRESSIVE );
		}
		
		if( tag.hasKey( TAG_SPAWN_COUNT, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			spawnCount = tag.getInteger( TAG_SPAWN_COUNT );
		}
		if( tag.hasKey( TAG_SPAWN_RANGE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			spawnRange = tag.getFloat( TAG_SPAWN_RANGE );
		}
		
		// Logic
		if( tag.hasKey( TAG_SPAWN_ENTITY, TrapHelper.NBT_TYPE_STRING ) ) {
			String line = tag.getString( TAG_SPAWN_ENTITY );
			if( line == null || line.isEmpty( ) ) {
				entityToSpawn = EntityPig.class;
				cachedEntity = null;
			}
			else {
				setEntityToSpawn( new ResourceLocation( line ) );
			}
		}
		if( tag.hasKey( TAG_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			spawnDelay = tag.getInteger( TAG_DELAY );
		}
		if( tag.hasKey( TAG_DELAY_BUILDUP, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
			spawnDelayBuildup = tag.getDouble( TAG_DELAY_BUILDUP );
		}
	}
	
	@Override
	public
	SPacketUpdateTileEntity getUpdatePacket( )
	{
		return new SPacketUpdateTileEntity( pos, 0, getUpdateTag( ) );
	}
	
	@Override
	public
	NBTTagCompound getUpdateTag( )
	{
		NBTTagCompound tag = super.writeToNBT( new NBTTagCompound( ) );
		return writeNBTSentToClient( tag );
	}
	
	@Override
	public
	void onDataPacket( NetworkManager net, SPacketUpdateTileEntity pkt )
	{
		if( world.isRemote ) {
			handleUpdateTag( pkt.getNbtCompound( ) );
		}
	}
	
	@Override
	public
	boolean receiveClientEvent( int id, int type )
	{
		if( world.isRemote ) {
			DeadlyWorldMod.log( ).warn( "Getting client event '{}:{}'", id, type );
			if( id == EVENT_TIMER_RESET ) {
				spawnDelay = minSpawnDelay;
				return true;
			}
		}
		return super.receiveClientEvent( id, type );
	}
	
	@Override
	public
	boolean onlyOpsCanSetNbt( ) { return true; }
	
	@SideOnly( Side.CLIENT )
	public
	Entity getRenderEntity( )
	{
		if( cachedEntity == null && world != null ) {
			try {
				cachedEntity = entityToSpawn.getConstructor( World.class ).newInstance( world );
			}
			catch( Exception ex ) {
				DeadlyWorldMod.log( ).error( "Encountered exception while constructing entity for render '{}'", entityToSpawn, ex );
				cachedEntity = new EntityPig( world );
			}
			
			if( cachedEntity instanceof EntityLiving ) {
				((EntityLiving) cachedEntity).onInitialSpawn( world.getDifficultyForLocation( pos ), null );
			}
		}
		return cachedEntity;
	}
	
	@SideOnly( Side.CLIENT )
	public
	float getRenderEntityRotation( float partialTicks ) { return (float) (prevMobRotation + (mobRotation - prevMobRotation) * partialTicks); }
}
