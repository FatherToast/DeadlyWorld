package fathertoast.deadlyworld.item;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;

import java.util.Random;

public
enum EnumPotionCloudType implements WeightedEnumConfig.Meta
{
	POISON( "poison", 1 ) {
		@Override
		public
		void addEffects( Config dimConfig, EntityAreaEffectCloud cloud )
		{
			cloud.addEffect( new PotionEffect( MobEffects.POISON, dimConfig.CHEST_SURPRISE.GAS_POISON_DURATION, dimConfig.CHEST_SURPRISE.GAS_POISON_POTENCY ) );
		}
	},
	
	WITHER( "wither", 0 ) {
		@Override
		public
		void addEffects( Config dimConfig, EntityAreaEffectCloud cloud )
		{
			cloud.addEffect( new PotionEffect( MobEffects.WITHER, dimConfig.CHEST_SURPRISE.GAS_WITHER_DURATION, dimConfig.CHEST_SURPRISE.GAS_WITHER_POTENCY ) );
		}
	},
	
	HARM( "harm", 0 ) {
		@Override
		public
		void addEffects( Config dimConfig, EntityAreaEffectCloud cloud )
		{
			cloud.addEffect( new PotionEffect( MobEffects.INSTANT_DAMAGE, 0, dimConfig.CHEST_SURPRISE.GAS_HARM_POTENCY ) );
		}
	};
	
	private final String NAME;
	public final  int    DEFAULT_WEIGHT;
	
	EnumPotionCloudType( String name, int defaultWeight )
	{
		NAME = name;
		DEFAULT_WEIGHT = defaultWeight;
	}
	
	public abstract
	void addEffects( Config dimConfig, EntityAreaEffectCloud cloud );
	
	@Override
	public
	int defaultWeight( ) { return DEFAULT_WEIGHT; }
	
	@Override
	public
	String toString( ) { return NAME; }
	
	public static
	EnumPotionCloudType fromString( String name )
	{
		for( EnumPotionCloudType value : values( ) ) {
			if( value.NAME.equals( name ) ) {
				return value;
			}
		}
		DeadlyWorldMod.log( ).warn( "Attempted to load invalid potion cloud type with name '{}'", name );
		return POISON;
	}
}
