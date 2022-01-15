package fathertoast.deadlyworld.item;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public
class ItemDeadlyEvent extends Item
{
	public static final String ID = "event_item";
	
	private static final String TAG_EVENT_NAME = "DWEvent";
	
	public
	ItemDeadlyEvent( )
	{
		setMaxStackSize( 1 );
	}
	
	void trigger( ItemStack stack, World world, Vec3d pos, EntityPlayer player )
	{
		Config dimConfig = Config.getOrDefault( world );
		getEvent( stack ).trigger( dimConfig, world, pos, player );
	}
	
	private
	String getEventNameId( ItemStack stack )
	{
		NBTTagCompound tag = stack.getTagCompound( );
		if( tag != null && tag.hasKey( TAG_EVENT_NAME, TrapHelper.NBT_TYPE_STRING ) ) {
			return tag.getString( TAG_EVENT_NAME );
		}
		return "";
	}
	
	private
	EnumDeadlyEventType getEvent( ItemStack stack )
	{
		String eventName = getEventNameId( stack );
		if( !eventName.isEmpty( ) ) {
			for( EnumDeadlyEventType eventType : EnumDeadlyEventType.values() ) {
				if( eventType.NAME.equals( eventName ) ) {
					return eventType;
				}
			}
		}
		return EnumDeadlyEventType.NONE;
	}
	
	private
	ItemStack setEvent( ItemStack stack, EnumDeadlyEventType eventType )
	{
		NBTTagCompound tag = stack.getTagCompound( );
		if( tag == null ) {
			stack.setTagCompound( tag = new NBTTagCompound( ) );
		}
		tag.setString( TAG_EVENT_NAME, eventType.NAME );
		return stack;
	}
	
	public
	ItemStack createEventItem( EnumDeadlyEventType eventType )
	{
		return setEvent( new ItemStack( this ), eventType );
	}
	
	@Override
	public
	void getSubItems( CreativeTabs tab, NonNullList< ItemStack > items )
	{
		if( isInCreativeTab( tab ) ) {
			for( EnumDeadlyEventType eventType : EnumDeadlyEventType.values( ) ) {
				if( eventType != EnumDeadlyEventType.NONE ) {
					items.add( createEventItem( eventType ) );
				}
			}
		}
	}
	
	private static final String LANG_KEY = "tile." + DeadlyWorldMod.LANG_KEY + ID + ".tooltip";
	
	@Override
	@SideOnly( Side.CLIENT )
	public
	void addInformation( ItemStack stack, World world, List< String > tooltip, ITooltipFlag flag )
	{
		tooltip.add(
			TextFormatting.AQUA.toString( ) + I18n.translateToLocal( LANG_KEY ) +
			" \"" + I18n.translateToLocal( LANG_KEY + "." + getEvent( stack ).NAME ) + "\""
		);
	}
	
	@Override
	public
	int getEntityLifespan( ItemStack itemStack, World world ) { return 0; }
}
