package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DWBiomes {

    public static final DeferredRegister<Biome> REGISTRY = DeferredRegister.create(ForgeRegistries.BIOMES, DeadlyWorld.MOD_ID);


    public static final RegistryObject<Biome> SEWER = createSewerBiome();


    private static RegistryObject<Biome> createSewerBiome() {
        return REGISTRY.register("sewer", () -> new Biome.Builder()
                .biomeCategory(Biome.Category.NONE)
                .depth(0.0F)
                .precipitation(Biome.RainType.NONE)
                .downfall(0.0F)
                .scale(1.0F)
                .temperature(0.5F)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .specialEffects(new BiomeAmbience.Builder()
                        .fogColor(0x92AF7E)
                        .waterColor(0x88AD9B)
                        .waterFogColor(0x92AF7E)
                        .foliageColorOverride(0x8C966D)
                        .grassColorOverride(0x8C966D)
                        .skyColor(0x697051)
                        .ambientLoopSound(DWSounds.SEWER_BIOME_LOOP.get())
                        .build())
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .mobSpawnSettings(new MobSpawnInfo.Builder()
                        .addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(DWEntities.MINI_SPIDER.get(), 100, 1, 3))
                        .build())
                .build());
    }
}
