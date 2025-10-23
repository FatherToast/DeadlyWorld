package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.entity.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.FloorTrapConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantFloatProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;

public record FloorTrapSettings(
        FloatProvider decoyChance, FloatProvider requiredPlayerRange, FloatProvider checkSightChance,
        IntProvider triggersRemaining, IntProvider resetTime
) {
    public static final Codec<FloorTrapSettings> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            FloatProvider.CODEC.fieldOf( "decoy_chance" ).forGetter( FloorTrapSettings::decoyChance ),
            FloatProvider.CODEC.fieldOf( "required_player_range" ).forGetter( FloorTrapSettings::requiredPlayerRange ),
            FloatProvider.CODEC.fieldOf( "activation_sight_check" ).forGetter( FloorTrapSettings::checkSightChance ),
            IntProvider.CODEC.fieldOf( "triggers_remaining" ).forGetter( FloorTrapSettings::triggersRemaining ),
            IntProvider.CODEC.fieldOf( "reset_time" ).forGetter( FloorTrapSettings::resetTime )
    ).apply( instance, FloorTrapSettings::new ) );
    
    public static FloorTrapSettings of(FloorTrapType type, DimensionConfigGroup dimConfigs ) { return of( type.getConfig( dimConfigs ) ); }
    
    public static FloorTrapSettings of( FloorTrapConfig.TrapTypeCategory config ) {
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