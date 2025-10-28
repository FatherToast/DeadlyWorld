package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public final class DWDamageTypes {
    
    public static final ResourceKey<DamageType> SPIKE_TRAP = create( "spike_trap" );
    
    public static final ResourceKey<DamageType> TRIGGER_SILVERFISH = create( "trigger_silverfish" );
    
    /**
     * @return A DamageSource instance of the specified damage type.<br>
     * requires level registry access.
     */
    public static DamageSource of( Level level, ResourceKey<DamageType> key ) {
        return new DamageSource( level.registryAccess().registryOrThrow( Registries.DAMAGE_TYPE ).getHolderOrThrow( key ) );
    }
    
    /** Helper method for creating a registry key for a damage type. */
    private static ResourceKey<DamageType> create( String name ) {
        return ResourceKey.create( Registries.DAMAGE_TYPE, DeadlyWorld.rl( name ) );
    }
    
    /** Called by registry set builder to generate our damage types. */
    public static void bootstrap( BootstapContext<DamageType> context ) {
        register( context, SPIKE_TRAP, new DamageType( msg( "spike_trap" ), 0.1F ) );
        register( context, TRIGGER_SILVERFISH, new DamageType( msg( "trigger_silverfish" ), DamageScaling.NEVER, 0.0F ) );
    }
    
    /** Helper method for damage type data gen. It is very pointless, but semantically I think it looks better (Sarinsa) */
    private static void register( BootstapContext<DamageType> context, ResourceKey<DamageType> damageTypeKey, DamageType damageType ) {
        context.register( damageTypeKey, damageType );
    }
    
    private static String msg( String name ) {
        return DeadlyWorld.MOD_ID + "." + name;
    }
}