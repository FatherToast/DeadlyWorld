package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.tile.spawner.DeadlySpawnerTileEntity;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeadlySpawnerBlock extends ContainerBlock {
    
    private final SpawnerType spawnerType;
    
    public DeadlySpawnerBlock( @Nonnull SpawnerType type ) {
        super( Config.BLOCKS.get( SpawnerType.CATEGORY, type.toString() ).adjustBlockProperties( AbstractBlock.Properties.copy( Blocks.SPAWNER ) )
                .requiresCorrectToolForDrops()
                .strength( 5.0F )
        );
        spawnerType = type;

        // TODO - Yes, this is no longer needed. Each spawner comes with its own block that stores the spawner type.
        /* These are all separate blocks now, right?  Is this needed?
		setDefaultState( blockState.getBaseState( ).withProperty( EnumSpawnerType.PROPERTY, EnumSpawnerType.LONE ) );
         */
    }
    
    @Nullable
    @Override
    public TileEntity newBlockEntity( IBlockReader world ) { return new DeadlySpawnerTileEntity(); }
    
    public SpawnerType getSpawnerType() { return spawnerType; }
    
    public static void initTileEntity( ISeedReader world, SpawnerType spawnerType, BlockPos pos, DimensionConfigGroup dimConfigs ) {
        if ( !world.isAreaLoaded( pos, 1 )) {
            DeadlyWorld.LOG.error( "Tried to initialize spawner tile entity in an unloaded chunk: \"{}\"", pos.toString() );
            return;
        }
        TileEntity spawner = world.getBlockEntity( pos );
        
        if( spawner instanceof DeadlySpawnerTileEntity ) {
            ((DeadlySpawnerTileEntity) spawner).initializeSpawner( spawnerType, dimConfigs );
        }
        else {
            DeadlyWorld.LOG.error( "Failed to fetch mob spawner tile entity at [{}]!", pos );
        }
    }
    
    @Override
    public int getExpDrop( BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silkTouch ) {
        return silkTouch > 0 ? 0 : (15 + RANDOM.nextInt( 15 ) + RANDOM.nextInt( 15 ));
    }
    
    @Override
    public void setPlacedBy( World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack ) {
        super.setPlacedBy( world, pos, state, placer, itemStack );
        
        if( !world.isClientSide ) {
            ServerWorld serverWorld = (ServerWorld) world;
            initTileEntity( serverWorld, this.spawnerType, pos, Config.getDimensionConfigs( world ) );
        }
    }
    
    @Override
    public BlockRenderType getRenderShape( BlockState state ) { return BlockRenderType.MODEL; }
}