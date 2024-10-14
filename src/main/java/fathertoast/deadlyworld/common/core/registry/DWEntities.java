package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DWEntities {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.ENTITY_TYPES, DeadlyWorld.MOD_ID );
    
    //    public static final RegistryObject<EntityType<Mimic>> MIMIC = register( "mimic",
    //            EntityType.Builder.of( Mimic::new, MobCategory.MONSTER )
    //                    .sized( 0.9375F, 0.9375F ).clientTrackingRange( 8 ) );
    
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
                    .sized( 0.35F, 0.35F ).clientTrackingRange( 8 ) );
    
    public static final RegistryObject<EntityType<MiniArrow>> MINI_ARROW = register( "mini_arrow",
            EntityType.Builder.<MiniArrow>of( MiniArrow::new, MobCategory.MISC )
                    .sized( 0.1F, 0.1F ).clientTrackingRange( 4 ).updateInterval( 20 ) );
    
    
    /** Sets the default attributes for entity types, such as max health, attack damage etc. */
    public static void createAttributes( EntityAttributeCreationEvent event ) {
        // New mobs
        //event.put( MIMIC.get(), Mimic.createAttributes().build() );
        
        // Mini mobs
        event.put( MINI_CREEPER.get(), MiniCreeper.createAttributes().build() );
        event.put( MINI_ZOMBIE.get(), MiniZombie.createAttributes().build() );
        event.put( MINI_SKELETON.get(), MiniSkeleton.createAttributes().build() );
        event.put( MINI_SPIDER.get(), MiniSpider.createAttributes().build() );
    }
    
    public static AttributeSupplier.Builder standardMiniAttributes( AttributeSupplier.Builder builder, double baseSpeed ) {
        return standardMiniAttributes( builder, baseSpeed, Attributes.MAX_HEALTH.getDefaultValue() );
    }
    
    public static AttributeSupplier.Builder standardMiniAttributes( AttributeSupplier.Builder builder, double baseSpeed, double baseHealth ) {
        return builder
                .add( Attributes.MOVEMENT_SPEED, baseSpeed * 1.3 )
                .add( Attributes.MAX_HEALTH, baseHealth / 3.0 );
    }
    
    public static void registerSpawnPlacements( SpawnPlacementRegisterEvent event ) {
        registerSpawnPlacements( event, MINI_CREEPER );
        registerSpawnPlacements( event, MINI_ZOMBIE );
        registerSpawnPlacements( event, MINI_SKELETON );
        registerSpawnPlacements( event, MINI_SPIDER );
    }
    
    private static <T extends Entity> RegistryObject<EntityType<T>> register( String name, EntityType.Builder<T> builder ) {
        return REGISTRY.register( name, () -> builder.build( name ) );
    }
    
    private static <T extends Monster> void registerSpawnPlacements( SpawnPlacementRegisterEvent event, RegistryObject<EntityType<T>> entityType ) {
        event.register( entityType.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE );
    }
}