package fathertoast.deadlyworld;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for using the ObfuscationReflectionHelper.
 * <p>
 * Two names must be given for each field, the SRG name and the DEOBF name.
 */
@SuppressWarnings( "unused" )
public
class ObfuscationHelper< T, E >
{
	private static
	Logger logger( ) { return DeadlyWorldMod.log( ); }
	
	private final Class< T > classToAccess;
	private final String[]   names;
	
	public
	ObfuscationHelper( Class< T > fieldClass, String srgName, String deobfName )
	{
		classToAccess = fieldClass;
		names = new String[] { srgName, deobfName };
	}
	
	public
	void set( T instance, E value )
	{
		try {
			ObfuscationReflectionHelper.setPrivateValue( classToAccess, instance, value, names );
		}
		catch( Exception ex ) {
			logger( ).error( "Failed to set private value ({}#{}={})", classToAccess.getSimpleName( ), names[ 1 ], value, ex );
		}
	}
	
	public
	E get( T instance )
	{
		try {
			return ObfuscationReflectionHelper.getPrivateValue( classToAccess, instance, names );
		}
		catch( Exception ex ) {
			logger( ).error( "Failed to get private value ({}#{}==?)", classToAccess.getSimpleName( ), names[ 1 ], ex );
		}
		return null;
	}
	
	public static
	class Static< T, E >
	{
		private final Class< T > classToAccess;
		private final String[]   names;
		
		public
		Static( Class< T > fieldClass, String srgName, String deobfName )
		{
			classToAccess = fieldClass;
			names = new String[] { srgName, deobfName };
		}
		
		public
		void set( E value )
		{
			try {
				ObfuscationReflectionHelper.setPrivateValue( classToAccess, null, value, names );
			}
			catch( Exception ex ) {
				logger( ).error( "Failed to set private static value ({}#{}={})", classToAccess.getSimpleName( ), names[ 1 ], value, ex );
			}
		}
		
		public
		E get( )
		{
			try {
				return ObfuscationReflectionHelper.getPrivateValue( classToAccess, null, names );
			}
			catch( Exception ex ) {
				logger( ).error( "Failed to get private static value ({}#{}==?)", classToAccess.getSimpleName( ), names[ 1 ], ex );
			}
			return null;
		}
	}
}
