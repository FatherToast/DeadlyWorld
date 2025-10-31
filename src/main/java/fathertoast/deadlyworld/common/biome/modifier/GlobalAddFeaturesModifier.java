package fathertoast.deadlyworld.common.biome.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.core.registry.DWBiomeModifiers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.function.Supplier;

public record GlobalAddFeaturesModifier(
        HolderSet<PlacedFeature> features,
        GenerationStep.Decoration step
) implements BiomeModifier {

    public static Supplier<Codec<GlobalAddFeaturesModifier>> codecForRegistry() {
        return () -> RecordCodecBuilder.create( builder -> builder.group(
                PlacedFeature.LIST_CODEC.fieldOf( "features" ).forGetter( GlobalAddFeaturesModifier::features ),
                GenerationStep.Decoration.CODEC.fieldOf( "step" ).forGetter( GlobalAddFeaturesModifier::step )
        ).apply( builder, GlobalAddFeaturesModifier::new ) );
    }

    @Override
    public void modify( Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder ) {
        if ( phase == Phase.ADD ) {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            features.forEach( holder -> generationSettings.addFeature( step, holder ) );
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return DWBiomeModifiers.GLOBAL_ADD_FEATURES.get();
    }
}
