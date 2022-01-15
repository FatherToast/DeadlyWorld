package fathertoast.deadlyworld.config;

import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@SuppressWarnings( { "unused" } )
public
class WeightedEnumConfig< T extends Enum< T > & WeightedEnumConfig.Meta >
{
	private final int               TOTAL_WEIGHT;
	private final List< Item< T > > ITEMS = new ArrayList<>( );
	
	WeightedEnumConfig( Collection< Item< T > > items )
	{
		ITEMS.addAll( items );
		TOTAL_WEIGHT = WeightedRandom.getTotalWeight( ITEMS );
	}
	
	public
	T nextItem( Random random )
	{
		return TOTAL_WEIGHT <= 0 ? null : WeightedRandom.getRandomItem( random, ITEMS, TOTAL_WEIGHT ).itemValue;
	}
	
	@Override
	public
	String toString( )
	{
		if( ITEMS.size( ) <= 0 ) {
			return "";
		}
		StringBuilder str = new StringBuilder( );
		for( Item item : ITEMS ) {
			str.append( item ).append( ',' );
		}
		return str.substring( 0, str.length( ) - 1 );
	}
	
	public
	String[] toStringArray( )
	{
		String[] array = new String[ ITEMS.size( ) ];
		for( int i = 0; i < ITEMS.size( ); i++ ) {
			array[ i ] = ITEMS.get( i ).toString( );
		}
		return array;
	}
	
	static
	class Item< T extends Enum< T > > extends WeightedRandom.Item
	{
		private final T itemValue;
		
		Item( T value, int weight )
		{
			super( weight );
			itemValue = value;
		}
		
		@Override
		public
		String toString( ) { return itemValue.toString( ) + " " + itemWeight; }
	}
	
	public
	interface Meta
	{
		int defaultWeight( );
	}
}
