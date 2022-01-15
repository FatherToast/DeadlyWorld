package fathertoast.deadlyworld.config;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

/**
 * Used to store config data relative to the world. Smaller-scope environments (eg Biome) take priority over larger ones (eg Dimension).
 * <p>
 * This is a modified version that disables dimensions, as the configs here are per-dimension.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@SuppressWarnings( { "WeakerAccess", "unused" } )
public abstract
class TargetEnvironment implements Comparable< TargetEnvironment >
{
	public static
	TargetEnvironment read( String line )
	{
		// Get the value parameter
		String[] itemList = line.split( "=", 2 );
		float    value;
		if( itemList.length < 2 ) {
			Config.log.error( "Ignoring config line '{}' - contains no value parameter", line );
			return null;
		}
		try {
			value = Float.parseFloat( itemList[ 1 ].trim( ) );
		}
		catch( Exception ex ) {
			Config.log.error( "Exception occurred while reading config line '{}'", line, ex );
			return null;
		}
		
		// Load depending on target type
		String[] target = itemList[ 0 ].trim( ).split( "/", 2 );
		if( target.length < 2 ) {
			Config.log.error( "Ignoring config line '{}' - must declare environment (e.g., 'biome:' or 'dimension:')", line );
		}
		else if( target[ 0 ].equalsIgnoreCase( "biome" ) ) {
			if( !target[ 1 ].endsWith( "*" ) ) {
				return new TargetBiome( 0, value, target[ 1 ] );
			}
			else {
				return new TargetBiomeGroup( 1, value, target[ 1 ].substring( target[ 1 ].length( ) - 1 ) );
			}
		}
		else if( target[ 0 ].equalsIgnoreCase( "dimension" ) ) {
			// The modified part, skip dimension targets
			Config.log.error( "Ignoring config line '{}' - 'dimension' is invalid in per-dimension configs", line );
		}
		else {
			Config.log.error( "Ignoring config line '{}' - unrecognized environment '{}'", line, target[ 0 ] );
		}
		return null;
	}
	
	private final int   priority;
	private final float value;
	
	TargetEnvironment( int scope, float val )
	{
		priority = scope;
		value = val;
	}
	
	public
	float getValue( ) { return value; }
	
	public abstract
	boolean applies( EnvironmentListConfig.LocationInfo location );
	
	@Override
	public
	int compareTo( TargetEnvironment other ) { return priority - other.priority; }
	
	@Override
	public
	String toString( ) { return "=" + value; }
	
	public static
	class TargetBiome extends TargetEnvironment
	{
		private final ResourceLocation registryName;
		private final int              intId;
		
		// Used to make on-demand targets
		public
		TargetBiome( Biome biome, float val )
		{
			super( -1, val );
			registryName = biome.getRegistryName( );
			intId = Biome.REGISTRY.getIDForObject( biome );
		}
		
		TargetBiome( int scope, float val, String biomeId )
		{
			super( scope, val );
			
			Biome biome = Biome.REGISTRY.getObject( new ResourceLocation( biomeId ) );
			if( biome != null ) {
				registryName = biome.getRegistryName( );
				intId = Biome.REGISTRY.getIDForObject( biome );
			}
			else {
				ResourceLocation regName = null;
				int              id      = -1;
				try {
					id = Integer.parseInt( biomeId );
					biome = Biome.REGISTRY.getObjectById( id );
					if( biome != null ) {
						regName = Biome.REGISTRY.getNameForObject( biome );
						Config.log.warn( "Numerical id (biome/{}) used for biome with string id 'biome/{}'! " +
						                 "Please avoid using numerical ids.", id, regName );
					}
					else {
						Config.log.info( "Biome with numerical id 'biome/{}' is invalid or not yet registered. " +
						                 "Please set the biome's mod before this mod in the load order. " +
						                 "Also stop using numerical ids, you hooligan.", id );
					}
				}
				catch( NumberFormatException ex ) {
					regName = new ResourceLocation( biomeId );
					Config.log.info( "Biome 'biome/{}' is invalid or not yet registered. " +
					                 "Please set the biome's mod before this mod in the load order.", regName );
				}
				registryName = regName;
				intId = id;
			}
		}
		
		@Override
		public
		boolean applies( EnvironmentListConfig.LocationInfo location )
		{
			return registryName != null ?
			       registryName.equals( location.biomeName ) :
			       intId == Biome.REGISTRY.getIDForObject( location.biome );
		}
		
		@Override
		public
		String toString( ) { return "biome/" + registryName + super.toString( ); }
	}
	
	public static
	class TargetBiomeGroup extends TargetEnvironment
	{
		private final String registryNamePrefix;
		
		// Used to make on-demand targets
		public
		TargetBiomeGroup( String prefix, float val )
		{
			super( -1, val );
			registryNamePrefix = new ResourceLocation( prefix ).toString( );
		}
		
		TargetBiomeGroup( int scope, float val, String biomeId )
		{
			super( scope, val );
			if( biomeId.isEmpty( ) ) {
				Config.log.warn( "Detected empty biome group string 'biome/*' - this matches all biomes in the 'minecraft:' namespace. " +
				                 "Please use 'biome/minecraft:*' instead if this is your intended purpose!" );
			}
			registryNamePrefix = new ResourceLocation( biomeId ).toString( );
		}
		
		@Override
		public
		boolean applies( EnvironmentListConfig.LocationInfo location )
		{
			return location.biomeName.toString( ).startsWith( registryNamePrefix );
		}
		
		@Override
		public
		String toString( ) { return "biome/" + registryNamePrefix + "*" + super.toString( ); }
	}
}
