package fathertoast.deadlyworld.common.world.logic;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.entity.TowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.TowerDispenserConfig;
import fathertoast.deadlyworld.common.world.levelgen.DeadlyFeature;
import fathertoast.deadlyworld.common.world.levelgen.settings.FloorTrapSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PotionTower extends BaseTower {

    public static final String POTION_KEY = "Potion";
    public static final String DYNAMIC_POTION_KEY = "DynamicPotion";

    private boolean dynamic;
    @Nullable
    private MobEffectInstance potion;


    public PotionTower( TowerDispenserBlockEntity towerDispenser ) {
        super( TowerType.POTION, towerDispenser, towerDispenser );
    }

    // Overridden in block
    @Override
    public void activateTower(ServerLevel level, BlockPos pos, Entity target, Vec3 center, Vec3 offset, Vec3 vecToTarget, double distance) { }

    public void initializeTower(WorldGenLevel level, BlockPos pos, RandomSource random, FloorTrapSettings trapSettings ) {
        final TowerDispenserConfig.PotionTowerDispenserTypeCategory towerConfig =
                (TowerDispenserConfig.PotionTowerDispenserTypeCategory) towerType.getFeatureConfig( Config.getDimensionConfigs( level.getLevel() ) );

        DeadlyFeature.debugMarkerIfEnabled( level, pos, towerConfig );

        /*
        initializeTower( getLevel(), pos, random,
                trapSettings.requiredPlayerRange().sample( random ),
                trapSettings.checkSightChance().sample( random ),
                trapSettings.resetTime().getMinValue(),
                trapSettings.resetTime().getMaxValue()
        );

         */
    }

    @Override
    public void initializeTower( @Nullable Level level, BlockPos pos, RandomSource random ) {
        final TowerDispenserConfig.PotionTowerDispenserTypeCategory towerConfig =
                (TowerDispenserConfig.PotionTowerDispenserTypeCategory) towerType.getFeatureConfig( Config.getDimensionConfigs( level ) );

        initializeTower( level, pos, random,
                towerConfig.activationRange.get(), (float) towerConfig.checkSightChance.get(),
                towerConfig.attackDelay.getMin(), towerConfig.attackDelay.getMax(), towerConfig.attackDamage == null ? -1.0F : (float) towerConfig.attackDamage.get(),
                (float) towerConfig.projectileSpeed.get(), (float) towerConfig.projectileVariance.get(), towerConfig.dynamicChance.rollChance( random )
        );
    }

    public void initializeTower( @Nullable Level level, BlockPos pos, RandomSource random, double activationRange,
                                 float checkSightChance, int minAttackDelay, int maxAttackDelay, float attackDamage,
                                 float projectileSpeed, float projectileVariance, boolean dynamic ) {
        this.checkSight = roll( random, checkSightChance );
        this.activationRange = activationRange;
        this.minAttackDelay = minAttackDelay;
        this.maxAttackDelay = maxAttackDelay;
        this.attackDamage = attackDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileVariance = projectileVariance;
        this.dynamic = dynamic;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    @Nullable
    public MobEffectInstance getPotionCopy() {
        return potion == null ? null : new MobEffectInstance( potion );
    }

    @Override
    public void load( @Nullable Level level, BlockPos pos, CompoundTag loadTag ) {
        super.load( level, pos, loadTag );

        if ( NBTHelper.containsNumber( loadTag, DYNAMIC_POTION_KEY ) )
            dynamic = loadTag.getBoolean( DYNAMIC_POTION_KEY );
        if ( NBTHelper.containsCompound( loadTag, POTION_KEY ))
            potion = MobEffectInstance.load( loadTag.getCompound( POTION_KEY ) );
    }

    @Override
    public CompoundTag save( CompoundTag saveTag ) {
        saveTag = super.save( saveTag );

        saveTag.putBoolean( DYNAMIC_POTION_KEY, dynamic );
        saveTag.put( POTION_KEY, potion == null ? new CompoundTag() : potion.save( new CompoundTag() ) );

        return saveTag;
    }
}
