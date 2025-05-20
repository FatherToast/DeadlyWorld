package fathertoast.deadlyworld.common.world.levelgen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.SpawnerConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantFloatProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;

public record SpawnerSettings(
        IntProvider requiredPlayerRange, FloatProvider checkSightChance, IntProvider maxNearbyEntities,
        IntProvider spawnDelay, IntProvider spawnDelayProgression, FloatProvider spawnDelayRecovery,
        IntProvider maxSpawns, IntProvider spawnCount, IntProvider spawnRange, FloatProvider dynamicChance
        //???Provider spawnList TODO allow override for entity list config setting here
) {
    private static final Codec<IntProvider> SHORT_CODEC = IntProvider.codec( 0, Short.MAX_VALUE );
    public static final Codec<SpawnerSettings> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            SHORT_CODEC.fieldOf( "required_player_range" ).forGetter( SpawnerSettings::requiredPlayerRange ),
            FloatProvider.CODEC.fieldOf( "activation_sight_check" ).forGetter( SpawnerSettings::checkSightChance ),
            SHORT_CODEC.fieldOf( "max_nearby_entities" ).forGetter( SpawnerSettings::maxNearbyEntities ),
            
            SHORT_CODEC.fieldOf( "delay" ).forGetter( SpawnerSettings::spawnDelay ),
            SHORT_CODEC.fieldOf( "delay_progression" ).forGetter( SpawnerSettings::spawnDelayProgression ),
            FloatProvider.CODEC.fieldOf( "delay_recovery_rate" ).forGetter( SpawnerSettings::spawnDelayRecovery ),
            
            SHORT_CODEC.fieldOf( "max_spawns" ).forGetter( SpawnerSettings::maxSpawns ),
            SHORT_CODEC.fieldOf( "spawn_count" ).forGetter( SpawnerSettings::spawnCount ),
            SHORT_CODEC.fieldOf( "spawn_range" ).forGetter( SpawnerSettings::spawnRange ),
            FloatProvider.CODEC.fieldOf( "dynamic_chance" ).forGetter( SpawnerSettings::dynamicChance )
    ).apply( instance, SpawnerSettings::new ) );
    
    public static SpawnerSettings of( SpawnerType type, DimensionConfigGroup dimConfigs ) { return of( type.getFeatureConfig( dimConfigs ) ); }
    
    public static SpawnerSettings of( SpawnerConfig.SpawnerTypeCategory config ) {
        return new SpawnerSettings(
                ConfigConstantIntProvider.of( config.activationRange ),
                ConfigConstantFloatProvider.of( config.checkSightChance ),
                ConfigConstantIntProvider.of( config.maxNearbyEntities ),
                
                ConfigUniformIntProvider.of( config.delayMin, config.delayMax ),
                ConfigConstantIntProvider.of( config.delayProgression ),
                ConfigConstantFloatProvider.of( config.delayRecovery ),
                
                ConfigConstantIntProvider.of( config.maxSpawns ),
                ConfigConstantIntProvider.of( config.spawnCount ),
                ConfigConstantIntProvider.of( config.spawnRange ),
                ConfigConstantFloatProvider.of( config.dynamicChance )
        );
    }
    
    public void initializeSpawner( WorldGenLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity spawnerBlockEntity ) {
            spawnerBlockEntity.getSpawnerLogic().initializeSpawner( level, pos, random, this );
        }
    }
}