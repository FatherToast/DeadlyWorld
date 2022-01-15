package fathertoast.deadlyworld.item;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.loot.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public
enum EnumDeadlyEventType
{
	NONE( "none" ) {
		@Override
		void trigger( Config dimConfig, World world, Vec3d pos, EntityPlayer player ) { }
	},
	
	SILVERFISH( "spawn_silverfish" ) {
		@Override
		void trigger( Config dimConfig, World world, Vec3d pos, EntityPlayer player )
		{
			// Spawn silverfish
			for( int i = 0; i < dimConfig.CHEST_INFESTED.SILVERFISH_COUNT; i++ ) {
				float angle = 2.0F * (float) Math.PI * world.rand.nextFloat( );
				
				EntitySilverfish silverfish = new EntitySilverfish( world );
				silverfish.setLocationAndAngles( pos.x, pos.y + 0.1D, pos.z, (float) Math.toDegrees( angle ), 0.0F );
				
				float speed = dimConfig.CHEST_INFESTED.LAUNCH_SPEED * world.rand.nextFloat( ) + 0.02F;
				
				silverfish.setAttackTarget( player );
				silverfish.onInitialSpawn( world.getDifficultyForLocation( new BlockPos( pos ) ), null );
				
				silverfish.motionX = MathHelper.sin( angle ) * speed;
				silverfish.motionY = 0.2F + 0.2F * world.rand.nextFloat( );
				silverfish.motionZ = MathHelper.cos( angle ) * speed;
				
				world.spawnEntity( silverfish );
			}
			world.playSound( null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_SILVERFISH_HURT, SoundCategory.BLOCKS, 1.0F, 1.0F );
		}
	},
	
	TNT( "tnt" ) {
		@Override
		void trigger( Config dimConfig, World world, Vec3d pos, EntityPlayer player )
		{
			int fuseRange = dimConfig.CHEST_SURPRISE.TNT_FUSE_TIME_MAX - dimConfig.CHEST_SURPRISE.TNT_FUSE_TIME_MIN;
			if( fuseRange <= 0 ) {
				fuseRange = 1;
			}
			
			// Spawn the primed tnt blocks
			for( int i = 0; i < dimConfig.CHEST_SURPRISE.TNT_COUNT; i++ ) {
				EntityTNTPrimed tntPrimed = new EntityTNTPrimed( world, pos.x, pos.y, pos.z, null );
				
				float speed = dimConfig.CHEST_SURPRISE.TNT_LAUNCH_SPEED * world.rand.nextFloat( ) + 0.02F;
				tntPrimed.setFuse( dimConfig.CHEST_SURPRISE.TNT_FUSE_TIME_MIN + world.rand.nextInt( fuseRange ) );
				
				tntPrimed.motionX *= speed;
				tntPrimed.motionY += 0.1F + 0.1F * world.rand.nextFloat( );
				tntPrimed.motionZ *= speed;
				
				world.spawnEntity( tntPrimed );
			}
			world.playSound( null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F );
		}
	},
	
	LAVA( "lava" ) {
		@Override
		void trigger( Config dimConfig, World world, Vec3d pos, EntityPlayer player )
		{
			// Decide on position for lava
			BlockPos lavaPos        = new BlockPos( pos );
			Block    blockAtTrigger = world.getBlockState( lavaPos ).getBlock( );
			if( blockAtTrigger instanceof BlockChest ) {
				lavaPos = lavaPos.add( 0, 1, 0 );
			}
			
			// Try to place the lava
			if( world.getBlockState( lavaPos ).isFullCube( ) ) {
				// Failure, just play a sound
				world.playSound( null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F );
			}
			else {
				// Place flowing lava block
				world.setBlockState( lavaPos, Blocks.FLOWING_LAVA.getDefaultState( ), UPDATE_FLAGS );
				world.playSound( null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F );
			}
		}
	},
	
	POISON_GAS( "poison_gas" ) {
		@Override
		void trigger( Config dimConfig, World world, Vec3d pos, EntityPlayer player )
		{
			final float minRadius = 0.5F;
			EnumPotionCloudType cloudType = dimConfig.CHEST_SURPRISE.GAS_POTION_TYPE_LIST.nextItem( world.rand );
			
			EntityAreaEffectCloud cloud = new EntityAreaEffectCloud( world, pos.x, pos.y, pos.z );
			cloudType.addEffects( dimConfig, cloud );
			cloud.setOwner( null );
			
			cloud.setWaitTime( dimConfig.CHEST_SURPRISE.GAS_DURATION_DELAY );
			cloud.setDuration( dimConfig.CHEST_SURPRISE.GAS_DURATION );
			//cloud.setDurationOnUse( 0 ); Must be done through NBT
			
			cloud.setRadius( minRadius );
			cloud.setRadiusPerTick( (dimConfig.CHEST_SURPRISE.GAS_MAX_RADIUS - minRadius) / (float) dimConfig.CHEST_SURPRISE.GAS_DURATION );
			cloud.setRadiusOnUse( 0.0F );
			
			world.spawnEntity( cloud );
			world.playSound( null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F );
		}
	},
	
	SPAWN_MIMIC( "spawn_mimic" ) {
		@Override
		void trigger( Config dimConfig, World world, Vec3d pos, EntityPlayer player )
		{
			// Get the chest
			BlockPos chestPos   = new BlockPos( pos );
			Block    chestBlock = world.getBlockState( chestPos ).getBlock( );
			if( chestBlock instanceof BlockChest ) {
				// Delete the chest triggering this event
				world.setBlockToAir( chestPos );
			}
			else {
				// No chest found, create a new default chest
				chestBlock = Blocks.CHEST;
				
				// If triggered by a chest being broken, this event happens after the block is removed,
				// but before the chest item is spawned, so we will be creating an extra vanilla chest.
			}
			
			// Create the chest item to equip
			ItemStack chestItem = new ItemStack( chestBlock );
			NBTTagCompound tileEntityTag = chestItem.getOrCreateSubCompound( "BlockEntityTag" ); // Tag applied when placed
			tileEntityTag.setString( "LootTable", EnumChestType.MIMIC.LOOT_TABLE_CHEST.toString( ) ); // Add loot table
			tileEntityTag.setLong( "LootTableSeed", world.rand.nextLong( ) ); // Prevent stacking
			
			// Add text to the chest item tooltip to distinguish it from regular (empty) chests
			String space = TextFormatting.YELLOW.toString( ) + " " + TextFormatting.OBFUSCATED.toString( );
			String line = "Not" + space + "A" + space + "Mimic";
			if( world.rand.nextInt(3) == 0 ) {
				line = "Maybe" + space + line;
			}
			if( world.rand.nextInt(6) == 0 ) {
				line = "Really" + space + line;
			}
			if( world.rand.nextInt( 9 ) == 0 ) {
				line = "Definitely" + space + line;
			}
			NBTTagList tooltip = new NBTTagList( );
			tooltip.appendTag( new NBTTagString( TextFormatting.YELLOW.toString( ) + TextFormatting.OBFUSCATED.toString( ) + line ) );
			chestItem.getOrCreateSubCompound( "display" ).setTag( "Lore", tooltip );
			
			// Pick an entity to spawn
			ResourceLocation          registryName  = dimConfig.CHEST_MIMIC.SPAWN_LIST.nextItem( world.rand );
			Class< ? extends Entity > entityToSpawn = EntityList.getClass( registryName );
			if( entityToSpawn == null ) {
				DeadlyWorldMod.log( ).warn(
					"Spawn mimic event received non-registered entity name '{}'" +
					" - This is probably caused by an error or change in the config for DIM_{} (defaulting to zombie)",
					registryName, world.provider.getDimension( )
				);
				entityToSpawn = EntityZombie.class;
			}
			
			// Try to create the entity to spawn
			Entity entity;
			try {
				entity = entityToSpawn.getConstructor( World.class ).newInstance( world );
			}
			catch( Exception ex ) {
				DeadlyWorldMod.log( ).error( "Encountered exception while constructing entity '{}'", entityToSpawn, ex );
				entity = new EntityZombie( world );
			}
			
			// Initialize the entity
			entity.setPositionAndRotation( pos.x, pos.y, pos.z, world.rand.nextFloat( ) * 2.0F * (float) Math.PI, 0.0F );
			entity.motionY = 0.3F;
			if( entity instanceof EntityLiving ) {
				EntityLiving livingEntity = (EntityLiving) entity;
				
				// Convert to baby, if possible
				if( world.rand.nextFloat( ) < dimConfig.CHEST_MIMIC.BABY_CHANCE ) {
					if( entity instanceof EntityZombie ) {
						((EntityZombie) entity).setChild( true );
					}
					else if( entity instanceof EntityAgeable ) {
						((EntityAgeable) entity).setGrowingAge( Integer.MIN_VALUE );
					}
				}
				
				// Equip the entity disable despawn
				livingEntity.onInitialSpawn( world.getDifficultyForLocation( chestPos ), null );
				livingEntity.setItemStackToSlot( EntityEquipmentSlot.HEAD, chestItem );
				livingEntity.setDropChance( EntityEquipmentSlot.HEAD, 2.0F );
				livingEntity.enablePersistence( );
				
				// Apply attribute modifiers
				IAttributeInstance attrib;
				if( dimConfig.CHEST_MIMIC.MULTIPLIER_DAMAGE != 1.0F ) {
					try {
						attrib = livingEntity.getEntityAttribute( SharedMonsterAttributes.ATTACK_DAMAGE );
						attrib.setBaseValue( attrib.getBaseValue( ) * dimConfig.CHEST_MIMIC.MULTIPLIER_DAMAGE );
					}
					catch( Exception ex ) {
						// This is fine, entity just doesn't have the attribute
					}
				}
				if( dimConfig.CHEST_MIMIC.MULTIPLIER_HEALTH != 1.0F ) {
					try {
						attrib = livingEntity.getEntityAttribute( SharedMonsterAttributes.MAX_HEALTH );
						attrib.setBaseValue( attrib.getBaseValue( ) * dimConfig.CHEST_MIMIC.MULTIPLIER_HEALTH );
					}
					catch( Exception ex ) {
						// This is fine, entity just doesn't have the attribute
					}
				}
				if( dimConfig.CHEST_MIMIC.MULTIPLIER_SPEED != 1.0F ) {
					try {
						attrib = livingEntity.getEntityAttribute( SharedMonsterAttributes.MOVEMENT_SPEED );
						attrib.setBaseValue( attrib.getBaseValue( ) * dimConfig.CHEST_MIMIC.MULTIPLIER_SPEED );
					}
					catch( Exception ex ) {
						// This is fine, entity just doesn't have the attribute
					}
				}
				livingEntity.setHealth( livingEntity.getMaxHealth( ) );
				
				// Target player that opened the chest
				livingEntity.setAttackTarget( player );
			}
			
			// Spawn the entity and play alert sound
			world.spawnEntity( entity );
			world.playSound( null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_SHULKER_SHOOT, SoundCategory.BLOCKS, 1.0F, 1.0F );
		}
	};
	
	//private static final double HEIGHT_CHEST_TOP = 0.875;
	//private static final double HEIGHT_CHEST_OPENING = 0.625;
	
	/** For events, we want the change to happen immediately and interact with the environment. */
	public static final int UPDATE_FLAGS = FeatureGenerator.FLAG_BLOCK_UPDATE | FeatureGenerator.FLAG_NOTIFY_CLIENTS | FeatureGenerator.FLAG_PRIORITY_RERENDER;
	
	public static final String PATH = "events/";
	
	public final String NAME;
	public final String DISPLAY_NAME;
	
	public final ResourceLocation LOOT_TABLE;
	
	EnumDeadlyEventType( String name ) { this( name, name.replace( "_", " " ) ); }
	
	EnumDeadlyEventType( String name, String prettyName )
	{
		NAME = name;
		DISPLAY_NAME = prettyName;
		
		LOOT_TABLE = LootTableList.register( new ResourceLocation( DeadlyWorldMod.MOD_ID, PATH + name ) );
	}
	
	abstract
	void trigger( Config dimConfig, World world, Vec3d pos, EntityPlayer player );
	
	public
	void buildLootTable( LootTableBuilder loot ) {
		loot.addPool(
			new LootPoolBuilder( "Event: " + DISPLAY_NAME )
				.addEntry( new LootEntryItemBuilder( "events." + NAME, ModObjects.EVENT_ITEM.createEventItem( this ) ).toLootEntry( ) )
				.toLootPool( )
		);
	}
}
