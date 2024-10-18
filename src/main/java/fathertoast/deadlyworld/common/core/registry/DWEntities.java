package fathertoast.deadlyworld.common.core.registry;

import fathertoast.crust.api.config.common.field.AttributeListField;
import fathertoast.crust.api.config.common.value.ConfigDrivenAttributeModifierMap;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class DWEntities {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.ENTITY_TYPES, DeadlyWorld.MOD_ID );
    
    // Mimics
    //    public static final RegistryObject<EntityType<Mimic>> MIMIC = register( "mimic",
    //            EntityType.Builder.of( Mimic::new, MobCategory.MONSTER )
    //                    .sized( 0.9375F, 0.9375F ).clientTrackingRange( 8 ) );
    
    // Mini mobs
    public static final RegistryObject<EntityType<MiniCreeper>> MINI_CREEPER = register( "mini_creeper",
            EntityType.Builder.of( MiniCreeper::new, MobCategory.MONSTER )
                    .sized( 0.35F, 0.7F ).clientTrackingRange( 8 ) );
    public static final RegistryObject<EntityType<MiniZombie>> MINI_ZOMBIE = register( "mini_zombie",
            EntityType.Builder.of( MiniZombie::new, MobCategory.MONSTER )
                    .sized( 0.35F, 0.85F ).clientTrackingRange( 8 ) );
    public static final RegistryObject<EntityType<MiniSkeleton>> MINI_SKELETON = register( "mini_skeleton",
            EntityType.Builder.of( MiniSkeleton::new, MobCategory.MONSTER )
                    .sized( 0.35F, 0.85F ).clientTrackingRange( 8 ) );
    public static final RegistryObject<EntityType<MiniSpider>> MINI_SPIDER = register( "mini_spider",
            EntityType.Builder.of( MiniSpider::new, MobCategory.MONSTER )
                    .sized( 0.45F, 0.35F ).clientTrackingRange( 8 ) );
    public static final RegistryObject<EntityType<MicroGhast>> MICRO_GHAST = register( "micro_ghast",
            EntityType.Builder.of( MicroGhast::new, MobCategory.MONSTER )
                    .sized( 0.2F, 0.2F ).clientTrackingRange( 8 ) );
    
    // Projectiles
    public static final RegistryObject<EntityType<MiniArrow>> MINI_ARROW = register( "mini_arrow",
            EntityType.Builder.<MiniArrow>of( MiniArrow::new, MobCategory.MISC )
                    .sized( 0.1F, 0.1F ).clientTrackingRange( 4 ).updateInterval( 20 ) );
    public static final RegistryObject<EntityType<MicroFireball>> MICRO_FIREBALL = register( "micro_fireball",
            EntityType.Builder.<MicroFireball>of( MicroFireball::new, MobCategory.MISC )
                    .sized( 0.1F, 0.1F ).clientTrackingRange( 4 ).updateInterval( 20 ) );
    
    /** Sets the default attributes for entity types, such as max health, attack damage etc. */
    public static void createAttributes( EntityAttributeCreationEvent event ) {
        // Mimics
        //event.put( MIMIC.get(), Mimic.createAttributes().build() );
        
        // Mini mobs
        createConfigAttributes( event, MINI_CREEPER, Config.ENTITIES.MINIS.creeperAttributes, MiniCreeper.createAttributes() );
        createConfigAttributes( event, MINI_ZOMBIE, Config.ENTITIES.MINIS.zombieAttributes, MiniZombie.createAttributes() );
        createConfigAttributes( event, MINI_SKELETON, Config.ENTITIES.MINIS.skeletonAttributes, MiniSkeleton.createAttributes() );
        createConfigAttributes( event, MINI_SPIDER, Config.ENTITIES.MINIS.spiderAttributes, MiniSpider.createAttributes() );
        createConfigAttributes( event, MICRO_GHAST, Config.ENTITIES.MINIS.ghastAttributes, MicroGhast.createAttributes().add( Attributes.ATTACK_DAMAGE ) );
    }
    
    private static <T extends LivingEntity> void createConfigAttributes(
            EntityAttributeCreationEvent event, RegistryObject<EntityType<T>> type, AttributeListField attributeConfig, AttributeSupplier.Builder attributeBuilder ) {
        event.put( type.get(), new ConfigDrivenAttributeModifierMap( attributeConfig, attributeBuilder ) );
    }
    
    public static void registerMonsterSpawnPlacements( SpawnPlacementRegisterEvent event ) {
        registerMonsterSpawnPlacements( event, MINI_CREEPER );
        registerMonsterSpawnPlacements( event, MINI_ZOMBIE );
        registerMonsterSpawnPlacements( event, MINI_SKELETON );
        registerMonsterSpawnPlacements( event, MINI_SPIDER );
        registerSpawnPlacements( event, MICRO_GHAST, MicroGhast::checkMicroGhastSpawnRules );
    }
    
    /** Registers an entity type. */
    private static <T extends Entity> RegistryObject<EntityType<T>> register( String name, EntityType.Builder<T> builder ) {
        return REGISTRY.register( name, () -> builder.build( name ) );
    }
    
    /** Registers default monster spawn placement rules to an entity type that spawns on the ground. */
    private static <T extends Monster> void registerMonsterSpawnPlacements(
            SpawnPlacementRegisterEvent event, RegistryObject<EntityType<T>> entityType ) {
        registerSpawnPlacements( event, entityType, Monster::checkMonsterSpawnRules );
    }
    
    /** Registers spawn placement rules to an entity type that spawns on the ground. */
    private static <T extends Entity> void registerSpawnPlacements(
            SpawnPlacementRegisterEvent event, RegistryObject<EntityType<T>> entityType, SpawnPlacements.SpawnPredicate<T> predicate ) {
        registerSpawnPlacements( event, entityType, SpawnPlacements.Type.ON_GROUND, predicate );
    }
    
    /** Registers spawn placement rules to an entity type. */
    private static <T extends Entity> void registerSpawnPlacements(
            SpawnPlacementRegisterEvent event, RegistryObject<EntityType<T>> entityType, @Nullable SpawnPlacements.Type placementType,
            SpawnPlacements.SpawnPredicate<T> predicate ) {
        event.register( entityType.get(), placementType, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate,
                SpawnPlacementRegisterEvent.Operation.REPLACE );
    }
}