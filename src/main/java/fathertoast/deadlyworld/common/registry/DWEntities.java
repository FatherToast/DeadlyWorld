package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MimicEntity;
import fathertoast.deadlyworld.common.entity.MiniCreeperEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DWEntities {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.ENTITIES, DeadlyWorld.MOD_ID );
    
    public static final RegistryObject<EntityType<MimicEntity>> MIMIC = register( "mimic",
            EntityType.Builder.of( MimicEntity::new, EntityClassification.MONSTER )
                    .sized( 0.9375F, 0.9375F ).clientTrackingRange( 8 ) );
    
    public static final RegistryObject<EntityType<MiniCreeperEntity>> MINI_CREEPER = register( "mini_creeper",
            EntityType.Builder.of( MiniCreeperEntity::new, EntityClassification.MONSTER )
                    .sized( 0.3F, 0.65F ).clientTrackingRange( 8 ) );
    
    /** Sets the default attributes for entity types, such as max health, attack damage etc. */
    public static void createAttributes( EntityAttributeCreationEvent event ) {
        // New mobs
        event.put( MIMIC.get(), MimicEntity.createAttributes().build() );
        
        // Mini mobs
        event.put( MINI_CREEPER.get(), MiniCreeperEntity.createAttributes().build() );
    }
    
    private static <T extends Entity> RegistryObject<EntityType<T>> register( String name, EntityType.Builder<T> builder ) {
        return REGISTRY.register( name, () -> builder.build( name ) );
    }
}