package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.util.References;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class MiniChestMimic extends ChestMimic {
    
    public MiniChestMimic( EntityType<? extends Monster> entityType, Level level ) {
        super( entityType, level );
        // Is smol, can't take big steps like its older sibling
        setMaxUpStep( 0.5F );
    }
    
    @Override
    public float getVoicePitch() { return super.getVoicePitch() + References.MINI_PITCH_SHIFT; }
}