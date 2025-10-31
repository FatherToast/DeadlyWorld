package fathertoast.deadlyworld.common.biome.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.config.levelgen.setting.BooleanFieldSetting;
import fathertoast.deadlyworld.common.core.registry.DWBiomeModifiers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A copy-paste of {@link net.minecraftforge.common.world.ForgeBiomeModifiers.RemoveFeaturesBiomeModifier}
 * with the addition of a config-driven boolean value used to determine if the modifier should run or not.
 */
public record ConfigRemoveFeaturesModifier(
        BooleanFieldSetting enabled,
        HolderSet<Biome> biomes,
        HolderSet<PlacedFeature> features,
        Set<GenerationStep.Decoration> steps
) implements BiomeModifier {
    
    public static Supplier<Codec<ConfigRemoveFeaturesModifier>> codecForRegistry() {
        return () -> RecordCodecBuilder.create( builder -> builder.group(
                BooleanFieldSetting.CODEC.fieldOf( "enabled" ).forGetter( ConfigRemoveFeaturesModifier::enabled ),
                Biome.LIST_CODEC.fieldOf( "biomes" ).forGetter( ConfigRemoveFeaturesModifier::biomes ),
                PlacedFeature.LIST_CODEC.fieldOf( "features" ).forGetter( ConfigRemoveFeaturesModifier::features ),
                
                new ExtraCodecs.EitherCodec<>( GenerationStep.Decoration.CODEC.listOf(), GenerationStep.Decoration.CODEC ).xmap(
                        either -> either.map( Set::copyOf, Set::of ), // convert list/singleton to set when decoding
                        set -> set.size() == 1 ? Either.right( set.toArray( GenerationStep.Decoration[]::new )[0] ) : Either.left( List.copyOf( set ) )
                ).optionalFieldOf( "steps", EnumSet.allOf( GenerationStep.Decoration.class ) ).forGetter( ConfigRemoveFeaturesModifier::steps )
        ).apply( builder, ConfigRemoveFeaturesModifier::new ) );
    }
    
    @Override
    public void modify( Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder ) {
        if( !enabled.get() ) return;
        
        if( phase == Phase.REMOVE && biomes.contains( biome ) ) {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            
            for( GenerationStep.Decoration step : steps ) {
                generationSettings.getFeatures( step ).removeIf( features::contains );
            }
        }
    }
    
    @Override
    public Codec<? extends BiomeModifier> codec() {
        return DWBiomeModifiers.CONFIG_REMOVE_FEATURES.get();
    }
}