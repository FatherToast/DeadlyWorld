package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.MiniDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.NormalDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.BuriedBlocksFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.DeadlyOreFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.InfestedOreFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.LoneChestFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class DWFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.FEATURES, DeadlyWorld.MOD_ID );
    
    // Decoration features
    public static RegistryObject<LoneChestFeature> LONE_CHEST = register( "lone_chest", LoneChestFeature::new );
    public static RegistryObject<LoneSpawnerFeature> LONE_SPAWNER = register( "lone_spawner", LoneSpawnerFeature::new );
    public static RegistryObject<LoneHangingSpawnerFeature> LONE_HANGING_SPAWNER = register( "lone_hanging_spawner", LoneHangingSpawnerFeature::new );
    public static RegistryObject<SilverfishNestFeature> SILVERFISH_NEST = register( "silverfish_nest", SilverfishNestFeature::new );
    public static RegistryObject<FloorTrapFeature> FLOOR_TRAP = register( "floor_trap", FloorTrapFeature::new );
    public static RegistryObject<PotionFloorTrapFeature> POTION_FLOOR_TRAP = register( "potion_floor_trap", PotionFloorTrapFeature::new );
    public static RegistryObject<TowerFeature> TOWER = register( "tower", TowerFeature::new );
    public static RegistryObject<SeaMineFeature> SEA_MINE = register( "sea_mine", SeaMineFeature::new );
    public static RegistryObject<PitfallTrapFeature> PITFALL_TRAP = register( "pitfall_trap", PitfallTrapFeature::new );
    
    // Dungeon features
    public static RegistryObject<NormalDungeonFeature> NORMAL_DUNGEON = register( "simple_dungeon", NormalDungeonFeature::new );
    public static RegistryObject<MiniDungeonFeature> MINI_DUNGEON = register( "mini_dungeon", MiniDungeonFeature::new );
    
    // Vein features
    public static RegistryObject<BuriedBlocksFeature> BURIED_BLOCK = register( "buried_block", BuriedBlocksFeature::new );
    public static RegistryObject<DeadlyOreFeature> DEADLY_ORE = register( "deadly_ore", DeadlyOreFeature::new );
    public static RegistryObject<InfestedOreFeature> INFESTED_ORE = register( "infested_ore", InfestedOreFeature::new );
    
    
    private static <T extends Feature<?>> RegistryObject<T> register( String name, Supplier<T> featureSupplier ) {
        return REGISTRY.register( name, featureSupplier );
    }
}