package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class DWFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.FEATURES, DeadlyWorld.MOD_ID );
    
    public static RegistryObject<LoneSpawnerFeature> LONE_SPAWNER = register( "lone_spawner", LoneSpawnerFeature::new );
    public static RegistryObject<SilverfishNestFeature> SILVERFISH_NEST = register( "silverfish_nest", SilverfishNestFeature::new );
    public static RegistryObject<FloorTrapFeature> FLOOR_TRAP = register( "floor_trap", FloorTrapFeature::new );
    public static RegistryObject<PotionFloorTrapFeature> POTION_FLOOR_TRAP = register( "potion_floor_trap", PotionFloorTrapFeature::new );
    public static RegistryObject<SimpleTowerDispenserFeature> TOWER_DISPENSER = register("tower_dispenser", SimpleTowerDispenserFeature::new);
    
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