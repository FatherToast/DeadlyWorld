package fathertoast.deadlyworld.item;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public
class ItemFeatureTester extends Item
{
	private static final String TAG_FEATURE_NAME = "DWFeatureName";
	
	public
	ItemFeatureTester( )
	{
		setMaxStackSize( 1 );
	}
	
	private
	String getFeatureName( ItemStack stack )
	{
		NBTTagCompound tag = stack.getTagCompound( );
		if( tag != null && tag.hasKey( TAG_FEATURE_NAME, TrapHelper.NBT_TYPE_STRING ) ) {
			return tag.getString( TAG_FEATURE_NAME );
		}
		return "";
	}
	
	private
	WorldGenDeadlyFeature getFeature( ItemStack stack )
	{
		String featureName = getFeatureName( stack );
		if( !featureName.isEmpty( ) ) {
			for( WorldGenDeadlyFeature feature : FeatureGenerator.FEATURE_LIST ) {
				if( feature.NAME.equals( featureName ) ) {
					return feature;
				}
			}
		}
		return null;
	}
	
	private
	ItemStack setFeature( ItemStack stack, WorldGenDeadlyFeature feature )
	{
		NBTTagCompound tag = stack.getTagCompound( );
		if( tag == null ) {
			stack.setTagCompound( tag = new NBTTagCompound( ) );
		}
		tag.setString( TAG_FEATURE_NAME, feature.NAME );
		return stack;
	}
	
	private
	ItemStack createFeatureTester( WorldGenDeadlyFeature feature )
	{
		return setFeature( new ItemStack( this ), feature );
	}
	
	@Override
	public
	void getSubItems( CreativeTabs tab, NonNullList< ItemStack > items )
	{
		if( isInCreativeTab( tab ) ) {
			for( WorldGenDeadlyFeature feature : FeatureGenerator.FEATURE_LIST ) {
				items.add( createFeatureTester( feature ) );
			}
		}
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public
	void addInformation( ItemStack stack, World world, List< String > tooltip, ITooltipFlag flag )
	{
		tooltip.add( TextFormatting.AQUA.toString( ) + "Feature: \"" + getFeatureName( stack ) + "\"" );
	}
	
	@Override
	public
	EnumActionResult onItemUse( EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ )
	{
		ItemStack stack = player.getHeldItem( hand );
		if( !world.isRemote && !stack.isEmpty( ) ) {
			WorldGenDeadlyFeature feature = getFeature( stack );
			if( feature != null ) {
				// Generate the feature
				String dimension = Config.getDimensionKey( world );
				Config dimConfig = Config.get( world );
				if( dimConfig == null ) {
					dimConfig = Config.get( );
					DeadlyWorldMod.log( ).warn(
						"[Player='{}',@{}] Feature tester is being used in a disabled dimension ({}); " +
						"generating feature:'{}' using config for dimension '{}'",
						player.getName( ), pos, dimension, feature.NAME, DimensionType.OVERWORLD.getName( )
					);
				}
				else {
					DeadlyWorldMod.log( ).info(
						"[Player='{}',@{}] Feature tester generating feature:'{}' using config for dimension '{}'",
						player.getName( ), pos, feature.NAME, dimension
					);
				}
				feature.placeFeature( dimConfig, dimConfig.TERRAIN.REPLACEABLE_BLOCKS, world, player.getRNG( ), pos );
			}
			else {
				return EnumActionResult.FAIL;
			}
		}
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public
	int getEntityLifespan( ItemStack itemStack, World world ) { return 0; }
}
