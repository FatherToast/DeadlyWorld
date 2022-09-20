package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.feature.features.SpawnerFeature;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DWFeatures {
    
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.FEATURES, DeadlyWorld.MOD_ID );
    
    // TEMP
    public static final RegistryObject<Feature<NoFeatureConfig>> DEFAULT_SPAWNER =
            register( "default_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner(SpawnerType.DEFAULT)));
    public static final RegistryObject<Feature<NoFeatureConfig>> DUNGEON_SPAWNER =
            register( "dungeon_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner(SpawnerType.DUNGEON)));
    public static final RegistryObject<Feature<NoFeatureConfig>> SWARM_SPAWNER =
            register( "swarm_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner(SpawnerType.SWARM)));
    public static final RegistryObject<Feature<NoFeatureConfig>> BRUTAL_SPAWNER =
            register( "brutal_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner(SpawnerType.BRUTAL)));
    public static final RegistryObject<Feature<NoFeatureConfig>> NEST_SPAWNER =
            register( "nest_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner(SpawnerType.NEST)));
    public static final RegistryObject<Feature<NoFeatureConfig>> STREAM_SPAWNER =
            register( "stream_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner(SpawnerType.STREAM)));
    public static final RegistryObject<Feature<NoFeatureConfig>> MINI_SPAWNER =
            register( "mini_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner(SpawnerType.STREAM)));


    private static <FC extends IFeatureConfig, T extends Feature<FC>> RegistryObject<T> register( String name, Supplier<T> featureSupplier ) {
        return REGISTRY.register( name, featureSupplier );
    }
}