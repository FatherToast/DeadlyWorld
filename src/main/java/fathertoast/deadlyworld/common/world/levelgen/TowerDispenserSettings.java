package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.entity.TowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.TowerConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantFloatProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;

public record TowerDispenserSettings(
        FloatProvider requiredPlayerRange, FloatProvider checkSightChance,
        IntProvider attackDelay, FloatProvider attackDamage,
        FloatProvider projectileSpeed, FloatProvider projectileVariance
) {
    public static final Codec<TowerDispenserSettings> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            FloatProvider.CODEC.fieldOf( "required_player_range" ).forGetter( TowerDispenserSettings::requiredPlayerRange ),
            FloatProvider.CODEC.fieldOf( "activation_sight_check" ).forGetter( TowerDispenserSettings::checkSightChance ),
            IntProvider.CODEC.fieldOf( "attack_delay" ).forGetter( TowerDispenserSettings::attackDelay ),
            FloatProvider.CODEC.fieldOf( "attack_damage" ).forGetter( TowerDispenserSettings::attackDamage ),
            FloatProvider.CODEC.fieldOf( "projectile_speed" ).forGetter( TowerDispenserSettings::projectileSpeed ),
            FloatProvider.CODEC.fieldOf( "projectile_variance" ).forGetter( TowerDispenserSettings::projectileVariance )
    ).apply( instance, TowerDispenserSettings::new ) );
    
    public static TowerDispenserSettings of( TowerType type, DimensionConfigGroup dimConfigs ) {
        return of( type.getFeatureConfig( dimConfigs ) );
    }
    
    public static TowerDispenserSettings of( TowerConfig.TowerTypeCategory config ) {
        return new TowerDispenserSettings(
                ConfigConstantFloatProvider.of( config.activationRange ),
                ConfigConstantFloatProvider.of( config.checkSightChance ),
                
                ConfigUniformIntProvider.of( config.attackDelay ),
                ConfigConstantFloatProvider.of( config.attackDamage ),
                
                ConfigConstantFloatProvider.of( config.projectileSpeed ),
                ConfigConstantFloatProvider.of( config.projectileVariance )
        );
    }
    
    public void initializeDispenser( WorldGenLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof TowerDispenserBlockEntity towerDispenser ) {
            towerDispenser.getTowerLogic().initializeTower( level, pos, random, this );
        }
    }
}