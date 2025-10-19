package fathertoast.deadlyworld.common.item;

import fathertoast.deadlyworld.common.config.dimension.ChestConfig;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public enum InfestedEventType implements IEventType {
    
    SPIDERS( "spiders", 10, "spawn mini spiders" ) {
        /** Triggers this event. */
        @Override
        public void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state,
                                  Direction blockFacing, @Nullable Player player, ItemStack item ) {
            spawnInfestation( level, pos, blockFacing, player, DWEntities.MINI_SPIDER.get(),
                    dimConfigs.CHESTS.INFESTED.spiderCount.get(), dimConfigs.CHESTS.INFESTED.spiderSpeed.getFloat() );
            level.playSound( null, pos, SoundEvents.SPIDER_HURT,
                    SoundSource.HOSTILE, 1.0F, 2.0F );
        }
    },
    
    SILVERFISH( "silverfish", 10, "spawn silverfish" ) {
        /** Triggers this event. */
        @Override
        public void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state,
                                  Direction blockFacing, @Nullable Player player, ItemStack item ) {
            spawnInfestation( level, pos, blockFacing, player, EntityType.SILVERFISH,
                    dimConfigs.CHESTS.INFESTED.silverfishCount.get(), dimConfigs.CHESTS.INFESTED.silverfishSpeed.getFloat() );
            level.playSound( null, pos, SoundEvents.SILVERFISH_HURT,
                    SoundSource.HOSTILE, 1.0F, 1.0F );
        }
    };
    
    private static void spawnInfestation( ServerLevel level, BlockPos pos, Direction blockFacing, @Nullable Player player,
                                          EntityType<? extends Mob> entityType, int count, float launchSpeed ) {
        for( int i = 0; i < count; i++ ) {
            Mob mob = entityType.create( level );
            if( mob == null ) continue;
            
            float angle = setPosAndRot( level, pos, blockFacing, mob );
            
            level.addFreshEntity( mob );
            if( !mob.isAddedToWorld() ) continue;
            
            float speed = launchSpeed * level.random.nextFloat() + 0.02F;
            if( player != null ) mob.setTarget( player );
            
            ForgeEventFactory.onFinalizeSpawn( mob, level, level.getCurrentDifficultyAt( pos ),
                    MobSpawnType.TRIGGERED, null, null );
            
            mob.setDeltaMovement(
                    Mth.sin( angle ) * speed,
                    0.2 + 0.2 * level.random.nextFloat(),
                    Mth.cos( angle ) * speed
            );
        }
    }
    
    private static float setPosAndRot( ServerLevel level, BlockPos pos, Direction blockFacing, LivingEntity entity ) {
        entity.setPos( pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5 );
        float angleDeg = blockFacing.getAxis() == Direction.Axis.Y ? 360.0F * level.random.nextFloat() :
                blockFacing.toYRot() + 90.0F * (level.random.nextFloat() - 0.5F);
        entity.setYBodyRot( angleDeg );
        entity.setYHeadRot( angleDeg );
        return (float) Math.toRadians( angleDeg );
    }
    
    
    public final String id;
    public final int defaultWeight;
    public final String description;
    
    InfestedEventType( String id, int defaultWeight, String description ) {
        this.id = id;
        this.defaultWeight = defaultWeight;
        this.description = description;
    }
    
    @Override
    public String getId() { return id; }
    
    @Override
    public int getDefaultWeight() { return defaultWeight; }
    
    @Override
    public String getDescription() { return description; }
    
    @Override
    public int getIndex() { return ordinal(); }
    
    @Override
    public ChestConfig.EventChestTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) {
        return dimConfigs.CHESTS.INFESTED;
    }
}