package fathertoast.deadlyworld.item;

import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

@SuppressWarnings( "WeakerAccess" )
public
class ItemBlockDeadlySilverfish extends ItemBlock
{
	public final BlockDeadlySilverfish INFESTED_BLOCK;
	public final Item                  DISGUISE;
	
	public
	ItemBlockDeadlySilverfish( BlockDeadlySilverfish infestedBlock )
	{
		super( infestedBlock );
		INFESTED_BLOCK = infestedBlock;
		DISGUISE = Item.getItemFromBlock( infestedBlock.DISGUISE );
		
		setMaxDamage( 0 );
		setHasSubtypes( infestedBlock.DISGUISE_PROPERTY != null );
	}
	
	// These copy-to methods are fairly costly in comparison to the block state conversions; use sparingly.
	public
	ItemStack copyToDisguise( ItemStack infestedStack )
	{
		NBTTagCompound tag = infestedStack.serializeNBT( );
		// Change the item to the disguise item
		tag.setString( "id", DISGUISE.getRegistryName( ).toString( ) );
		return new ItemStack( tag );
	}
	
	// These copy-to methods are fairly costly in comparison to the block state conversions; use sparingly.
	public
	ItemStack copyFromDisguise( ItemStack disguiseStack )
	{
		NBTTagCompound tag = disguiseStack.serializeNBT( );
		// Change the item to this item
		tag.setString( "id", getRegistryName( ).toString( ) );
		return new ItemStack( tag );
	}
	
	@Override
	public
	int getMetadata( int meta ) { return meta; }
	
	@Override
	public
	String getUnlocalizedName( ItemStack stack )
	{
		try {
			return DISGUISE.getUnlocalizedName( stack );
		}
		catch( Exception ex ) {
			// In case they don't like the stack having the infested item
			return DISGUISE.getUnlocalizedName( copyToDisguise( stack ) );
		}
	}
	
	@Override
	@Nullable
	public
	String getCreatorModId( ItemStack stack )
	{
		if( Config.get( ).GENERAL.SILVERFISH_DISGUISE_MOD ) {
			try {
				return DISGUISE.getCreatorModId( stack );
			}
			catch( Exception ex ) {
				// In case they don't like the stack having the infested item
				return DISGUISE.getCreatorModId( copyToDisguise( stack ) );
			}
		}
		return super.getCreatorModId( stack );
	}
	
	@Override
	public
	void getSubItems( CreativeTabs tab, NonNullList< ItemStack > items )
	{
		if( isInCreativeTab( tab ) ) {
			NonNullList< ItemStack > capturedItems = NonNullList.create( );
			DISGUISE.getSubItems( CreativeTabs.SEARCH, capturedItems );
			
			if( capturedItems.isEmpty( ) ) {
				// No captured items; add all valid states
				INFESTED_BLOCK.getSubBlocks( tab, items );
			}
			else {
				// Items captured; add copies of the items that would have been added
				for( ItemStack disguiseStack : capturedItems ) {
					items.add( copyFromDisguise( disguiseStack ) );
				}
			}
		}
	}
}
