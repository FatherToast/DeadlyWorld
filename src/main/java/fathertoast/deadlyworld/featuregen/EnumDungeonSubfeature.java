package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;

public
enum EnumDungeonSubfeature implements WeightedEnumConfig.Meta
{
	SPAWNER( EnumSpawnerType.DUNGEON, 100 ),
	
	TOWER_SIMPLE( EnumTowerType.DEFAULT, 10 ),
	
	TOWER_FIRE( EnumTowerType.FIRE, 10 ),
	
	TOWER_POTION( EnumTowerType.POTION, 10 ),
	
	TOWER_GATLING( EnumTowerType.GATLING, 5 ),
	
	TOWER_FIREBALL( EnumTowerType.FIREBALL, EnumTowerType.FIREBALL.NAME, 5 );
	
	private final String NAME;
	public final  int    DEFAULT_WEIGHT;
	
	private final Object TYPE;
	private WorldGenDeadlyWorldFeature FEATURE;
	
	EnumDungeonSubfeature( EnumSpawnerType type, int defaultWeight )
	{
		NAME = "spawner_" + type.NAME;
		DEFAULT_WEIGHT = defaultWeight;
		TYPE = type;
	}
	
	EnumDungeonSubfeature( EnumTowerType type, int defaultWeight )
	{
		this( type, type.NAME + "_arrow", defaultWeight );
	}
	
	EnumDungeonSubfeature( EnumTowerType type, String name, int defaultWeight )
	{
		NAME = "tower_" + name;
		DEFAULT_WEIGHT = defaultWeight;
		TYPE = type;
	}
	
	public
	WorldGenDeadlyWorldFeature getWorldGen( )
	{
		if( FEATURE == null ) {
			if( TYPE instanceof EnumTowerType ) {
				FEATURE = ((EnumTowerType) TYPE).makeWorldGen( );
			}
			else if( TYPE instanceof EnumSpawnerType ) {
				FEATURE = ((EnumSpawnerType) TYPE).makeWorldGen( );
			}
		}
		return FEATURE;
	}
	
	@Override
	public
	int defaultWeight( ) { return DEFAULT_WEIGHT; }
	
	@Override
	public
	String toString( ) { return NAME; }
}
