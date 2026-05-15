package fathertoast.deadlyworld.common.item;

import fathertoast.deadlyworld.common.config.dimension.ChestConfig;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public enum SurpriseEventType implements IEventType {
    
    TNT( "tnt", 10, "spawn primed TNT" ) {
        /** Triggers this event. */
        @Override
        public void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state,
                                  Direction blockFacing, @Nullable Player player, ItemStack item ) {
            for( int i = 0; i < dimConfigs.CHESTS.SURPRISE.tntCount.get(); i++ ) {
                PrimedTnt tnt = new PrimedTnt( level,
                        pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, player );
                
                float speed = dimConfigs.CHESTS.SURPRISE.tntSpeed.getFloat() * level.random.nextFloat() + 0.02F;
                tnt.setFuse( dimConfigs.CHESTS.SURPRISE.tntFuseTime.next( level.random ) );
                
                float angle = (float) Math.toRadians( blockFacing.getAxis() == Direction.Axis.Y ? 360.0F * level.random.nextFloat() :
                        blockFacing.toYRot() + 90.0F * (level.random.nextFloat() - 0.5F) );
                
                level.addFreshEntity( tnt );
                if( !tnt.isAddedToWorld() ) continue;
                
                tnt.setDeltaMovement(
                        Mth.sin( angle ) * speed,
                        0.1 + 0.1 * level.random.nextFloat(),
                        Mth.cos( angle ) * speed
                );
            }
            level.playSound( null, pos, SoundEvents.TNT_PRIMED,
                    SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    },
    
    LAVA( "lava", 5, "overflow with lava" ) {
        /** Triggers this event. */
        @Override
        public void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state,
                                  Direction blockFacing, @Nullable Player player, ItemStack item ) {
            // Decide on position for lava
            final BlockPos.MutableBlockPos lavaPos = pos.mutable();
            if( level.getBlockState( lavaPos ).isSolid() ) lavaPos.move( Direction.UP );
            
            // Try to place the lava
            if( level.getBlockState( lavaPos ).isSolid() ) {
                // Failure, just play a sound
                level.playSound( null, pos, SoundEvents.LAVA_EXTINGUISH,
                        SoundSource.BLOCKS, 1.0F, 1.0F );
            }
            else {
                // Place lava block
                level.setBlock( lavaPos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL );
                level.playSound( null, pos, SoundEvents.BUCKET_EMPTY_LAVA,
                        SoundSource.BLOCKS, 1.0F, 1.0F );
            }
        }
    },
    
    RUNNY_LAVA( "runny_lava", 2, "overflow with runny lava" ) {
        /** Triggers this event. */
        @Override
        public void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state,
                                  Direction blockFacing, @Nullable Player player, ItemStack item ) {
            // Decide on position for lava
            final BlockPos.MutableBlockPos lavaPos = pos.mutable();
            if( level.getBlockState( lavaPos ).isSolid() ) lavaPos.move( Direction.UP );
            
            // Try to place the lava
            if( level.getBlockState( lavaPos ).isSolid() ) {
                // Failure, just play a sound
                level.playSound( null, pos, SoundEvents.LAVA_EXTINGUISH,
                        SoundSource.BLOCKS, 1.0F, 1.0F );
            }
            else {
                // Place lava block
                level.setBlock( lavaPos, DWBlocks.RUNNY_LAVA.get().defaultBlockState(), Block.UPDATE_ALL );
                level.playSound( null, pos, SoundEvents.BUCKET_EMPTY_LAVA,
                        SoundSource.BLOCKS, 1.0F, 1.0F );
            }
        }
    },
    
    POISON( "poison_gas", 5, "release an expanding poison cloud" ) {
        /** Triggers this event. */
        @Override
        public void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state,
                                  Direction blockFacing, @Nullable Player player, ItemStack item ) {
            AreaEffectCloud cloud = new AreaEffectCloud( level,
                    pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5 );
            cloud.addEffect( new MobEffectInstance( MobEffects.POISON,
                    dimConfigs.CHESTS.SURPRISE.poisonGasEffectDuration.get(),
                    dimConfigs.CHESTS.SURPRISE.poisonGasEffectPotency.get() ) );
            cloud.setOwner( player );
            
            cloud.setWaitTime( dimConfigs.CHESTS.SURPRISE.poisonGasDelay.get() );
            cloud.setDuration( dimConfigs.CHESTS.SURPRISE.poisonGasDuration.get() );
            cloud.setDurationOnUse( 0 );
            
            final float minRadius = 0.5F;
            cloud.setRadius( minRadius );
            cloud.setRadiusPerTick( (dimConfigs.CHESTS.SURPRISE.poisonGasMaxRadius.getFloat() - minRadius) / (float) cloud.getDuration() );
            cloud.setRadiusOnUse( 0.0F );
            
            level.addFreshEntity( cloud );
            level.playSound( null, pos, SoundEvents.SPLASH_POTION_BREAK,
                    SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    },
    
    WITHER( "wither_gas", 1, "release an expanding wither cloud" ) {
        /** Triggers this event. */
        @Override
        public void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state,
                                  Direction blockFacing, @Nullable Player player, ItemStack item ) {
            AreaEffectCloud cloud = new AreaEffectCloud( level,
                    pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5 );
            cloud.addEffect( new MobEffectInstance( MobEffects.WITHER,
                    dimConfigs.CHESTS.SURPRISE.witherGasEffectDuration.get(),
                    dimConfigs.CHESTS.SURPRISE.witherGasEffectPotency.get() ) );
            cloud.setOwner( player );
            
            cloud.setWaitTime( dimConfigs.CHESTS.SURPRISE.witherGasDelay.get() );
            cloud.setDuration( dimConfigs.CHESTS.SURPRISE.witherGasDuration.get() );
            cloud.setDurationOnUse( 0 );
            
            final float minRadius = 0.5F;
            cloud.setRadius( minRadius );
            cloud.setRadiusPerTick( (dimConfigs.CHESTS.SURPRISE.witherGasMaxRadius.getFloat() - minRadius) / (float) cloud.getDuration() );
            cloud.setRadiusOnUse( 0.0F );
            
            level.addFreshEntity( cloud );
            level.playSound( null, pos, SoundEvents.SPLASH_POTION_BREAK,
                    SoundSource.BLOCKS, 1.0F, 1.0F );
            level.playSound( null, pos, SoundEvents.WITHER_SHOOT,
                    SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    };
    
    public final String id;
    public final int defaultWeight;
    public final String description;
    
    SurpriseEventType( String id, int defaultWeight, String description ) {
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
        return dimConfigs.CHESTS.SURPRISE;
    }
}