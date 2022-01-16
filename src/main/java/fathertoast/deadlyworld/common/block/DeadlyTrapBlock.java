package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.tile.spawner.FloorTrapTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DeadlyTrapBlock extends ContainerBlock {
    
    public DeadlyTrapBlock() {
        super( AbstractBlock.Properties.copy( Blocks.SPAWNER ) );
        // TODO - use config to define properties
    }
    
    @Nullable
    @Override
    public TileEntity newBlockEntity( IBlockReader world ) {
        return new FloorTrapTileEntity();
    }
    
    public void initTileEntity( World world, BlockPos pos, DimensionConfigGroup dimConfigs ) {
        TileEntity spawner = world.getBlockEntity( pos );
        
        if( spawner instanceof FloorTrapTileEntity ) {
            //((DeadlySpawnerTileEntity) spawner).initializeSpawner( this.spawnerType, dimConfigs );
        }
        else {
            DeadlyWorld.LOG.error( "Failed to fetch mob spawner tile entity at [{}]!", pos );
        }
    }
    
    @Override
    public int getExpDrop( BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch ) {
        return silktouch > 0 ? 0 : (15 + RANDOM.nextInt( 15 ) + RANDOM.nextInt( 15 ));
    }
    
    @Override
    public void setPlacedBy( World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack ) {
        super.setPlacedBy( world, pos, state, placer, itemStack );
        
        if( !world.isClientSide ) {
            initTileEntity( world, pos, Config.getDimensionConfigs( world ) );
        }
    }
    
    @Override
    public BlockRenderType getRenderShape( BlockState state ) { return BlockRenderType.MODEL; }
}