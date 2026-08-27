package fathertoast.deadlyworld.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class DWRegistries {
    
    /** Deadly World's mod ID. */
    public static final String MOD_ID = "deadlyworld";
    
    /**
     * The forge registry for Deadly World fishing pranks.
     * <br><br>
     * Note that any new pranks registered will not be picked in-game
     * unless a new entry is added in the Deadly World fishing pranks config.
     */
    public static Supplier<IForgeRegistry<IFishingPrank>> FISHING_PRANKS_REGISTRY;
    /** The registry key pointing to the fishing pranks registry. */
    public static final ResourceKey<Registry<IFishingPrank>> FISHING_PRANKS_REG_KEY = ResourceKey.createRegistryKey( id( "fishing_pranks" ) );
    
    /** The forge registry for decoy types. */
    public static Supplier<IForgeRegistry<DecoyType>> DECOY_TYPE_REGISTRY;
    /** The registry key pointing to the decoy type registry. */
    public static final ResourceKey<Registry<DecoyType>> DECOY_TYPE_REG_KEY = ResourceKey.createRegistryKey( id( "decoy_types" ) );
    
    
    /** @return A ResourceLocation under the Deadly World namespace with the given path. */
    private static ResourceLocation id( String path ) {
        return ResourceLocation.fromNamespaceAndPath( MOD_ID, path );
    }
    
    private DWRegistries() { }
}
