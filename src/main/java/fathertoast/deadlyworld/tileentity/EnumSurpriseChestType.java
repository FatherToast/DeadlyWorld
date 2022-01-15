package fathertoast.deadlyworld.tileentity;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.item.*;
import fathertoast.deadlyworld.loot.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public
enum EnumSurpriseChestType implements WeightedEnumConfig.Meta
{
	TNT( EnumDeadlyEventType.TNT, 10 ) {
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			super.buildChestLootTable( loot );
			loot.addThemePoolExplosives( );
			loot.addLootTable( "base", "Vanilla Chest", LootTableList.CHESTS_SIMPLE_DUNGEON );
		}
	},
	
	LAVA( EnumDeadlyEventType.LAVA, 5 ) {
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			super.buildChestLootTable( loot );
			loot.addThemePoolFire( );
			loot.addLootTable( "base", "Vanilla Chest", LootTableList.CHESTS_SIMPLE_DUNGEON );
		}
	},
	
	POISON_GAS( EnumDeadlyEventType.POISON_GAS, 5 ) {
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			super.buildChestLootTable( loot );
			loot.addThemePoolBrewing( );
			loot.addLootTable( "base", "Vanilla Chest", LootTableList.CHESTS_SIMPLE_DUNGEON );
		}
	};
	
	public final ResourceLocation LOOT_TABLE_CHEST;
	
	public final String NAME;
	public final int    DEFAULT_WEIGHT;
	
	public final EnumDeadlyEventType EVENT;
	
	EnumSurpriseChestType( EnumDeadlyEventType event, int defaultWeight )
	{
		NAME = event.NAME;
		DEFAULT_WEIGHT = defaultWeight;
		
		EVENT = event;
		
		LOOT_TABLE_CHEST = LootTableList.register( new ResourceLocation(
			DeadlyWorldMod.MOD_ID,
			FeatureGenerator.CHEST_LOOT_TABLE_PATH + EnumChestType.FEATURE_PATH +
			EnumChestType.SURPRISE.NAME + "/" + event.NAME ) );
	}
	
	public
	void buildChestLootTable( LootTableBuilder loot )
	{
		// Events should always be the first entry
		loot.addLootTable( "surprise", "Event", EVENT.LOOT_TABLE );
	}
	
	@Override
	public
	int defaultWeight( ) { return DEFAULT_WEIGHT; }
	
	@Override
	public
	String toString( ) { return NAME; }
}
