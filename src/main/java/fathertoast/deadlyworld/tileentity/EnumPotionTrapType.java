package fathertoast.deadlyworld.tileentity;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

import java.util.Arrays;
import java.util.Collections;

public
enum EnumPotionTrapType implements WeightedEnumConfig.Meta
{
	HARM( "harm", 10 ) {
		@Override
		public
		ItemStack getPotion( Config dimConfig )
		{
			return PotionUtils.appendEffects( new ItemStack( Items.LINGERING_POTION ), Collections.singleton(
				new PotionEffect( MobEffects.INSTANT_DAMAGE, 0, dimConfig.FLOOR_TRAP_POTION.HARM_POTENCY )
			) );
		}
	},
	
	POISON( "poison", 5 ) {
		@Override
		public
		ItemStack getPotion( Config dimConfig )
		{
			return PotionUtils.appendEffects( new ItemStack( Items.SPLASH_POTION ), Collections.singleton(
				new PotionEffect( MobEffects.POISON, dimConfig.FLOOR_TRAP_POTION.POISON_DURATION, dimConfig.FLOOR_TRAP_POTION.POISON_POTENCY )
			) );
		}
	},
	
	HUNGER( "hunger", 5 ) {
		@Override
		public
		ItemStack getPotion( Config dimConfig )
		{
			return PotionUtils.appendEffects( new ItemStack( Items.SPLASH_POTION ), Collections.singleton(
				new PotionEffect( MobEffects.HUNGER, dimConfig.FLOOR_TRAP_POTION.HUNGER_DURATION, dimConfig.FLOOR_TRAP_POTION.HUNGER_POTENCY )
			) );
		}
	},
	
	DAZE( "daze", 5 ) {
		@Override
		public
		ItemStack getPotion( Config dimConfig )
		{
			return PotionUtils.appendEffects( new ItemStack( Items.SPLASH_POTION ), Arrays.asList(
				new PotionEffect( MobEffects.WEAKNESS, dimConfig.FLOOR_TRAP_POTION.DAZE_DURATION, dimConfig.FLOOR_TRAP_POTION.DAZE_POTENCY ),
				new PotionEffect( MobEffects.MINING_FATIGUE, dimConfig.FLOOR_TRAP_POTION.DAZE_DURATION, dimConfig.FLOOR_TRAP_POTION.DAZE_POTENCY ),
				new PotionEffect( MobEffects.SLOWNESS, dimConfig.FLOOR_TRAP_POTION.DAZE_DURATION >> 1, dimConfig.FLOOR_TRAP_POTION.DAZE_POTENCY ),
				new PotionEffect( MobEffects.BLINDNESS, dimConfig.FLOOR_TRAP_POTION.DAZE_DURATION >> 2, 0 ),
				new PotionEffect( MobEffects.INSTANT_DAMAGE, 0, 0 )
			) );
		}
	},
	
	LEVITATION( "levitation", 5 ) {
		@Override
		public
		ItemStack getPotion( Config dimConfig )
		{
			return PotionUtils.appendEffects( new ItemStack( Items.SPLASH_POTION ), Collections.singleton(
				new PotionEffect( MobEffects.LEVITATION, dimConfig.FLOOR_TRAP_POTION.LEVITATION_DURATION, dimConfig.FLOOR_TRAP_POTION.LEVITATION_POTENCY )
			) );
		}
	};
	
	private final String NAME;
	public final  int    DEFAULT_WEIGHT;
	
	EnumPotionTrapType( String name, int defaultWeight )
	{
		NAME = name;
		DEFAULT_WEIGHT = defaultWeight;
	}
	
	public abstract
	ItemStack getPotion( Config dimConfig );
	
	@Override
	public
	int defaultWeight( ) { return DEFAULT_WEIGHT; }
	
	@Override
	public
	String toString( ) { return NAME; }
	
	public static
	EnumPotionTrapType fromString( String name )
	{
		for( EnumPotionTrapType value : values( ) ) {
			if( value.NAME.equals( name ) ) {
				return value;
			}
		}
		DeadlyWorldMod.log( ).warn( "Attempted to load invalid potion trap type with name '{}'", name );
		return EnumPotionTrapType.HARM;
	}
}
