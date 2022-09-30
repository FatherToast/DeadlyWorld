package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;

public class DWEntities {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.ENTITIES, DeadlyWorld.MOD_ID );
    
    public static final RegistryObject<EntityType<MimicEntity>> MIMIC = register( "mimic",
            EntityType.Builder.of( MimicEntity::new, EntityClassification.MONSTER )
                    .sized( 0.9375F, 0.9375F ).clientTrackingRange( 8 ) );
    
    public static final RegistryObject<EntityType<MiniCreeperEntity>> MINI_CREEPER = register( "mini_creeper",
            EntityType.Builder.of( MiniCreeperEntity::new, EntityClassification.MONSTER )
                    .sized( 0.35F, 0.7F ).clientTrackingRange( 8 ) );
    
    public static final RegistryObject<EntityType<MiniZombieEntity>> MINI_ZOMBIE = register( "mini_zombie",
            EntityType.Builder.of( MiniZombieEntity::new, EntityClassification.MONSTER )
                    .sized( 0.35F, 0.85F ).clientTrackingRange( 8 ) );
    
    public static final RegistryObject<EntityType<MiniSkeletonEntity>> MINI_SKELETON = register( "mini_skeleton",
            EntityType.Builder.of( MiniSkeletonEntity::new, EntityClassification.MONSTER )
                    .sized( 0.35F, 0.85F ).clientTrackingRange( 8 ) );

    public static final RegistryObject<EntityType<MiniSpiderEntity>> MINI_SPIDER = register( "mini_spider",
            EntityType.Builder.of( MiniSpiderEntity::new, EntityClassification.MONSTER )
                    .sized( 0.35F, 0.35F ).clientTrackingRange( 8 ) );
    
    public static final RegistryObject<EntityType<MiniArrowEntity>> MINI_ARROW = register( "mini_arrow",
            EntityType.Builder.<MiniArrowEntity>of( MiniArrowEntity::new, EntityClassification.MISC )
                    .sized( 0.1F, 0.1F ).clientTrackingRange( 4 ).updateInterval( 20 ) );
    
    
    /** Sets the default attributes for entity types, such as max health, attack damage etc. */
    public static void createAttributes( EntityAttributeCreationEvent event ) {
        // New mobs
        event.put( MIMIC.get(), MimicEntity.createAttributes().build() );
        
        // Mini mobs
        event.put( MINI_CREEPER.get(), MiniCreeperEntity.createAttributes().build() );
        event.put( MINI_ZOMBIE.get(), MiniZombieEntity.createAttributes().build() );
        event.put( MINI_SKELETON.get(), MiniSkeletonEntity.createAttributes().build() );
        event.put( MINI_SPIDER.get(), MiniSpiderEntity.createAttributes().build() );
    }
    
    public static AttributeModifierMap.MutableAttribute standardMiniAttributes( AttributeModifierMap.MutableAttribute builder, double baseSpeed ) {
        return standardMiniAttributes( builder, baseSpeed, Attributes.MAX_HEALTH.getDefaultValue() );
    }
    
    public static AttributeModifierMap.MutableAttribute standardMiniAttributes( AttributeModifierMap.MutableAttribute builder, double baseSpeed, double baseHealth ) {
        return builder
                .add( Attributes.MOVEMENT_SPEED, baseSpeed * 1.3 )
                .add( Attributes.MAX_HEALTH, baseHealth / 3.0 );
    }
    
    public static void registerSpawnPlacements() {
        EntitySpawnPlacementRegistry.register( MINI_CREEPER.get(), PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules );
        EntitySpawnPlacementRegistry.register( MINI_ZOMBIE.get(), PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules );
        EntitySpawnPlacementRegistry.register( MINI_SKELETON.get(), PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules );
        EntitySpawnPlacementRegistry.register( MINI_SPIDER.get(), PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules );
    }
    
    private static <T extends Entity> RegistryObject<EntityType<T>> register( String name, EntityType.Builder<T> builder ) {
        return REGISTRY.register( name, () -> builder.build( name ) );
    }
}