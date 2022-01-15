package fathertoast.deadlyworld.block;

import com.mojang.realmsclient.gui.ChatFormatting;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings( "deprecation" )
public
class BlockDeadlySilverfish extends Block
{
	public static final String ID = "infested";
	
	private static Block                 builderDisguise;
	private static PropertyDisguiseBlock builderProperty;
	
	// Used to circumvent the anti-instance state container creation.
	public static
	BlockDeadlySilverfish buildFor( Block block )
	{
		builderDisguise = block;
		
		IBlockState state = block.getDefaultState( );
		if( state.getPropertyKeys( ).isEmpty( ) ) {
			builderProperty = null;
		}
		else {
			builderProperty = new PropertyDisguiseBlock( block );
		}
		
		BlockDeadlySilverfish infestedBlock = new BlockDeadlySilverfish( );
		
		builderDisguise = null;
		builderProperty = null;
		return infestedBlock;
	}
	
	public final Block                 DISGUISE;
	public final PropertyDisguiseBlock DISGUISE_PROPERTY;
	
	private
	BlockDeadlySilverfish( )
	{
		super( Material.CLAY, MapColor.STONE );
		DISGUISE = builderDisguise;
		DISGUISE_PROPERTY = builderProperty;
		
		IBlockState state = DISGUISE.getDefaultState( );
		if( DISGUISE_PROPERTY != null ) {
			setDefaultState( blockState.getBaseState( ).withProperty( DISGUISE_PROPERTY, DISGUISE.getMetaFromState( state ) ) );
		}
		
		setHardness( 0.0F );
		setSoundType( DISGUISE.getSoundType( ) );
		blockParticleGravity = DISGUISE.blockParticleGravity;
		slipperiness = DISGUISE.slipperiness;
		fullBlock = state.isOpaqueCube( );
		lightOpacity = DISGUISE.getLightOpacity( state );
		//translucent = DISGUISE.isTranslucent( state ); Client side method, also not important for this application
	}
	
	@Override
	protected
	BlockStateContainer createBlockState( )
	{
		if( builderProperty == null ) {
			return new BlockStateContainer( this );
		}
		builderProperty.parentBlock = this;
		return new BlockStateContainer( this, builderProperty );
	}
	
	public
	IBlockState toDisguise( IBlockState infestedState ) { return DISGUISE_PROPERTY == null ? DISGUISE.getDefaultState( ) : DISGUISE_PROPERTY.toDisguise( infestedState ); }
	
	public
	IBlockState fromDisguise( IBlockState disguiseState ) { return DISGUISE_PROPERTY == null ? getDefaultState( ) : DISGUISE_PROPERTY.fromDisguise( disguiseState ); }
	
	@Override
	public
	IBlockState getStateFromMeta( int meta ) { return DISGUISE_PROPERTY == null ? getDefaultState( ) : DISGUISE_PROPERTY.setMeta( meta ); }
	
	@Override
	public
	int getMetaFromState( IBlockState state ) { return DISGUISE_PROPERTY == null ? 0 : DISGUISE_PROPERTY.getMeta( state ); }
	
	@Override
	public
	void getSubBlocks( CreativeTabs tab, NonNullList< ItemStack > items )
	{
		if( DISGUISE_PROPERTY == null ) {
			items.add( new ItemStack( this, 1, 0 ) );
		}
		else {
			for( int meta : DISGUISE_PROPERTY.getAllowedValues( ) ) {
				items.add( new ItemStack( this, 1, meta ) );
			}
		}
	}
	
	@Override
	public
	void harvestBlock( World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tileEntity, ItemStack stack )
	{
		if( canSilkHarvest( world, pos, state, player ) && EnchantmentHelper.getEnchantmentLevel( Enchantments.SILK_TOUCH, stack ) > 0 ) {
			DISGUISE.harvestBlock( world, player, pos, toDisguise( state ), tileEntity, stack );
		}
		else {
			super.harvestBlock( world, player, pos, state, tileEntity, stack );
		}
	}
	
	@Override
	public
	void dropBlockAsItemWithChance( World world, BlockPos pos, IBlockState state, float chance, int fortune )
	{
		if( !world.isRemote && world.getGameRules( ).getBoolean( "doTileDrops" ) ) {
			// Spawn a silverfish
			EntitySilverfish silverfish = new EntitySilverfish( world );
			silverfish.setLocationAndAngles( pos.getX( ) + 0.5, pos.getY( ), pos.getZ( ) + 0.5, 0.0F, 0.0F );
			silverfish.onInitialSpawn( world.getDifficultyForLocation( pos ), null );
			world.spawnEntity( silverfish );
			silverfish.spawnExplosionParticle( );
			
			if( silverfish.getRNG( ).nextFloat( ) < Config.getOrDefault( world ).TERRAIN.SILVERFISH_AGGRO_CHANCE ) {
				// Immediately start calling for reinforcements if it can find a player
				if( silverfish.getAttackTarget( ) == null ) {
					double range;
					try {
						range = silverfish.getEntityAttribute( SharedMonsterAttributes.FOLLOW_RANGE ).getAttributeValue( );
					}
					catch( Exception ex ) {
						range = 16.0; // Default follow range
					}
					List< EntityPlayer > nearbyPlayers = world.getEntitiesWithinAABB(
						EntityPlayer.class, silverfish.getEntityBoundingBox( ).expand( range, range / 2.0, range )
					);
					for( EntityPlayer player : nearbyPlayers ) {
						if( player != null && silverfish.canEntityBeSeen( player ) ) {
							silverfish.setAttackTarget( player );
							break;
						}
					}
				}
				if( silverfish.getAttackTarget( ) != null ) {
					// Causes the silverfish to call for reinforcements
					silverfish.attackEntityFrom( DamageSource.MAGIC, 0.0F );
				}
			}
		}
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public
	void addInformation( ItemStack stack, @Nullable World player, List< String > tooltip, ITooltipFlag advanced )
	{
		tooltip.add( ChatFormatting.RED + new TextComponentTranslation( "tile.deadlyworld.deadly_silverfish.tooltip" ).getUnformattedComponentText( ) );
	}
	
	@Override
	public
	void onEntityCollidedWithBlock( World world, BlockPos pos, IBlockState state, Entity entity )
	{
		//TODO - Perhaps break the block?
		DISGUISE.onEntityCollidedWithBlock( world, pos, state, entity );
	}
	
	@Override
	public
	void onFallenUpon( World world, BlockPos pos, Entity entity, float fallDistance )
	{
		//TODO - Perhaps break the block?
		DISGUISE.onFallenUpon( world, pos, entity, fallDistance );
	}
	
	@Override
	public
	ItemStack getItem( World world, BlockPos pos, IBlockState state ) { return new ItemStack( this, 1, getMetaFromState( state ) ); }
	
	@Override
	public
	int quantityDropped( Random random ) { return 0; }
	
	@Override
	public
	MapColor getMapColor( IBlockState state, IBlockAccess world, BlockPos pos ) { return DISGUISE.getMapColor( toDisguise( state ), world, pos ); }
	
	@Override
	public
	IBlockState withRotation( IBlockState state, Rotation rotation ) { return fromDisguise( DISGUISE.withRotation( toDisguise( state ), rotation ) ); }
	
	@Override
	public
	IBlockState withMirror( IBlockState state, Mirror mirror ) { return fromDisguise( DISGUISE.withMirror( toDisguise( state ), mirror ) ); }
	
	@Override
	public
	boolean isReplaceable( IBlockAccess world, BlockPos pos ) { return false; }
	
	@Override
	public
	String getLocalizedName( ) { return DISGUISE.getLocalizedName( ); }
	
	@Override
	public
	String getUnlocalizedName( ) { return DISGUISE.getUnlocalizedName( ); }
	
	@Override
	public
	IBlockState getStateAtViewpoint( IBlockState state, IBlockAccess world, BlockPos pos, Vec3d viewpoint )
	{
		return fromDisguise( DISGUISE.getStateAtViewpoint( toDisguise( state ), world, pos, viewpoint ) );
	}
	
	@Override
	public
	IBlockState getStateForPlacement( World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand )
	{
		return fromDisguise( DISGUISE.getStateForPlacement( world, pos, facing, hitX, hitY, hitZ, meta, placer, hand ) );
	}
}
