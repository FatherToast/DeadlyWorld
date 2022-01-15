package fathertoast.deadlyworld.block.state;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;

import java.util.Collection;
import java.util.Set;

@SuppressWarnings( "WeakerAccess" )
public
class PropertyDisguiseBlock extends PropertyHelper< Integer >
{
	private final Block                   disguiseBlock;
	private final ImmutableSet< Integer > allowedValues;
	
	public Block parentBlock;
	
	public
	PropertyDisguiseBlock( Block disguise )
	{
		super( "disguise", Integer.class );
		if( disguise.getDefaultState( ).getPropertyKeys( ).isEmpty( ) ) {
			throw new IllegalArgumentException( "Disguise property should not be used for blocks without properties" );
		}
		disguiseBlock = disguise;
		
		Set< Integer > set = Sets.newHashSet( );
		for( IBlockState state : disguise.getBlockState( ).getValidStates( ) ) {
			set.add( disguise.getMetaFromState( state ) );
		}
		allowedValues = ImmutableSet.copyOf( set );
	}
	
	public
	int getMeta( IBlockState state ) { return state.getValue( this ); }
	
	public
	IBlockState setMeta( int meta ) { return parentBlock.getDefaultState( ).withProperty( this, meta ); }
	
	@SuppressWarnings( "deprecation" )
	public
	IBlockState toDisguise( IBlockState infestedState ) { return disguiseBlock.getStateFromMeta( getMeta( infestedState ) ); }
	
	public
	IBlockState fromDisguise( IBlockState disguiseState ) { return setMeta( disguiseBlock.getMetaFromState( disguiseState ) ); }
	
	@Override
	public
	Collection< Integer > getAllowedValues( ) { return allowedValues; }
	
	@Override
	public
	String getName( Integer value ) { return value.toString( ); }
	
	@Override
	public
	Optional< Integer > parseValue( String value )
	{
		try {
			Integer integer = Integer.valueOf( value );
			return allowedValues.contains( integer ) ? Optional.of( integer ) : Optional.absent( );
		}
		catch( NumberFormatException ex ) {
			return Optional.absent( );
		}
	}
	
	@Override
	public
	int hashCode( ) { return 31 * super.hashCode( ) + disguiseBlock.hashCode( ); }
	
	@Override
	public
	boolean equals( Object other )
	{
		if( this == other ) {
			return true;
		}
		if( other instanceof PropertyDisguiseBlock && super.equals( other ) ) {
			return disguiseBlock.equals( ((PropertyDisguiseBlock) other).disguiseBlock );
		}
		return false;
	}
}
