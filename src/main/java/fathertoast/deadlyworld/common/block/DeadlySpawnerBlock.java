package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.block.properties.SpawnerType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.tile.DeadlySpawnerTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DeadlySpawnerBlock extends ContainerBlock {
    
    public static final EnumProperty<SpawnerType> SPAWNER_TYPE = EnumProperty.create( "spawner_type", SpawnerType.class );
    
    public DeadlySpawnerBlock() {
        super( AbstractBlock.Properties.copy( Blocks.SPAWNER ) );
        this.registerDefaultState( stateDefinition.any().setValue( SPAWNER_TYPE, SpawnerType.LONE ) );
        
        // TODO - Will the config be loaded before the Forge registries are?
        //      - They can be; needed options here can be loaded during FMLConstructModEvent or in the mod's constructor
        /*
        super( Material.IRON, MapColor.STONE );
		setSoundType( SoundType.METAL );

		setHardness( Config.get( ).GENERAL.SPAWNER_HARDNESS );
		setResistance( Config.get( ).GENERAL.SPAWNER_RESISTANCE );

		setDefaultState( blockState.getBaseState( ).withProperty( EnumSpawnerType.PROPERTY, EnumSpawnerType.LONE ) );
         */
    }
    
    @Nullable
    @Override
    public TileEntity newBlockEntity( IBlockReader world ) { return new DeadlySpawnerTileEntity(); }
    
    
    public void initTileEntity( World world, BlockPos pos, BlockState state, DimensionConfigGroup dimConfigs ) {
        TileEntity spawner = world.getBlockEntity( pos );
        
        if( spawner instanceof DeadlySpawnerTileEntity ) {
            ((DeadlySpawnerTileEntity) spawner).initializeSpawner( state.getValue( SPAWNER_TYPE ), dimConfigs );
        }
        else {
            DeadlyWorld.LOG.error( "Failed to fetch mob spawner tile entity at [{}]!", pos );
        }
    }

    /*
    @Override
    protected
    BlockStateContainer createBlockState( ) { return new BlockStateContainer( this, EnumSpawnerType.PROPERTY ); }
     */
    
    // TODO - Metadata is no more! Gotta find another way
    /*
    @Override
    public
    void getSubBlocks( CreativeTabs tab, NonNullList< ItemStack > items )
    {
        for( EnumSpawnerType type : EnumSpawnerType.values( ) ) {
            items.add( new ItemStack( this, 1, type.getMetadata( ) ) );
        }
    }
     */
    
    @Override
    public int getExpDrop( BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch ) {
        return silktouch > 0 ? 0 : (15 + RANDOM.nextInt( 15 ) + RANDOM.nextInt( 15 ));
    }
    
    @Override
    public void setPlacedBy( World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack ) {
        super.setPlacedBy( world, pos, state, placer, itemStack );
        
        if( !world.isClientSide ) {
            initTileEntity( world, pos, state, null/*Config.getOrDefault( world )*/ );
        }
    }
    
    @Override
    public BlockRenderType getRenderShape( BlockState state ) { return BlockRenderType.MODEL; }
    
    @Override
    protected void createBlockStateDefinition( StateContainer.Builder<Block, BlockState> stateBuilder ) {
        stateBuilder.add( SPAWNER_TYPE );
    }
}