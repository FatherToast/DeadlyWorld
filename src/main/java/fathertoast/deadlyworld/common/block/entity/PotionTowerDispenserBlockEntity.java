package fathertoast.deadlyworld.common.block.entity;

import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.PotionTower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class PotionTowerDispenserBlockEntity extends TowerDispenserBlockEntity {

    public PotionTowerDispenserBlockEntity( BlockPos pos, BlockState state ) {
        super( DWBlockEntities.POTION_TOWER.get(), pos, state );
    }

    @Nullable
    public MobEffectInstance getPotionCopy() {
        return ((PotionTower) getTowerLogic()).getPotionCopy();
    }

    public boolean isDynamic() {
        return ((PotionTower) getTowerLogic()).isDynamic();
    }
}
