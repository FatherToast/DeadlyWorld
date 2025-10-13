package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.MiniDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.SimpleDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.BuriedLiquidFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.*;
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
    public static RegistryObject<TowerFeature> TOWER = register( "tower", TowerFeature::new );
    
    public static RegistryObject<BuriedLiquidFeature> BURIED_LIQUID = register( "buried_liquid", BuriedLiquidFeature::new );
    
    public static RegistryObject<SimpleDungeonFeature> SIMPLE_DUNGEON = register( "simple_dungeon", SimpleDungeonFeature::new );
    public static RegistryObject<MiniDungeonFeature> MINI_DUNGEON = register( "mini_dungeon", MiniDungeonFeature::new );
    
    
    private static <T extends Feature<?>> RegistryObject<T> register( String name, Supplier<T> featureSupplier ) {
        return REGISTRY.register( name, featureSupplier );
    }
}