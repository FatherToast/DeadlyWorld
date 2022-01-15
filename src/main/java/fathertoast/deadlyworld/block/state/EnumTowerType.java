package fathertoast.deadlyworld.block.state;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.loot.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.*;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public
enum EnumTowerType implements IExclusiveMetaProvider
{
	DEFAULT( "simple" ) {
		@Override
		public
		Config.FeatureTower getFeatureConfig( Config dimConfig ) { return dimConfig.TOWER_DEFAULT; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Arrow", Items.ARROW, 2, 16 );
			loot.addClusterDrop( "cluster", "Redstone", Items.REDSTONE );
			loot.addPool(
				new LootPoolBuilder( "rare" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchanted Bow", Items.BOW ).enchant( 30, true ).toLootEntry( ) )
					.toLootPool( )
			);
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.PROJECTILE_PROTECTION, Enchantments.POWER, Enchantments.PUNCH, Enchantments.INFINITY
					).toLootEntry( ) )
					.toLootPool( )
			);
		}
		
		@Override
		public
		void triggerAttack( Config dimConfig, TileEntityTowerDispenser towerEntity, EntityPlayer target,
		                    Vec3d center, Vec3d offset, Vec3d vecToTarget, double distanceH )
		{
			EntityArrow arrow = new EntityTippedArrow( towerEntity.getWorld( ) );
			towerEntity.shootArrow(
				center, offset, vecToTarget, distanceH,
				getFeatureConfig( dimConfig ).PROJECTILE_SPEED, getFeatureConfig( dimConfig ).PROJECTILE_VARIANCE, arrow
			);
		}
	},
	
	FIRE( "fire" ) {
		@Override
		public
		Config.FeatureTower getFeatureConfig( Config dimConfig ) { return dimConfig.TOWER_FIRE; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Arrow", Items.ARROW, 2, 16 );
			loot.addClusterDrop( "cluster", "Redstone", Items.REDSTONE );
			loot.addPool(
				new LootPoolBuilder( "rare" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchanted Bow", Items.BOW ).enchant( 30, true ).toLootEntry( ) )
					.toLootPool( )
			);
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.PROJECTILE_PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.FLAME, Enchantments.INFINITY
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Fire
			loot.addSemicommonDrop( "semicommon", "Coal", Items.COAL );
			loot.addUncommonDrop( "uncommon", "Fire Charge", Items.FIRE_CHARGE );
		}
		
		@Override
		public
		void triggerAttack( Config dimConfig, TileEntityTowerDispenser towerEntity, EntityPlayer target,
		                    Vec3d center, Vec3d offset, Vec3d vecToTarget, double distanceH )
		{
			EntityArrow arrow = new EntityTippedArrow( towerEntity.getWorld( ) );
			arrow.setFire( 100 );
			towerEntity.shootArrow(
				center, offset, vecToTarget, distanceH,
				getFeatureConfig( dimConfig ).PROJECTILE_SPEED, getFeatureConfig( dimConfig ).PROJECTILE_VARIANCE, arrow
			);
		}
	},
	
	POTION( "potion" ) {
		@Override
		public
		Config.FeatureTower getFeatureConfig( Config dimConfig ) { return dimConfig.TOWER_POTION; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Arrow", Items.ARROW, 2, 16 );
			loot.addClusterDrop( "cluster", "Redstone", Items.REDSTONE );
			loot.addPool(
				new LootPoolBuilder( "rare" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchanted Bow", Items.BOW ).enchant( 30, true ).toLootEntry( ) )
					.toLootPool( )
			);
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.PROJECTILE_PROTECTION, Enchantments.PROTECTION, Enchantments.THORNS, Enchantments.INFINITY
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Potions
			loot.addLootTable( "external", "Witch Loot", LootTableList.ENTITIES_WITCH );
			loot.addUncommonDrop( "uncommon", "Brewing",
			                      Items.RABBIT_FOOT, Items.BLAZE_POWDER, Items.SPECKLED_MELON, Items.GHAST_TEAR,
			                      Items.MAGMA_CREAM, Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE );
			loot.addPool(
				new LootPoolBuilder( "potionarrows" )
					.addConditions( LootPoolBuilder.UNCOMMON_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Poison Arrow", PotionUtils.addPotionToItemStack(
						new ItemStack( Items.TIPPED_ARROW ), PotionTypes.POISON ) ).setCount( 1, 8 ).toLootEntry( ) )
					.addEntry( new LootEntryItemBuilder( "Harm Arrow", PotionUtils.addPotionToItemStack(
						new ItemStack( Items.TIPPED_ARROW ), PotionTypes.HARMING ) ).setCount( 1, 8 ).toLootEntry( ) )
					.addEntry( new LootEntryItemBuilder( "Weakness Arrow", PotionUtils.addPotionToItemStack(
						new ItemStack( Items.TIPPED_ARROW ), PotionTypes.WEAKNESS ) ).setCount( 1, 8 ).toLootEntry( ) )
					.addEntry( new LootEntryItemBuilder( "Slowness Arrow", PotionUtils.addPotionToItemStack(
						new ItemStack( Items.TIPPED_ARROW ), PotionTypes.SLOWNESS ) ).setCount( 1, 8 ).toLootEntry( ) )
					.addEntry( new LootEntryItemBuilder( "Poison II Arrow", PotionUtils.addPotionToItemStack(
						new ItemStack( Items.TIPPED_ARROW ), PotionTypes.STRONG_POISON ) ).setCount( 1, 8 ).toLootEntry( ) )
					.addEntry( new LootEntryItemBuilder( "Harm II Arrow", PotionUtils.addPotionToItemStack(
						new ItemStack( Items.TIPPED_ARROW ), PotionTypes.STRONG_HARMING ) ).setCount( 1, 8 ).toLootEntry( ) )
					.toLootPool( )
			);
		}
		
		@Override
		public
		void triggerAttack( Config dimConfig, TileEntityTowerDispenser towerEntity, EntityPlayer target,
		                    Vec3d center, Vec3d offset, Vec3d vecToTarget, double distanceH )
		{
			final String TAG_POTION_TYPE = "PotionType";
			
			World world = towerEntity.getWorld( );
			
			// Load or pick the potion arrow type
			EnumPotionArrowType type;
			NBTTagCompound      typeData = towerEntity.getOrCreateTypeData( );
			if( typeData.hasKey( TAG_POTION_TYPE, TrapHelper.NBT_TYPE_STRING ) ) {
				type = EnumPotionArrowType.fromString( typeData.getString( TAG_POTION_TYPE ) );
			}
			else {
				type = dimConfig.TOWER_POTION.POTION_TYPE_LIST.nextItem( world.rand );
				if( type == null ) {
					type = EnumPotionArrowType.RANDOM;
				}
				typeData.setString( TAG_POTION_TYPE, type.toString( ) );
			}
			
			// Create the arrow
			EntityTippedArrow arrow = new EntityTippedArrow( towerEntity.getWorld( ) );
			type.addEffects( world.rand, dimConfig, arrow );
			towerEntity.shootArrow(
				center, offset, vecToTarget, distanceH,
				getFeatureConfig( dimConfig ).PROJECTILE_SPEED, getFeatureConfig( dimConfig ).PROJECTILE_VARIANCE, arrow
			);
		}
	},
	
	GATLING( "gatling" ) {
		@Override
		public
		Config.FeatureTower getFeatureConfig( Config dimConfig ) { return dimConfig.TOWER_GATLING; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Arrow", Items.ARROW, 4, 24 );
			loot.addClusterDrop( "cluster", "Redstone", Items.REDSTONE );
			loot.addPool(
				new LootPoolBuilder( "rare" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchanted Bow", Items.BOW ).enchant( 30, true ).toLootEntry( ) )
					.toLootPool( )
			);
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.PROJECTILE_PROTECTION, Enchantments.PUNCH, Enchantments.INFINITY
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Valuable
			loot.addClusterDrop( "cluster2", "Lapis Lazuli", new ItemStack( Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage( ) ) );
			loot.addRareDrop( "money", "Gem", Items.DIAMOND, Items.EMERALD );
		}
		
		@Override
		public
		void triggerAttack( Config dimConfig, TileEntityTowerDispenser towerEntity, EntityPlayer target,
		                    Vec3d center, Vec3d offset, Vec3d vecToTarget, double distanceH )
		{
			EntityArrow arrow = new EntityTippedArrow( towerEntity.getWorld( ) );
			towerEntity.shootArrow(
				center, offset, vecToTarget, distanceH,
				getFeatureConfig( dimConfig ).PROJECTILE_SPEED, getFeatureConfig( dimConfig ).PROJECTILE_VARIANCE, arrow
			);
		}
	},
	
	FIREBALL( "fireball", "fireball tower traps" ) {
		@Override
		public
		Config.FeatureTower getFeatureConfig( Config dimConfig ) { return dimConfig.TOWER_FIREBALL; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addSemicommonDrop( "semicommon", "Quartz", Items.QUARTZ );
			loot.addClusterDrop( "cluster", "Redstone", Items.REDSTONE );
			loot.addClusterDrop( "cluster2", "Lapis Lazuli", new ItemStack( Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage( ) ) );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.PROJECTILE_PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.FLAME, Enchantments.FIRE_ASPECT
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Fire
			loot.addCommonDrop( "common", "Fire Charge", Items.FIRE_CHARGE, 1, 8 );
			loot.addUncommonDrop( "uncommon", "Blaze Powder", Items.BLAZE_POWDER );
		}
		
		@Override
		public
		void triggerAttack( Config dimConfig, TileEntityTowerDispenser towerEntity, EntityPlayer target,
		                    Vec3d center, Vec3d offset, Vec3d vecToTarget, double distanceH )
		{
			final double spawnOffset = 0.6;
			
			World    world    = towerEntity.getWorld( );
			BlockPos topBlock = towerEntity.getPos( ).add( 0, 1, 0 );
			if( world.isAirBlock( topBlock ) ) {
				world.setBlockState( topBlock, Blocks.FIRE.getDefaultState( ) );
			}
			
			float accel = getFeatureConfig( dimConfig ).PROJECTILE_SPEED;
			float var   = (float) Math.sqrt( distanceH ) / 12.0F * getFeatureConfig( dimConfig ).PROJECTILE_VARIANCE;
			
			for( float count = getFeatureConfig( dimConfig ).ATTACK_DAMAGE; count >= 1.0F || count > 0.0F && count > world.rand.nextFloat( ); count-- ) {
				EntitySmallFireball fireball = new EntitySmallFireball(
					world, center.x + offset.x * spawnOffset, center.y, center.z + offset.z * spawnOffset,
					vecToTarget.x * accel + world.rand.nextGaussian( ) * var,
					vecToTarget.y * accel + world.rand.nextGaussian( ) * var,
					vecToTarget.z * accel + world.rand.nextGaussian( ) * var
				);
				world.spawnEntity( fireball );
			}
			
			towerEntity.getWorld( ).playSound( null, center.x, center.y, center.z, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS,
			                                   1.0F, 1.0F / (towerEntity.getWorld( ).rand.nextFloat( ) * 0.4F + 0.8F) );
		}
	};
	
	public static final PropertyEnum< EnumTowerType > PROPERTY = PropertyEnum.create( "type", EnumTowerType.class );
	
	public final ResourceLocation LOOT_TABLE_BLOCK;
	
	public final String NAME;
	public final String DISPLAY_NAME;
	
	EnumTowerType( String name ) { this( name, name.replace( "_", " " ) + " arrow tower traps" ); }
	
	EnumTowerType( String name, String prettyName )
	{
		NAME = name;
		DISPLAY_NAME = prettyName;
		
		LOOT_TABLE_BLOCK = LootTableList.register( new ResourceLocation(
			DeadlyWorldMod.MOD_ID, ModObjects.BLOCK_LOOT_TABLE_PATH + BlockTowerDispenser.ID + "/" + name ) );
	}
	
	public abstract
	Config.FeatureTower getFeatureConfig( Config dimConfig );
	
	public abstract
	void buildBlockLootTable( LootTableBuilder loot );
	
	public abstract
	void triggerAttack( Config dimConfig, TileEntityTowerDispenser towerEntity, EntityPlayer target,
	                    Vec3d center, Vec3d offset, Vec3d vecToTarget, double distanceH );
	
	public
	WorldGenTowerTrap makeWorldGen( ) { return new WorldGenTowerTrap( this ); }
	
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
	EnumTowerType byMetadata( int meta )
	{
		if( meta < 0 || meta >= values( ).length ) {
			DeadlyWorldMod.log( ).warn( "Attempted to load invalid towertrap type with metadata '{}'", meta );
			return DEFAULT;
		}
		return values( )[ meta ];
	}
}
