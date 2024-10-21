package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.config.BlocksConfig;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class DeadlyTrapBlock extends BaseEntityBlock {
    
    public DeadlyTrapBlock( BlocksConfig.BlockCategory config ) {
        super( config.adjustBlockProperties( Properties.copy( Blocks.SPAWNER ) ) );
    }
    
    public BaseTrap newTrapLogic( DeadlyTrapBlockEntity blockEntity ) {
        return new BaseTrap( blockEntity ) {
            @Override
            public void triggerTrap( ServerLevel level, BlockPos pos ) {
                // TODO
            }
        };
    }
    
    public void initializeTrap( Level level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof DeadlyTrapBlockEntity trapBlockEntity ) {
            trapBlockEntity.getTrapLogic().initializeTrap( level, pos, random );
        }
    }
    
    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) { return new DeadlyTrapBlockEntity( pos, state ); }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return getTicker( level, type, DWBlockEntities.DEADLY_TRAP.get() );
    }
    
    @Nullable
    public <T extends BlockEntity, V extends DeadlyTrapBlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockEntityType<T> type, BlockEntityType<V> expectedType ) {
        return createTickerHelper( type, expectedType,
                level.isClientSide ? DeadlyTrapBlockEntity::clientTick : DeadlyTrapBlockEntity::serverTick );
    }
    
    @Override
    public int getExpDrop( BlockState state, LevelReader level, RandomSource random, BlockPos pos, int fortune, int silkTouch ) {
        return 15 + random.nextInt( 15 ) + random.nextInt( 15 );
    }
    
    @Override
    public RenderShape getRenderShape( BlockState state ) { return RenderShape.MODEL; }
    
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag mode ) {
        super.appendHoverText( itemStack, level, tooltip, mode );
        
        Optional<Component> entityDisplayName = getSpawnEntityDisplayName( itemStack );
        if( entityDisplayName.isPresent() ) {
            tooltip.add( entityDisplayName.get() );
        }
        else {
            tooltip.add( CommonComponents.EMPTY );
            tooltip.add( Component.translatable( "block.minecraft.spawner.desc1" ).withStyle( ChatFormatting.GRAY ) );
            tooltip.add( CommonComponents.space().append( Component.translatable( "block.minecraft.spawner.desc2" ).withStyle( ChatFormatting.BLUE ) ) );
        }
    }
    
    private Optional<Component> getSpawnEntityDisplayName( ItemStack itemStack ) {
        CompoundTag tag = BlockItem.getBlockEntityData( itemStack );
        if( tag != null && NBTHelper.containsCompound( tag, BaseSpawner.SPAWN_DATA_TAG ) ) {
            ResourceLocation entityId = ResourceLocation.tryParse( tag.getCompound( BaseSpawner.SPAWN_DATA_TAG )
                    .getCompound( SpawnData.ENTITY_TAG ).getString( Entity.ID_TAG ) );
            if( entityId != null ) {
                return ForgeRegistries.ENTITY_TYPES.getDelegate( entityId ).map( ( entityType ) ->
                        Component.translatable( entityType.get().getDescriptionId() ).withStyle( ChatFormatting.GRAY ) );
            }
        }
        return Optional.empty();
    }
}