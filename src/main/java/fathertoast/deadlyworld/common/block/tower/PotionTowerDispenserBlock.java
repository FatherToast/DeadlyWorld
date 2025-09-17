package fathertoast.deadlyworld.common.block.tower;

import fathertoast.deadlyworld.common.block.entity.PotionTowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.block.entity.TowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTower;
import fathertoast.deadlyworld.common.world.logic.PotionTower;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PotionTowerDispenserBlock extends TowerDispenserBlock {

    public PotionTowerDispenserBlock() {
        super( TowerType.POTION );
    }

    @Override
    public BaseTower newTowerLogic(TowerDispenserBlockEntity blockEntity ) {
        return new PotionTower( blockEntity ) {
            @Override
            public void activateTower( ServerLevel level, BlockPos pos, Entity target,
                                      Vec3 center, Vec3 offset, Vec3 vecToTarget, double distance ) {
                towerType.triggerAttack( Config.getDimensionConfigs( level ), blockEntity, target, center, offset, vecToTarget, distance );
            }
        };
    }

    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) { return new PotionTowerDispenserBlockEntity( pos, state ); }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type ) {
        return getTicker( level, type, DWBlockEntities.POTION_TOWER.get() );
    }
}
