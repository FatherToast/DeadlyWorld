package fathertoast.deadlyworld.common.tile.tower;

import fathertoast.deadlyworld.common.block.TowerDispenserBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.FloorTrapConfig;
import fathertoast.deadlyworld.common.core.config.TowerDispenserConfig;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.function.Function;
import java.util.function.Supplier;

public enum TowerType implements IStringSerializable {

    SIMPLE("simple", (dimConfig) -> dimConfig.TOWER_DISPENSERS.SIMPLE) {
        @Override
        public void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserTileEntity tileEntity, PlayerEntity target,
                            Vector3d center, Vector3d offset, Vector3d vecToTarget, double distanceH ) {
            BlockPos pos = tileEntity.getBlockPos();
            AbstractArrowEntity arrow = new ArrowEntity( tileEntity.getLevel(), pos.getX(), pos.getY(), pos.getZ() );
            tileEntity.shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }
    },

    FIRE( "fire", (dimConfig) -> dimConfig.TOWER_DISPENSERS.SIMPLE) {
        @Override
        public void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserTileEntity tileEntity, PlayerEntity target,
                            Vector3d center, Vector3d offset, Vector3d vecToTarget, double distanceH ) {
            BlockPos pos = tileEntity.getBlockPos();
            AbstractArrowEntity arrow = new ArrowEntity( tileEntity.getLevel(), pos.getX(), pos.getY(), pos.getZ() );
            arrow.setSecondsOnFire( 5 );
            tileEntity.shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }
    },

    POTION( "potion", (dimConfig) -> dimConfig.TOWER_DISPENSERS.SIMPLE ) {
        @Override
        public void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserTileEntity tileEntity, PlayerEntity target,
                            Vector3d center, Vector3d offset, Vector3d vecToTarget, double distanceH ) {
            TowerDispenserConfig.PotionTowerDispenserTypeCategory config = dimConfig.TOWER_DISPENSERS.POTION;

            World world = tileEntity.getLevel();

            // Create the arrow
            BlockPos pos = tileEntity.getBlockPos();
            ArrowEntity arrow = new ArrowEntity( world, pos.getX(), pos.getY(), pos.getZ() );
            arrow.addEffect(tileEntity.getPotionEffect(config.potionList.get(), world.random));
            tileEntity.shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }
    },

    GATLING( "gatling", (dimConfig) -> dimConfig.TOWER_DISPENSERS.SIMPLE ) {
        @Override
        public
        void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserTileEntity tileEntity, PlayerEntity target,
                            Vector3d center, Vector3d offset, Vector3d vecToTarget, double distanceH ) {
            BlockPos pos = tileEntity.getBlockPos();
            AbstractArrowEntity arrow = new ArrowEntity( tileEntity.getLevel(), pos.getX(), pos.getY(), pos.getZ() );
            tileEntity.shootArrow(
                    center, offset, vecToTarget, distanceH,
                    (float) getFeatureConfig( dimConfig ).projectileSpeed.get(), (float) getFeatureConfig( dimConfig ).projectileVariance.get(), arrow
            );
        }
    },

    FIREBALL( "fireball", (dimConfig) -> dimConfig.TOWER_DISPENSERS.SIMPLE ) {
        @Override
        public
        void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserTileEntity tileEntity, PlayerEntity target,
                            Vector3d center, Vector3d offset, Vector3d vecToTarget, double distanceH ) {
            final double spawnOffset = 0.6;

            World world = tileEntity.getLevel();
            BlockPos topBlock = tileEntity.getBlockPos().above();

            if( world.getBlockState( topBlock ).isAir(world, topBlock) ) {
                world.setBlock( topBlock, Blocks.FIRE.defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE );
            }

            float accel = (float) getFeatureConfig( dimConfig ).projectileSpeed.get();
            float var   = (float) Math.sqrt( distanceH ) / 12.0F * (float) getFeatureConfig( dimConfig ).projectileVariance.get();

            for( float count = (float) getFeatureConfig( dimConfig ).attackDamage.get(); count >= 1.0F || count > 0.0F && count > world.random.nextFloat( ); count-- ) {
                SmallFireballEntity fireball = new SmallFireballEntity(
                        world, center.x + offset.x * spawnOffset, center.y, center.z + offset.z * spawnOffset,
                        vecToTarget.x * accel + world.random.nextGaussian( ) * var,
                        vecToTarget.y * accel + world.random.nextGaussian( ) * var,
                        vecToTarget.z * accel + world.random.nextGaussian( ) * var
                );
                world.addFreshEntity( fireball );
            }

            world.playSound( null, center.x, center.y, center.z, SoundEvents.BLAZE_SHOOT, SoundCategory.BLOCKS,
                    1.0F, 1.0F / world.random.nextFloat( ) * 0.4F + 0.8F);
        }
    };

    /** The path for loot tables associated with these types. */
    public static final String LOOT_TABLE_PATH = "tower_dispensers/";
    public static final String CATEGORY = "tower_dispensers";

    private final String id;
    private final String displayName;
    /** A function that returns the feature config associated with this tower dispenser type for a given dimension config. */
    private final Function<DimensionConfigGroup, TowerDispenserConfig.TowerDispenserTypeCategory> configFunction;


    TowerType( String id, Function<DimensionConfigGroup, TowerDispenserConfig.TowerDispenserTypeCategory> configFunction) {
        this( id, id.replace( "_", " " ) + " tower dispensers", configFunction );
    }

    TowerType( String id, String displayName, Function<DimensionConfigGroup, TowerDispenserConfig.TowerDispenserTypeCategory> configFunction ) {
        this.id = id;
        this.displayName = displayName;
        this.configFunction = configFunction;
    }

    @Override
    public String getSerializedName() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ResourceLocation getChestLootTable() {
        return DeadlyWorld.resourceLoc( References.CHEST_LOOT_PATH + LOOT_TABLE_PATH + this );
    }

    /** @return A Supplier of the Spawner Block to register for this Spawner Type */
    public Supplier<TowerDispenserBlock> getBlock() { return () -> new TowerDispenserBlock(this); }

    public final TowerDispenserConfig.TowerDispenserTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configFunction.apply( dimConfigs ); }

    public abstract void triggerAttack( DimensionConfigGroup dimConfig, TowerDispenserTileEntity tileEntity, PlayerEntity target,
                       Vector3d center, Vector3d offset, Vector3d vecToTarget, double distanceH );

    @Override
    public String toString( ) { return id; }


    public static TowerType fromIndex(int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to load invalid floor trap type from index '{}'", index );
            return SIMPLE;
        }
        return values()[index];
    }
}
