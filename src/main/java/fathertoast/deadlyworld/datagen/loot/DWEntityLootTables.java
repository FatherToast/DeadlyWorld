package fathertoast.deadlyworld.datagen.loot;

import fathertoast.crust.api.datagen.loot.LootTableBuilder;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

public class DWEntityLootTables extends EntityLootSubProvider {
    protected DWEntityLootTables() { super( FeatureFlags.REGISTRY.allFlags() ); }
    
    /** Builds all loot tables for this provider. */
    @Override
    public void generate() {
        // New mobs
        //        add( DWEntities.MIMIC.get(), new LootTableBuilder()
        //                .addLootTable( "chest", Blocks.CHEST.getLootTable() ).toLootTable() );
        
        // Mini mobs
        add( DWEntities.MINI_CREEPER.get(), new LootTableBuilder()
                .addLootTable( "vanilla", EntityType.CREEPER.getDefaultLootTable() ).toLootTable() );
        add( DWEntities.MINI_ZOMBIE.get(), new LootTableBuilder()
                .addLootTable( "vanilla", EntityType.ZOMBIE.getDefaultLootTable() ).toLootTable() );
        add( DWEntities.MINI_SKELETON.get(), new LootTableBuilder()
                .addLootTable( "vanilla", EntityType.SKELETON.getDefaultLootTable() ).toLootTable() );
        add( DWEntities.MINI_SPIDER.get(), new LootTableBuilder()
                .addLootTable( "vanilla", EntityType.SPIDER.getDefaultLootTable() ).toLootTable() );
    }
    
    /** Supplies the entity types this loot table provider will be used for. */
    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        // This is basically pulled straight from the forge docs on data gen for block/entity loot tables
        return DWEntities.REGISTRY.getEntries().stream().flatMap( RegistryObject::stream );
    }
}