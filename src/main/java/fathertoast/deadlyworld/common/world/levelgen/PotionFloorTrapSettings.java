package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.entity.PotionTrapBlockEntity;
import fathertoast.deadlyworld.common.config.dimension.TrapConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantFloatProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import fathertoast.deadlyworld.common.world.logic.PotionTrap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;

public record PotionFloorTrapSettings(
        FloatProvider requiredPlayerRange, FloatProvider checkSightChance,
        IntProvider resetTime, IntProvider triggersRemaining, FloatProvider decoyChance, FloatProvider dynamicChance
) {
    public static final Codec<PotionFloorTrapSettings> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
            FloatProvider.CODEC.fieldOf( "required_player_range" ).forGetter( PotionFloorTrapSettings::requiredPlayerRange ),
            FloatProvider.CODEC.fieldOf( "activation_sight_check" ).forGetter( PotionFloorTrapSettings::checkSightChance ),
            IntProvider.CODEC.fieldOf( "reset_time" ).forGetter( PotionFloorTrapSettings::resetTime ),
            IntProvider.CODEC.fieldOf( "triggers_remaining" ).forGetter( PotionFloorTrapSettings::triggersRemaining ),
            FloatProvider.CODEC.fieldOf( "decoy_chance" ).forGetter( PotionFloorTrapSettings::decoyChance ),
            FloatProvider.CODEC.fieldOf( "dynamic_chance" ).forGetter( PotionFloorTrapSettings::dynamicChance )
    ).apply( instance, PotionFloorTrapSettings::new ) );

    public static PotionFloorTrapSettings create( TrapConfig.PotionTrapTypeCategory config ) {
        return new PotionFloorTrapSettings(
                ConfigConstantFloatProvider.of( config.activationRange ),
                ConfigConstantFloatProvider.of( config.checkSightChance ),

                ConfigUniformIntProvider.of( config.resetTime ),
                ConfigConstantIntProvider.of( config.triggersRemaining ),

                ConfigConstantFloatProvider.of( config.decoyChance ),
                ConfigConstantFloatProvider.of( config.dynamicChance )
        );
    }

    public void initializeTrap( WorldGenLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof PotionTrapBlockEntity potionTrap ) {
            ((PotionTrap) potionTrap.getTrapLogic()).initializeTrap( level, pos, random, this );
        }
    }
}
