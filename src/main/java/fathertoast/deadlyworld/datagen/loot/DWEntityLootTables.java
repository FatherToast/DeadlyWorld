package fathertoast.deadlyworld.datagen.loot;

import fathertoast.crust.api.datagen.loot.LootEntryItemBuilder;
import fathertoast.crust.api.datagen.loot.LootTableBuilder;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class DWEntityLootTables extends EntityLootSubProvider {
    protected DWEntityLootTables() { super( FeatureFlags.REGISTRY.allFlags() ); }
    
    /** Builds all loot tables for this provider. */
    @Override
    public void generate() {
        // New mobs
        add( DWEntities.CHEST_MIMIC, new LootTableBuilder().addPool( mimicCore() ) );
        add( DWEntities.MINI_CHEST_MIMIC, new LootTableBuilder().addPool( mimicCore() ) );
        add( DWEntities.JUKEBOX_MIMIC, new LootTableBuilder().addPool( mimicCore() ).addLootTable( "jukebox", Blocks.JUKEBOX.getLootTable() ) );
        add( DWEntities.SPAWNER_MIMIC, new LootTableBuilder().addPool( mimicCore() ) );
        add( DWEntities.MINI_SPAWNER_MIMIC, new LootTableBuilder().addPool( mimicCore() ) );
        
        // Mini mobs
        addVanillaLike( DWEntities.MINI_CREEPER, EntityType.CREEPER );
        addVanillaLike( DWEntities.MINI_ZOMBIE, EntityType.ZOMBIE );
        addVanillaLike( DWEntities.MINI_SKELETON, EntityType.SKELETON );
        addVanillaLike( DWEntities.MINI_SPIDER, EntityType.SPIDER );
        addVanillaLike( DWEntities.MICRO_GHAST, EntityType.GHAST );
    }
    
    protected <T extends Entity> void addVanillaLike( Supplier<EntityType<T>> entity, EntityType<?> vanillaEntity ) {
        add( entity.get(), new LootTableBuilder()
                .addLootTable( "vanilla", vanillaEntity.getDefaultLootTable() ).toLootTable() );
    }
    
    protected <T extends Entity> void add( Supplier<EntityType<T>> entity, LootTableBuilder builder ) {
        add( entity.get(), builder.toLootTable() );
    }
    
    /** Supplies the entity types this loot table provider will be used for. */
    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        // This is basically pulled straight from the forge docs on data gen for block/entity loot tables
        return DWEntities.REGISTRY.getEntries().stream().flatMap( RegistryObject::stream );
    }
    
    protected LootPool.Builder mimicCore() {
        return new DWLootPoolBuilder( "mimic_core" )
                .addEntry( new LootEntryItemBuilder( DWItems.MIMIC_CORE.get() ).setCount( -9, 1 ).toLootEntry() )
                .toLootPool();
    }
}