package fathertoast.deadlyworld.block;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

public
enum EnumSpawnerType implements IExclusiveMetaProvider
{
	LONE( "lone" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_LONE; }
		
		@Override
		public
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
		{
			// Cobblestone on top
			generator.setBlock(
				world, random, spawnerPos.add( 0, 1, 0 ),
				random.nextBoolean( ) ? Blocks.MOSSY_COBBLESTONE.getDefaultState( ) : Blocks.COBBLESTONE.getDefaultState( )
			);
		}
	},
	
	STREAM( "stream" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_STREAM; }
		
		@Override
		public
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
		{
			// Chiseled red sandstone on top
			generator.setBlock(
				world, random, spawnerPos.add( 0, 1, 0 ),
				Blocks.RED_SANDSTONE.getDefaultState( ).withProperty( BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.CHISELED )
			);
		}
	},
	
	SWARM( "swarm" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_SWARM; }
		
		@Override
		public
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
		{
			// Chiseled normal sandstone on top
			generator.setBlock(
				world, random, spawnerPos.add( 0, 1, 0 ),
				Blocks.SANDSTONE.getDefaultState( ).withProperty( BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED )
			);
		}
	},
	
	BRUTAL( "brutal" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_BRUTAL; }
		
		@Override
		public
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
		{
			// Chiseled stone brick on top
			generator.setBlock(
				world, random, spawnerPos.add( 0, 1, 0 ),
				Blocks.STONEBRICK.getDefaultState( ).withProperty( BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED )
			);
			
			// Place vines around outside
			for( int y = 0; y <= 1; y++ ) {
				for( EnumFacing facing : EnumFacing.HORIZONTALS ) {
					BlockPos pos = spawnerPos.add( 0, y, 0 ).offset( facing );
					if( random.nextInt( 3 ) == 0 && world.isAirBlock( pos ) ) {
						generator.setBlock(
							world, random, pos,
							Blocks.VINE.getDefaultState( ).withProperty( BlockVine.getPropertyFor( facing.getOpposite( ) ), true )
						);
					}
				}
			}
		}
		
		@Override
		public
		void initializeEntity( EntityLiving entity, Config dimConfig, World world, BlockPos pos )
		{
			super.initializeEntity( entity, dimConfig, world, pos );
			
			// Apply potion effects
			if( !(entity instanceof EntityCreeper) ) {
				boolean hide = dimConfig.SPAWNER_BRUTAL.AMBIENT_FX;
				if( dimConfig.SPAWNER_BRUTAL.FIRE_RESISTANCE ) {
					entity.addPotionEffect( new PotionEffect( MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, hide, !hide ) );
				}
				if( dimConfig.SPAWNER_BRUTAL.WATER_BREATHING ) {
					entity.addPotionEffect( new PotionEffect( MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 0, hide, !hide ) );
				}
			}
		}
	},
	
	SILVERFISH_NEST( "nest", "silverfish nest" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_SILVERFISH_NEST; }
		
		@Override
		public
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
		{
			final IBlockState infestedCobble = Blocks.MONSTER_EGG.getDefaultState( ).withProperty(
				BlockSilverfish.VARIANT, BlockSilverfish.EnumType.COBBLESTONE );
			
			// Place infested cobblestone under the spawner if no chest was generated
			BlockPos chestPos = spawnerPos.add( 0, -1, 0 );
			if( world.getBlockState( chestPos ).getBlock( ) != Blocks.CHEST ) {
				generator.setBlock( world, random, chestPos, infestedCobble );
			}
			
			// Place infested cobblestone around spawner
			generator.setBlock( world, random, spawnerPos.add( 0, 1, 0 ), infestedCobble );
			for( int y = -1; y <= 1; y++ ) {
				generator.setBlock( world, random, spawnerPos.add( -1, y, 0 ), infestedCobble );
				generator.setBlock( world, random, spawnerPos.add( 0, y, -1 ), infestedCobble );
				generator.setBlock( world, random, spawnerPos.add( 1, y, 0 ), infestedCobble );
				generator.setBlock( world, random, spawnerPos.add( 0, y, 1 ), infestedCobble );
			}
			generator.setBlock( world, random, spawnerPos.add( -1, 0, -1 ), infestedCobble );
			generator.setBlock( world, random, spawnerPos.add( -1, 0, 1 ), infestedCobble );
			generator.setBlock( world, random, spawnerPos.add( 1, 0, -1 ), infestedCobble );
			generator.setBlock( world, random, spawnerPos.add( 1, 0, 1 ), infestedCobble );
		}
	};
	
	public static final PropertyEnum< EnumSpawnerType > PROPERTY = PropertyEnum.create( "type", EnumSpawnerType.class );
	
	public final ResourceLocation lootTable;
	
	private final String nameId;
	
	public final String displayName;
	
	EnumSpawnerType( String name ) { this( name, name + " spawner" ); }
	
	EnumSpawnerType( String name, String prettyName )
	{
		nameId = name;
		lootTable = LootTableList.register( new ResourceLocation( DeadlyWorldMod.MOD_ID, "spawners/" + name ) );
		displayName = prettyName;
	}
	
	public abstract
	Config.FeatureSpawner getFeatureConfig( Config dimConfig );
	
	public abstract
	void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random );
	
	public
	void initializeEntity( EntityLiving entity, Config dimConfig, World world, BlockPos pos )
	{
		Config.FeatureSpawner config = getFeatureConfig( dimConfig );
		
		// Flat boosts
		if( config.ADDED_ARMOR > 0.0F ) {
			addAttribute( entity, SharedMonsterAttributes.ARMOR, config.ADDED_ARMOR );
		}
		if( config.ADDED_KNOCKBACK_RESIST > 0.0F ) {
			addAttribute( entity, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, config.ADDED_KNOCKBACK_RESIST );
		}
		if( config.ADDED_ARMOR_TOUGHNESS > 0.0F ) {
			addAttribute( entity, SharedMonsterAttributes.ARMOR_TOUGHNESS, config.ADDED_ARMOR_TOUGHNESS );
		}
		
		// Multipliers
		if( config.MULTIPLIER_DAMAGE != 1.0F ) {
			multAttribute( entity, SharedMonsterAttributes.ATTACK_DAMAGE, config.MULTIPLIER_DAMAGE );
		}
		if( config.MULTIPLIER_HEALTH != 1.0F ) {
			multAttribute( entity, SharedMonsterAttributes.MAX_HEALTH, config.MULTIPLIER_HEALTH );
		}
		if( config.MULTIPLIER_SPEED != 1.0F ) {
			multAttribute( entity, SharedMonsterAttributes.MOVEMENT_SPEED, config.MULTIPLIER_SPEED );
		}
		entity.setHealth( entity.getMaxHealth( ) );
	}
	
	public
	WorldGenSpawner makeWorldGen( ) { return new WorldGenSpawner( this ); }
	
	@Override
	public
	String toString( ) { return nameId; }
	
	@Override
	public
	String getName( ) { return nameId; }
	
	@Override
	public
	int getMetadata( ) { return ordinal( ); }
	
	public static
	EnumSpawnerType byMetadata( int meta )
	{
		if( meta < 0 || meta >= values( ).length ) {
			DeadlyWorldMod.log( ).warn( "Attempted to load invalid spawner type with metadata '{}'", meta );
			return LONE;
		}
		return values( )[ meta ];
	}
	
	private static
	void addAttribute( EntityLivingBase entity, IAttribute attribute, double amount )
	{
		entity.getEntityAttribute( attribute ).setBaseValue( entity.getEntityAttribute( attribute ).getBaseValue( ) + amount );
	}
	
	private static
	void multAttribute( EntityLivingBase entity, IAttribute attribute, double amount )
	{
		entity.getEntityAttribute( attribute ).setBaseValue( entity.getEntityAttribute( attribute ).getBaseValue( ) * amount );
	}
}
