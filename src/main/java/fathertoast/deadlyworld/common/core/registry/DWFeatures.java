package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DWFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.FEATURES, DeadlyWorld.MOD_ID );
    
    //public static List<RegistryObject<SpawnerFeature>> SPAWNERS = registerSpawners();
    //public static List<RegistryObject<FloorTrapFeature>> FLOOR_TRAPS = registerFloorTraps();
    
    static {
        //        for( SpawnerType spawnerType : SpawnerType.values() ) {
        //            register( spawnerType.toString() + "_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner( spawnerType ) ) );
        //        }
    }
    
    //    private static List<RegistryObject<SpawnerFeature>> registerSpawners() {
    //        List<RegistryObject<SpawnerFeature>> list = new ArrayList<>();
    //
    //        for( SpawnerType spawnerType : SpawnerType.values() ) {
    //            String name = spawnerType.toString();
    //            RegistryObject<SpawnerFeature> feature = register( name + "_spawner", () -> new SpawnerFeature( NoFeatureConfig.CODEC, DWBlocks.spawner( spawnerType ) ) );
    //            list.add( feature );
    //        }
    //        return list;
    //    }
    
    //    private static List<RegistryObject<FloorTrapFeature>> registerFloorTraps() {
    //        List<RegistryObject<FloorTrapFeature>> list = new ArrayList<>();
    //
    //        for( FloorTrapType trapType : FloorTrapType.values() ) {
    //            String name = trapType.toString();
    //            RegistryObject<FloorTrapFeature> feature = register( name + "_floor_trap", () -> new FloorTrapFeature( NoFeatureConfig.CODEC, DWBlocks.floorTrap( trapType ) ) );
    //            list.add( feature );
    //        }
    //        return list;
    //    }
    
    private static <T extends Feature<?>> RegistryObject<T> register( String name, Supplier<T> featureSupplier ) {
        return REGISTRY.register( name, featureSupplier );
    }
}