package fathertoast.deadlyworld.config;

import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings( { "unused", "WeakerAccess" } )
public
class WeightedBlockConfig extends WeightedRandom.Item
{
	// Returns a new target block set from the string property.
	public static
	BlockList newTargetDefinition( String line )
	{
		String[]       fragmentedStates = line.split( "," );
		List< String > repairedStates   = new ArrayList<>( );
		
		for( int i = 0; i < fragmentedStates.length; i++ ) {
			String fragment = fragmentedStates[ i ].trim( );
			// The block state needs to be repaired if it has multiple properties
			if( !fragment.contains( "]" ) && fragment.contains( "[" ) ) {
				boolean completed = false;
				
				// Combine following strings until we find the end bracket
				StringBuilder rebuilder = new StringBuilder( fragment );
				for( i++; i < fragmentedStates.length; i++ ) {
					String subfragment = fragmentedStates[ i ].trim( );
					rebuilder.append( "," ).append( subfragment );
					if( subfragment.contains( "]" ) ) {
						completed = true;
						break;
					}
				}
				fragment = rebuilder.toString( );
				
				if( !completed ) {
					Config.log.warn( "Reached end of line while parsing block state '{}' from single-line target ({})", fragment, line );
					continue;
				}
			}
			if( !fragment.isEmpty( ) ) {
				repairedStates.add( fragment );
			}
		}
		
		return WeightedBlockConfig.newTargetDefinition( repairedStates.toArray( new String[ 0 ] ) );
	}
	
	public static
	BlockList newTargetDefinition( String[] targetableBlockStates )
	{
		List< WeightedBlockConfig > itemList = new ArrayList<>( );
		
		for( String line : targetableBlockStates ) {
			String item;
			int    weight;
			
			// Get weight before handling like a normal target block
			int i = line.lastIndexOf( ' ' );
			if( i < 0 ) {
				Config.log.error( "No weight specified for entry '{}' (Format must be 'registry_name weight')", line );
				item = line;
				weight = 0;
			}
			else {
				item = line.substring( 0, i ).trim( );
				
				String weightString = line.substring( i + 1 ).trim( );
				try {
					weight = Integer.parseInt( weightString );
				}
				catch( NumberFormatException ex ) {
					Config.log.error( "Invalid weight for config entry 'registry_name={},weight={}'", item, weightString );
					weight = 0;
				}
			}
			
			WeightedBlockConfig targetBlock = new WeightedBlockConfig( item, weight );
			if( targetBlock.BLOCK_STATE.getBlock( ) != Blocks.AIR ) {
				itemList.add( targetBlock );
			}
		}
		return new BlockList( itemList );
	}
	
	private static
	IBlockState parseState( Block block, String propList )
	{
		IBlockState blockState = block.getDefaultState( );
		if( propList.isEmpty( ) ) {
			return blockState;
		}
		
		BlockStateContainer stateContainer = block.getBlockState( );
		String[]            properties     = propList.split( "," );
		for( String combinedEntry : properties ) {
			String[] entry = combinedEntry.split( "=", 2 );
			if( entry.length != 2 ) {
				Config.log.warn( "Invalid block property entry '{}' - format must follow 'property=value'", combinedEntry );
				continue;
			}
			else if( entry[ 1 ].equals( "*" ) ) {
				Config.log.warn( "Invalid block property entry '{}' - '*' operation is not valid for this config", combinedEntry );
				continue;
			}
			
			// Parse the entry key and value
			IProperty< ? extends Comparable< ? > > property = stateContainer.getProperty( entry[ 0 ] );
			if( property == null ) {
				Config.log.warn( "Invalid block property key '{}' for block '{}'", entry[ 0 ], block.getRegistryName( ) );
				continue;
			}
			Optional< ? extends Comparable< ? > > value = property.parseValue( entry[ 1 ] );
			if( value == null || !value.isPresent( ) ) {
				Config.log.warn(
					"Invalid block property value '{}' for property key '{}' and block '{}'",
					entry[ 1 ], entry[ 0 ], block.getRegistryName( )
				);
				continue;
			}
			
			// Add the completed entry to the state
			blockState = appendProperty( blockState, property, value.get( ) );
		}
		return blockState;
	}
	
	// Used because the wildcard nature of these values makes a mess of things otherwise.
	private static
	< T extends Comparable< T > > IBlockState appendProperty( IBlockState blockState, IProperty< T > property, Comparable< ? > value )
	{
		//noinspection unchecked
		return blockState.withProperty( property, (T) value );
	}
	
	// Used because the wildcard nature of these values makes a mess of things otherwise.
	private static
	< T extends Comparable< T > > String getValueName( IBlockState blockState, IProperty< T > property )
	{
		return property.getName( blockState.getValue( property ) );
	}
	
	public final IBlockState BLOCK_STATE;
	
	// Creates a weighted block using the default state.
	public
	WeightedBlockConfig( Block block, int weight )
	{
		this( block.getDefaultState( ), weight );
	}
	
	// Creates a weighted block using a specific state.
	public
	WeightedBlockConfig( IBlockState block, int weight )
	{
		super( weight );
		BLOCK_STATE = block;
	}
	
	// Creates a target block from a string that specifies a single block and extra data.
	private
	WeightedBlockConfig( String item, int weight )
	{
		super( weight );
		String[] pair = item.split( "\\[", 2 );
		
		Block block = TargetBlock.parseBlock( pair[ 0 ] );
		if( block == Blocks.AIR || pair.length < 2 || pair[ 1 ].equalsIgnoreCase( "normal]" ) || pair[ 1 ].equalsIgnoreCase( "default]" ) ) {
			BLOCK_STATE = block.getDefaultState( );
		}
		else if( !pair[ 1 ].endsWith( "]" ) ) {
			Config.log.warn( "Ignoring properties for broken weighted blockstate definition '{}' (no end bracket found)", item );
			BLOCK_STATE = Blocks.AIR.getDefaultState( );
		}
		else {
			BLOCK_STATE = parseState( block, pair[ 1 ].substring( 0, pair[ 1 ].length( ) - 1 ) );
		}
	}
	
	@Override
	public
	String toString( )
	{
		String registryName = Block.REGISTRY.getNameForObject( BLOCK_STATE.getBlock( ) ).toString( );
		if( BLOCK_STATE.equals( BLOCK_STATE.getBlock( ).getDefaultState( ) ) ) {
			return registryName + " " + itemWeight;
		}
		
		StringBuilder str = new StringBuilder( );
		str.append( registryName ).append( "[" );
		for( IProperty< ? > property : BLOCK_STATE.getPropertyKeys( ) ) {
			str.append( property.getName( ) ).append( '=' ).append( getValueName( BLOCK_STATE, property ) ).append( ',' );
		}
		return str.substring( 0, str.length( ) - 1 ) + "] " + itemWeight;
	}
	
	public static final
	class BlockList
	{
		public final int TOTAL_WEIGHT;
		
		private final List< WeightedBlockConfig > ITEMS;
		
		public
		BlockList( List< WeightedBlockConfig > items )
		{
			ITEMS = items;
			TOTAL_WEIGHT = WeightedRandom.getTotalWeight( items );
		}
		
		public
		IBlockState nextBlock( Random random )
		{
			return TOTAL_WEIGHT > 0 ? WeightedRandom.getRandomItem( random, ITEMS, TOTAL_WEIGHT ).BLOCK_STATE : null;
		}
		
		public
		IBlockState nextBlock( Random random, Block defaultBlock )
		{
			return nextBlock( random, defaultBlock.getDefaultState( ) );
		}
		public
		IBlockState nextBlock( Random random, IBlockState defaultBlock )
		{
			return TOTAL_WEIGHT > 0 ? WeightedRandom.getRandomItem( random, ITEMS, TOTAL_WEIGHT ).BLOCK_STATE : defaultBlock;
		}
		
		@Override
		public
		String toString( )
		{
			if( ITEMS.size( ) <= 0 ) {
				return "";
			}
			StringBuilder str = new StringBuilder( );
			for( WeightedBlockConfig item : ITEMS ) {
				str.append( item ).append( ',' );
			}
			return str.substring( 0, str.length( ) - 1 );
		}
	}
}