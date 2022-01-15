package fathertoast.deadlyworld.tileentity;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Random;

public
enum EnumPotionArrowType implements WeightedEnumConfig.Meta
{
	SLOWNESS( "slowness", 4 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.SLOWNESS, dimConfig.TOWER_POTION.SLOWNESS_DURATION, dimConfig.TOWER_POTION.SLOWNESS_POTENCY ) );
		}
	},
	
	POISON( "poison", 4 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.POISON, dimConfig.TOWER_POTION.POISON_DURATION, dimConfig.TOWER_POTION.POISON_POTENCY ) );
		}
	},
	
	WITHER( "wither", 1 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.WITHER, dimConfig.TOWER_POTION.WITHER_DURATION, dimConfig.TOWER_POTION.WITHER_POTENCY ) );
		}
	},
	
	HARM( "harm", 1 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.INSTANT_DAMAGE, 0, dimConfig.TOWER_POTION.HARM_POTENCY ) );
		}
	},
	
	HUNGER( "hunger", 2 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.HUNGER, dimConfig.TOWER_POTION.HUNGER_DURATION, dimConfig.TOWER_POTION.HUNGER_POTENCY ) );
		}
	},
	
	BLINDNESS( "blindness", 2 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.BLINDNESS, dimConfig.TOWER_POTION.BLINDNESS_DURATION ) );
		}
	},
	
	WEAKNESS( "weakness", 2 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.WEAKNESS, dimConfig.TOWER_POTION.WEAKNESS_DURATION, dimConfig.TOWER_POTION.WEAKNESS_POTENCY ) );
			arrow.addEffect( new PotionEffect( MobEffects.MINING_FATIGUE, dimConfig.TOWER_POTION.WEAKNESS_DURATION, dimConfig.TOWER_POTION.WEAKNESS_POTENCY ) );
		}
	},
	
	LEVITATION( "levitation", 1 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			arrow.addEffect( new PotionEffect( MobEffects.LEVITATION, dimConfig.TOWER_POTION.LEVITATION_DURATION, dimConfig.TOWER_POTION.LEVITATION_POTENCY ) );
		}
	},
	
	// This potion arrow type must be the last declared.
	RANDOM( "random", 1 ) {
		@Override
		public
		void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow )
		{
			// Passes the method on to a random potion arrow type, excluding this one
			values( )[ random.nextInt( values( ).length - 1 ) ].addEffects( random, dimConfig, arrow );
		}
	};
	
	private final String NAME;
	public final  int    DEFAULT_WEIGHT;
	
	EnumPotionArrowType( String name, int defaultWeight )
	{
		NAME = name;
		DEFAULT_WEIGHT = defaultWeight;
	}
	
	public abstract
	void addEffects( Random random, Config dimConfig, EntityTippedArrow arrow );
	
	@Override
	public
	int defaultWeight( ) { return DEFAULT_WEIGHT; }
	
	@Override
	public
	String toString( ) { return NAME; }
	
	public static
	EnumPotionArrowType fromString( String name )
	{
		for( EnumPotionArrowType value : values( ) ) {
			if( value.NAME.equals( name ) ) {
				return value;
			}
		}
		DeadlyWorldMod.log( ).warn( "Attempted to load invalid potion arrow type with name '{}'", name );
		return RANDOM;
	}
}
