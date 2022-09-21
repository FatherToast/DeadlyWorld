package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.feature.features.FloorTrapFeature;
import fathertoast.deadlyworld.common.feature.features.SpawnerFeature;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapType;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DWFeatures {
    
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.FEATURES, DeadlyWorld.MOD_ID );

    public static List<RegistryObject<SpawnerFeature>> SPAWNERS = registerSpawners();
    public static List<RegistryObject<FloorTrapFeature>> FLOOR_TRAPS = registerFloorTraps();



    private static List<RegistryObject<SpawnerFeature>> registerSpawners() {
        List<RegistryObject<SpawnerFeature>> list = new ArrayList<>();

        for (SpawnerType spawnerType : SpawnerType.values()) {
            String name = spawnerType.getSerializedName();
            RegistryObject<SpawnerFeature> feature = register( name + "_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner( spawnerType )));
            list.add( feature );
        }
        return list;
    }

    private static List<RegistryObject<FloorTrapFeature>> registerFloorTraps() {
        List<RegistryObject<FloorTrapFeature>> list = new ArrayList<>();

        for (FloorTrapType trapType : FloorTrapType.values()) {
            String name = trapType.getSerializedName();
            RegistryObject<FloorTrapFeature> feature = register( name + "_floor_trap", () -> new FloorTrapFeature( NoFeatureConfig.CODEC, DWBlocks.floorTrap( trapType )));
            list.add( feature );
        }
        return list;
    }

    private static <FC extends IFeatureConfig, T extends Feature<FC>> RegistryObject<T> register( String name, Supplier<T> featureSupplier ) {
        return REGISTRY.register( name, featureSupplier );
    }
}