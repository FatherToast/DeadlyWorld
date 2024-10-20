package fathertoast.deadlyworld.datagen.loot;

import fathertoast.crust.api.datagen.loot.LootTableBuilder;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class DWEntityLootTables extends EntityLootSubProvider {
    protected DWEntityLootTables() { super( FeatureFlags.REGISTRY.allFlags() ); }
    
    /** Builds all loot tables for this provider. */
    @Override
    public void generate() {
        // New mobs
        //add( DWEntities.MIMIC, new LootTableBuilder().addLootTable( "chest", Blocks.CHEST.getLootTable() ) );
        
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
}