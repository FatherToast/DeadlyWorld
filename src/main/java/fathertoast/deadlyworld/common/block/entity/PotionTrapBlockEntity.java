package fathertoast.deadlyworld.common.block.entity;

import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.PotionTrap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class PotionTrapBlockEntity extends FloorTrapBlockEntity {
    
    
    public PotionTrapBlockEntity( BlockPos pos, BlockState state ) {
        super( DWBlockEntities.POTION_TRAP.get(), pos, state );
    }
    
    @Nullable
    public MobEffectInstance getPotionCopy() {
        return ((PotionTrap) getTrapLogic()).getPotionCopy();
    }
    
    public boolean isDynamic() {
        return ((PotionTrap) getTrapLogic()).isDynamic();
    }
}
