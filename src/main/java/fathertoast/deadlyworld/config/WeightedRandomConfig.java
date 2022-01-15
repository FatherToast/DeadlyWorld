package fathertoast.deadlyworld.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public
class WeightedRandomConfig
{
	private final int          TOTAL_WEIGHT;
	private final List< Item > ITEMS = new ArrayList<>( );
	
	public
	WeightedRandomConfig( String line )
	{
		this( line.split( "," ) );
	}
	
	WeightedRandomConfig( String[] list )
	{
		for( String item : list ) {
			String entry;
			int    weight;
			
			int i = item.lastIndexOf( ' ' );
			if( i < 0 ) {
				Config.log.error( "No weight specified for entry '{}' (Format must be 'registry_name weight')", item );
				entry = item;
				weight = 0;
			}
			else {
				entry = item.substring( 0, i ).trim( );
				
				String weightString = item.substring( i + 1 ).trim( );
				try {
					weight = Integer.parseInt( weightString );
				}
				catch( NumberFormatException ex ) {
					Config.log.error( "Invalid weight for config entry 'registry_name={},weight={}'", entry, weightString );
					weight = 0;
				}
			}
			
			ITEMS.add( new Item( new ResourceLocation( entry ), weight ) );
		}
		TOTAL_WEIGHT = WeightedRandom.getTotalWeight( ITEMS );
	}
	
	public
	ResourceLocation nextItem( Random random )
	{
		return TOTAL_WEIGHT <= 0 ? null : WeightedRandom.getRandomItem( random, ITEMS, TOTAL_WEIGHT ).itemResource;
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
	class Item extends WeightedRandom.Item
	{
		private final ResourceLocation itemResource;
		
		Item( Class< ? extends Entity > entityClass, int weight )
		{
			this( EntityList.getKey( entityClass ), weight );
		}
		
		private
		Item( ResourceLocation resource, int weight )
		{
			super( weight );
			itemResource = resource;
		}
		
		@Override
		public
		String toString( ) { return itemResource.toString( ) + " " + itemWeight; }
	}
}
