package fathertoast.deadlyworld.common.block.tower;

import fathertoast.deadlyworld.common.block.entity.PotionTowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.block.entity.TowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.TowerDispenserConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public enum TowerType {
    
    SIMPLE( "simple", ( dimConfig ) -> dimConfig.TOWER_DISPENSERS.SIMPLE ) {
        @Override
        public void triggerAttack(DimensionConfigGroup dimConfig, TowerDispenserBlockEntity towerDispenser, Entity target,
                                  Vec3 center, Vec3 offset, Vec3 vecToTarget, double distanceH ) {
            BlockPos pos = towerDispenser.getBlockPos();
            AbstractArrow arrow = new Arrow( towerDispenser.getLevel(), pos.getX(), pos.getY(), pos.getZ() );
            towerDispenser.getTowerLogic().shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }
    },
    
    FIRE( "fire", ( dimConfig ) -> dimConfig.TOWER_DISPENSERS.FIRE ) {
        @Override
        public void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserBlockEntity towerDispenser, Entity target,
                                   Vec3 center, Vec3 offset, Vec3 vecToTarget, double distanceH ) {
            BlockPos pos = towerDispenser.getBlockPos();
            AbstractArrow arrow = new Arrow( towerDispenser.getLevel(), pos.getX(), pos.getY(), pos.getZ() );
            arrow.setSecondsOnFire( 5 );
            towerDispenser.getTowerLogic().shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }
    },
    
    POTION( "potion", ( dimConfig ) -> dimConfig.TOWER_DISPENSERS.POTION ) {
        @Override
        public void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserBlockEntity towerDispenser, Entity target,
                                   Vec3 center, Vec3 offset, Vec3 vecToTarget, double distanceH ) {
            TowerDispenserConfig.PotionTowerDispenserTypeCategory config = dimConfig.TOWER_DISPENSERS.POTION;
            PotionTowerDispenserBlockEntity potionTower = (PotionTowerDispenserBlockEntity) towerDispenser;

            Level level = potionTower.getLevel();

            // Create the arrow
            BlockPos pos = potionTower.getBlockPos();
            Arrow arrow = new Arrow( level, pos.getX(), pos.getY(), pos.getZ() );
            MobEffectInstance potion;

            if ( potionTower.isDynamic() ) {
                potion = config.potionList.get().next( level.random );
            }
            else {
                potion = potionTower.getPotionCopy();
            }
            arrow.addEffect( potion == null
                    ? new MobEffectInstance( MobEffects.DIG_SLOWDOWN, 120 )
                    : potion );
            towerDispenser.getTowerLogic().shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }

        @Override
        public Supplier<TowerDispenserBlock> getBlock() { return PotionTowerDispenserBlock::new; }
    },
    
    GATLING( "gatling", ( dimConfig ) -> dimConfig.TOWER_DISPENSERS.GATLING ) {
        @Override
        public void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserBlockEntity towerDispenser, Entity target,
                                   Vec3 center, Vec3 offset, Vec3 vecToTarget, double distanceH ) {
            BlockPos pos = towerDispenser.getBlockPos();
            AbstractArrow arrow = new Arrow( towerDispenser.getLevel(), pos.getX(), pos.getY(), pos.getZ() );
            towerDispenser.getTowerLogic().shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }
    },
    
    FIREBALL( "fireball", ( dimConfig ) -> dimConfig.TOWER_DISPENSERS.FIREBALL ) {
        @Override
        public void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserBlockEntity towerDispenser, Entity target,
                                           Vec3 center, Vec3 offset, Vec3 vecToTarget, double distanceH ) {
            final double spawnOffset = 0.6;

            Level level = towerDispenser.getLevel();
            BlockPos topBlock = towerDispenser.getBlockPos().above();

            if( level.getBlockState( topBlock ).isAir() ) {
                level.setBlock( topBlock, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL );
            }

            float accel = (float) getFeatureConfig( dimConfig ).projectileSpeed.get();
            float var = (float) Math.sqrt( distanceH ) / 12.0F * (float) getFeatureConfig( dimConfig ).projectileVariance.get();

            for( float count = (float) getFeatureConfig( dimConfig ).attackDamage.get(); count >= 1.0F || count > 0.0F && count > level.random.nextFloat(); count-- ) {
                SmallFireball fireball = new SmallFireball(
                        level, center.x + offset.x * spawnOffset, center.y, center.z + offset.z * spawnOffset,
                        vecToTarget.x * accel + level.random.nextGaussian() * var,
                        vecToTarget.y * accel + level.random.nextGaussian() * var,
                        vecToTarget.z * accel + level.random.nextGaussian() * var
                );
                level.addFreshEntity( fireball );
            }

            level.playSound( null, center.x, center.y, center.z, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS,
                    1.0F, 1.0F / level.random.nextFloat() * 0.4F + 0.8F );
        }
    };
    
    /** The path for loot tables associated with these types. */
    public static final String LOOT_TABLE_PATH = "tower_dispensers/";
    public static final String BLOCK_CATEGORY = "tower_dispenser";
    
    private final String id;
    private final String displayName;
    /** A function that returns the feature config associated with this tower dispenser type for a given dimension config. */
    private final Function<DimensionConfigGroup, TowerDispenserConfig.TowerDispenserTypeCategory> configFunction;
    
    
    TowerType( String id, Function<DimensionConfigGroup, TowerDispenserConfig.TowerDispenserTypeCategory> configFunction ) {
        this( id, id.replace( "_", " " ) + " tower dispensers", configFunction );
    }
    
    TowerType( String id, String displayName, Function<DimensionConfigGroup, TowerDispenserConfig.TowerDispenserTypeCategory> configFunction ) {
        this.id = id;
        this.displayName = displayName;
        this.configFunction = configFunction;
    }
    
    public String getDisplayName() { return displayName; }
    
    public ResourceLocation getChestLootTable() {
        return DeadlyWorld.resourceLoc( References.CHEST_LOOT_PATH + LOOT_TABLE_PATH + this );
    }
    
    /** @return A Supplier of the Spawner Block to register for this Spawner Type */
    public Supplier<TowerDispenserBlock> getBlock() { return () -> new TowerDispenserBlock( this ); }
    
    public final TowerDispenserConfig.TowerDispenserTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configFunction.apply( dimConfigs ); }
    
    public abstract void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserBlockEntity towerDispenser, Entity target,
                                            Vec3 center, Vec3 offset, Vec3 vecToTarget, double distanceH );
    
    @Override
    public String toString() { return id; }
    
    public static TowerType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to load invalid tower dispenser type from index '{}'", index );
            return SIMPLE;
        }
        return values()[index];
    }
}