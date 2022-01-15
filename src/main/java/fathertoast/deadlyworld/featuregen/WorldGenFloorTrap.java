package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public
class WorldGenFloorTrap extends WorldGenFloorFeature
{
	private final EnumFloorTrapType TYPE;
	private final IBlockState       BLOCK_STATE;
	
	public
	WorldGenFloorTrap( EnumFloorTrapType trapType )
	{
		super( "floor_trap." + trapType.NAME );
		TYPE = trapType;
		BLOCK_STATE = ModObjects.FLOOR_TRAP.getDefaultState( ).withProperty( EnumFloorTrapType.PROPERTY, trapType );
	}
	
	@Override
	public
	Config.FeatureFloorTrap getFeatureConfig( Config dimConfig ) { return TYPE.getFeatureConfig( dimConfig ); }
	
	@Override
	public
	BlockPos placeFeature( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		BlockPos trapPos = TrapHelper.isAnySideOpen( world, position ) ? position.add( 0, -1, 0 ) : position;
		placeTrap( trapPos, dimConfig, world, random );
		return trapPos;
	}
	
	@Override
	public
	boolean canBePlaced( Config dimConfig, World world, BlockPos position )
	{
		BlockPos trapPos;
		if( TrapHelper.isAnySideOpen( world, position ) ) {
			trapPos = position.add( 0, -1, 0 );
			if( TrapHelper.isAnySideOpen( world, trapPos ) ) {
				return false;
			}
		}
		else {
			trapPos = position;
		}
		return TYPE.canTypeBePlaced( world, trapPos );
	}
	
	private
	void placeTrap( BlockPos pos, Config dimConfig, World world, Random random )
	{
		BlockPos coverPos = pos.add( 0, 1, 0 );
		if( dimConfig.TERRAIN.FLOOR_TRAP_COVERS.TOTAL_WEIGHT > 0 && random.nextFloat( ) < TYPE.getFeatureConfig( dimConfig ).COVER_CHANCE ) {
			// Cover the trap with a random cover block
			setBlock( dimConfig, world, random, coverPos, dimConfig.TERRAIN.FLOOR_TRAP_COVERS.nextBlock( random ) );
		}
		else if( !world.isAirBlock( coverPos ) ) {
			// Otherwise, make sure the block above is clear
			setBlock( dimConfig, world, random, coverPos, Blocks.AIR.getDefaultState( ) );
		}
		
		setBlock( dimConfig, world, random, pos, BLOCK_STATE );
		ModObjects.FLOOR_TRAP.initTileEntity( world, pos, BLOCK_STATE, dimConfig, random );
	}
}
