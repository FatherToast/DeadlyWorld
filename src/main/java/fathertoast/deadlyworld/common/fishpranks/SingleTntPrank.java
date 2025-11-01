package fathertoast.deadlyworld.common.fishpranks;

import fathertoast.deadlyworld.api.IFishingPrank;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWFishingPranks;
import fathertoast.deadlyworld.common.entity.YeetTnt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;

public class SingleTntPrank implements IFishingPrank {
    
    @Override
    public void prank( ServerLevel level, ServerPlayer player, Vec3 hookPos, Vec3 moveVec ) {
        boolean isYeet = this == DWFishingPranks.YEET_TNT.get();
        PrimedTnt tnt = isYeet ? new YeetTnt( level, hookPos.x(), hookPos.y(), hookPos.z(), null ) :
                new PrimedTnt( level, hookPos.x(), hookPos.y(), hookPos.z(), null );
        tnt.setFuse( isYeet ? Config.FISHING_PRANKS.YEET_TNT.fuse.get() : Config.FISHING_PRANKS.SINGLE_TNT.fuse.get() );
        tnt.setDeltaMovement( moveVec );
        
        level.addFreshEntity( tnt );
        
        if( tnt.isAddedToWorld() ) {
            level.playSound( null, hookPos.x(), hookPos.y(), hookPos.z(),
                    SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    }
}