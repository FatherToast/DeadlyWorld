package fathertoast.deadlyworld.block.state;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.loot.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public
enum EnumFloorTrapType implements IExclusiveMetaProvider
{
	TNT( "tnt" ) {
		@Override
		public
		Config.FeatureFloorTrap getFeatureConfig( Config dimConfig ) { return dimConfig.FLOOR_TRAP_TNT; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Redstone", Items.REDSTONE, 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Iron Ingot", Items.IRON_INGOT );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.BLAST_PROTECTION, Enchantments.POWER, Enchantments.SHARPNESS
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Explosives
			loot.addClusterDrop( "cluster", "Gunpowder", Items.GUNPOWDER, 5 );
			loot.addRareDrop( "rare", "TNT", Blocks.TNT );
		}
		
		@Override
		public
		void triggerTrap( Config dimConfig, TileEntityFloorTrap trapEntity )
		{
			World world = trapEntity.getWorld( );
			
			double x = trapEntity.getPos( ).getX( ) + 0.5;
			double y = trapEntity.getPos( ).getY( ) + 1;
			double z = trapEntity.getPos( ).getZ( ) + 0.5;
			
			int fuseRange = dimConfig.FLOOR_TRAP_TNT.FUSE_TIME_MAX - dimConfig.FLOOR_TRAP_TNT.FUSE_TIME_MIN;
			if( fuseRange <= 0 ) {
				fuseRange = 1;
			}
			
			// Spawn the primed tnt blocks
			for( int i = 0; i < dimConfig.FLOOR_TRAP_TNT.TNT_COUNT; i++ ) {
				EntityTNTPrimed tntPrimed = new EntityTNTPrimed( world, x, y, z, null );
				
				float speed = dimConfig.FLOOR_TRAP_TNT.LAUNCH_SPEED * world.rand.nextFloat( ) + 0.02F;
				tntPrimed.setFuse( dimConfig.FLOOR_TRAP_TNT.FUSE_TIME_MIN + world.rand.nextInt( fuseRange ) );
				
				tntPrimed.motionX *= speed;
				tntPrimed.motionY += 0.1F * world.rand.nextFloat( );
				tntPrimed.motionZ *= speed;
				
				world.spawnEntity( tntPrimed );
			}
			world.playSound( null, x, y, z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F );
		}
	},
	
	TNT_MOB( "tnt_mob" ) {
		@Override
		public
		Config.FeatureFloorTrap getFeatureConfig( Config dimConfig ) { return dimConfig.FLOOR_TRAP_TNT_MOB; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Redstone", Items.REDSTONE, 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Iron Ingot", Items.IRON_INGOT );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.BLAST_PROTECTION, Enchantments.POWER, Enchantments.SHARPNESS
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Explosives
			loot.addClusterDrop( "cluster", "Gunpowder", Items.GUNPOWDER, 5 );
			loot.addRareDrop( "rare", "TNT", Blocks.TNT );
			loot.addRareDrop( "???", "Cake", Blocks.CAKE );
		}
		
		@Override
		public
		void triggerTrap( Config dimConfig, TileEntityFloorTrap trapEntity )
		{
			World world = trapEntity.getWorld( );
			
			double x = trapEntity.getPos( ).getX( ) + 0.5;
			double y = trapEntity.getPos( ).getY( ) + 1;
			double z = trapEntity.getPos( ).getZ( ) + 0.5;
			
			int fuseRange = dimConfig.FLOOR_TRAP_TNT_MOB.FUSE_TIME_MAX - dimConfig.FLOOR_TRAP_TNT_MOB.FUSE_TIME_MIN;
			if( fuseRange <= 0 ) {
				fuseRange = 1;
			}
			
			// Pick an entity to spawn
			ResourceLocation          registryName  = dimConfig.FLOOR_TRAP_TNT_MOB.SPAWN_LIST.nextItem( world.rand );
			Class< ? extends Entity > entityToSpawn = EntityList.getClass( registryName );
			if( entityToSpawn == null ) {
				DeadlyWorldMod.log( ).warn(
					"TNT mob floor trap received non-registered entity name '{}'" +
					" - This is probably caused by an error or change in the config for DIM_{} (defaulting to zombie)",
					registryName, world.provider.getDimension( )
				);
				entityToSpawn = EntityZombie.class;
			}
			
			// Try to create the entity to spawn
			Entity           entity;
			EntityLivingBase livingEntity = null;
			try {
				entity = entityToSpawn.getConstructor( World.class ).newInstance( world );
			}
			catch( Exception ex ) {
				DeadlyWorldMod.log( ).error( "Encountered exception while constructing entity '{}'", entityToSpawn, ex );
				entity = new EntityZombie( world );
			}
			
			// Initialize the entity
			entity.setPositionAndRotation( x, y, z, world.rand.nextFloat( ) * 2.0F * (float) Math.PI, 0.0F );
			entity.motionY = 0.3F;
			if( entity instanceof EntityLivingBase ) {
				livingEntity = (EntityLivingBase) entity;
				
				IAttributeInstance attrib;
				if( dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_HEALTH != 1.0F ) {
					try {
						attrib = livingEntity.getEntityAttribute( SharedMonsterAttributes.MAX_HEALTH );
						attrib.setBaseValue( attrib.getBaseValue( ) * dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_HEALTH );
					}
					catch( Exception ex ) {
						// This is fine, entity just doesn't have the attribute
					}
				}
				if( dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_SPEED != 1.0F ) {
					try {
						attrib = livingEntity.getEntityAttribute( SharedMonsterAttributes.MOVEMENT_SPEED );
						attrib.setBaseValue( attrib.getBaseValue( ) * dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_SPEED );
					}
					catch( Exception ex ) {
						// This is fine, entity just doesn't have the attribute
					}
				}
				livingEntity.setHealth( livingEntity.getMaxHealth( ) );
				livingEntity.setRevengeTarget( trapEntity.getTarget( ) );
			}
			
			// Make the tnt "hat"
			EntityTNTPrimed tntPrimed = new EntityTNTPrimed( world, x, y, z, livingEntity );
			tntPrimed.copyLocationAndAnglesFrom( entity );
			tntPrimed.setFuse( dimConfig.FLOOR_TRAP_TNT_MOB.FUSE_TIME_MIN + world.rand.nextInt( fuseRange ) );
			tntPrimed.startRiding( entity, true );
			
			// Spawn the entities and play alert sound
			world.spawnEntity( entity );
			world.spawnEntity( tntPrimed );
			world.playSound( null, x, y, z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F );
		}
	},
	
	POTION( "potion" ) {
		@Override
		public
		Config.FeatureFloorTrap getFeatureConfig( Config dimConfig ) { return dimConfig.FLOOR_TRAP_POTION; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Redstone", Items.REDSTONE, 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Gold Ingot", Items.GOLD_INGOT );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.PROTECTION, Enchantments.THORNS, Enchantments.SILK_TOUCH
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Potions
			loot.addLootTable( "external", "Witch Loot", LootTableList.ENTITIES_WITCH );
			loot.addUncommonDrop( "uncommon", "Brewing",
			                      Items.RABBIT_FOOT, Items.BLAZE_POWDER, Items.SPECKLED_MELON, Items.GHAST_TEAR,
			                      Items.MAGMA_CREAM, Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE );
		}
		
		@Override
		public
		void triggerTrap( Config dimConfig, TileEntityFloorTrap trapEntity )
		{
			final String TAG_POTION_TYPE = "PotionType";
			
			World world = trapEntity.getWorld( );
			
			double x = trapEntity.getPos( ).getX( ) + 0.5;
			double y = trapEntity.getPos( ).getY( ) + 1.1;
			double z = trapEntity.getPos( ).getZ( ) + 0.5;
			
			int resetRange = dimConfig.FLOOR_TRAP_POTION.RESET_TIME_MAX - dimConfig.FLOOR_TRAP_POTION.RESET_TIME_MIN;
			if( resetRange <= 0 ) {
				resetRange = 1;
			}
			trapEntity.disableTrap( dimConfig.FLOOR_TRAP_POTION.RESET_TIME_MIN + world.rand.nextInt( resetRange ) );
			
			// Load or pick the trap type
			EnumPotionTrapType type;
			NBTTagCompound     typeData = trapEntity.getOrCreateTypeData( );
			if( typeData.hasKey( TAG_POTION_TYPE, TrapHelper.NBT_TYPE_STRING ) ) {
				type = EnumPotionTrapType.fromString( typeData.getString( TAG_POTION_TYPE ) );
			}
			else {
				type = dimConfig.FLOOR_TRAP_POTION.POTION_TYPE_LIST.nextItem( world.rand );
				if( type == null ) {
					type = EnumPotionTrapType.HARM;
				}
				typeData.setString( TAG_POTION_TYPE, type.toString( ) );
			}
			ItemStack potionStack = type.getPotion( dimConfig );
			TrapHelper.setPotionColorFromEffects( potionStack );
			
			// Spawn the thrown potion
			EntityPotion potionEntity = new EntityPotion( world, x, y, z, potionStack );
			potionEntity.motionY = 0.33F + 0.04F * world.rand.nextFloat( );
			world.spawnEntity( potionEntity );
			
			world.playSound( null, x, y, z, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.0F );
		}
	};
	
	public static final PropertyEnum< EnumFloorTrapType > PROPERTY = PropertyEnum.create( "type", EnumFloorTrapType.class );
	
	public final ResourceLocation LOOT_TABLE_BLOCK;
	
	public final String NAME;
	public final String DISPLAY_NAME;
	
	EnumFloorTrapType( String name ) { this( name, name.replace( "_", " " ) + " floor traps" ); }
	
	EnumFloorTrapType( String name, String prettyName )
	{
		NAME = name;
		DISPLAY_NAME = prettyName;
		
		LOOT_TABLE_BLOCK = LootTableList.register( new ResourceLocation(
			DeadlyWorldMod.MOD_ID, ModObjects.BLOCK_LOOT_TABLE_PATH + BlockFloorTrap.ID + "/" + name ) );
	}
	
	public abstract
	Config.FeatureFloorTrap getFeatureConfig( Config dimConfig );
	
	public abstract
	void buildBlockLootTable( LootTableBuilder loot );
	
	public
	boolean canTypeBePlaced( World world, BlockPos position )
	{
		return !world.getBlockState( position.add( 0, 2, 0 ) ).isFullCube( ) &&
		       world.isAirBlock( position.add( 0, 1, 0 ) ) &&
		       world.getBlockState( position.add( 0, -1, 0 ) ).isFullCube( );
	}
	
	public abstract
	void triggerTrap( Config dimConfig, TileEntityFloorTrap trapEntity );
	
	public
	WorldGenFloorTrap makeWorldGen( ) { return new WorldGenFloorTrap( this ); }
	
	@Override
	public
	String toString( ) { return NAME; }
	
	@Override
	public
	String getName( ) { return NAME; }
	
	@Override
	public
	int getMetadata( ) { return ordinal( ); }
	
	public static
	EnumFloorTrapType byMetadata( int meta )
	{
		if( meta < 0 || meta >= values( ).length ) {
			DeadlyWorldMod.log( ).warn( "Attempted to load invalid floortrap type with metadata '{}'", meta );
			return TNT;
		}
		return values( )[ meta ];
	}
}
