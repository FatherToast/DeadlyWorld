package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.entity.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.TrapConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantFloatProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;

public record SeaMineMobTrapSettings(
        FloatProvider decoyChance, FloatProvider requiredPlayerRange, FloatProvider checkSightChance,
        IntProvider triggersRemaining, IntProvider resetTime
) {
    public static final Codec<SeaMineMobTrapSettings> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
            FloatProvider.CODEC.fieldOf( "decoy_chance" ).forGetter( SeaMineMobTrapSettings::decoyChance ),
            FloatProvider.CODEC.fieldOf( "required_player_range" ).forGetter( SeaMineMobTrapSettings::requiredPlayerRange ),
            FloatProvider.CODEC.fieldOf( "activation_sight_check" ).forGetter( SeaMineMobTrapSettings::checkSightChance ),
            IntProvider.CODEC.fieldOf( "triggers_remaining" ).forGetter( SeaMineMobTrapSettings::triggersRemaining ),
            IntProvider.CODEC.fieldOf( "reset_time" ).forGetter( SeaMineMobTrapSettings::resetTime )
    ).apply( instance, SeaMineMobTrapSettings::new ) );

    public static FloorTrapSettings of( TrapType type, DimensionConfigGroup dimConfigs ) { return of( type.getFeatureConfig( dimConfigs ) ); }

    public static FloorTrapSettings of( TrapConfig.TrapTypeCategory config ) {
        return new FloorTrapSettings(
                ConfigConstantFloatProvider.of( config.decoyChance ),

                ConfigConstantFloatProvider.of( config.activationRange ),
                ConfigConstantFloatProvider.of( config.checkSightChance ),

                ConfigConstantIntProvider.of( config.triggersRemaining ),
                ConfigUniformIntProvider.of( config.resetTime )
        );
    }

    public void initializeTrap( WorldGenLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof DeadlyTrapBlockEntity trapBlockEntity ) {
            trapBlockEntity.getTrapLogic().initializeTrap( level, pos, random, this );
        }
    }
}
