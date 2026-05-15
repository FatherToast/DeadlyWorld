package fathertoast.deadlyworld.common.fishpranks;

import fathertoast.deadlyworld.api.IFishingPrank;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;

import static fathertoast.deadlyworld.common.config.Config.FISHING_PRANKS;

public class MobPrank implements IFishingPrank {
    
    @Override
    public void prank( ServerLevel level, ServerPlayer player, Vec3 hookPos, Vec3 moveVec ) {
        EntityType<?> type = FISHING_PRANKS.MOB.mobList.get().next( level.random );
        
        if( type == null ) return;
        
        Entity entity = type.create( level );
        
        if( entity == null ) {
            DeadlyWorld.LOG.warn( "Failed to spawn entity for mob fishing prank! Problematic entity type: {}",
                    ForgeRegistries.ENTITY_TYPES.getKey( type ) );
            return;
        }
        entity.setPos( hookPos );
        entity.setDeltaMovement( moveVec );
        
        if( entity instanceof Mob mob ) {
            ForgeEventFactory.onFinalizeSpawn( mob, level, level.getCurrentDifficultyAt( player.blockPosition() ),
                    MobSpawnType.TRIGGERED, null, null );
        }
        level.addFreshEntity( entity );
    }
    
    @Override
    public boolean canUse( ServerLevel level, ServerPlayer player, Vec3 hookPos ) {
        if( level.getDifficulty() == Difficulty.PEACEFUL ) return false;
        
        // The body of water must be at least 2 blocks deep
        BlockPos pos = new BlockPos( (int) hookPos.x(), (int) hookPos.y(), (int) hookPos.z() );
        
        return level.getFluidState( pos.below() ).is( FluidTags.WATER )
                && level.getFluidState( pos.below( 2 ) ).is( FluidTags.WATER );
    }
}
