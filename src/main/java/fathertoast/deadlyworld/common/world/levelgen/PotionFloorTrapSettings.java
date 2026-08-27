package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.entity.PotionTrapBlockEntity;
import fathertoast.deadlyworld.common.config.dimension.FloorTrapConfig;
import fathertoast.crust.api.config.common.value.provider.ConfigConstantFloatProvider;
import fathertoast.crust.api.config.common.value.provider.ConfigConstantIntProvider;
import fathertoast.crust.api.config.common.value.provider.ConfigUniformIntProvider;
import fathertoast.deadlyworld.common.world.logic.PotionTrap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;

public record PotionFloorTrapSettings(
        FloatProvider camoChance, FloatProvider decoyChance, FloatProvider requiredPlayerRange,
        FloatProvider checkSightChance, IntProvider triggersRemaining, IntProvider resetTime,
        FloatProvider dynamicChance
) {
    public static final Codec<PotionFloorTrapSettings> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            FloatProvider.CODEC.fieldOf( "camo_chance" ).forGetter( PotionFloorTrapSettings::camoChance ),
            FloatProvider.CODEC.fieldOf( "decoy_chance" ).forGetter( PotionFloorTrapSettings::decoyChance ),
            FloatProvider.CODEC.fieldOf( "required_player_range" ).forGetter( PotionFloorTrapSettings::requiredPlayerRange ),
            FloatProvider.CODEC.fieldOf( "activation_sight_check" ).forGetter( PotionFloorTrapSettings::checkSightChance ),
            IntProvider.CODEC.fieldOf( "triggers_remaining" ).forGetter( PotionFloorTrapSettings::triggersRemaining ),
            IntProvider.CODEC.fieldOf( "reset_time" ).forGetter( PotionFloorTrapSettings::resetTime ),
            FloatProvider.CODEC.fieldOf( "dynamic_chance" ).forGetter( PotionFloorTrapSettings::dynamicChance )
    ).apply( instance, PotionFloorTrapSettings::new ) );
    
    public static PotionFloorTrapSettings create( FloorTrapConfig.PotionTrapTypeCategory config ) {
        return new PotionFloorTrapSettings(
                ConfigConstantFloatProvider.of( config.camoChance ),
                ConfigConstantFloatProvider.of( config.decoyChance ),
                
                ConfigConstantFloatProvider.of( config.activationRange ),
                ConfigConstantFloatProvider.of( config.checkSightChance ),
                
                ConfigConstantIntProvider.of( config.triggersRemaining ),
                ConfigUniformIntProvider.of( config.resetTime ),
                
                ConfigConstantFloatProvider.of( config.dynamicChance )
        );
    }
    
    public void initializeTrap( WorldGenLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof PotionTrapBlockEntity potionTrap ) {
            ((PotionTrap) potionTrap.getTrapLogic()).initializeTrap( level, pos, random, this );
        }
    }
}