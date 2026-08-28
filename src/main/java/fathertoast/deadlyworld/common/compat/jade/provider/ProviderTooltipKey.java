package fathertoast.deadlyworld.common.compat.jade.provider;

import fathertoast.deadlyworld.api.IDeadlyWorldApi;

/**
 * Holds an NBT key and a translation key related to a provider tooltip.
 * Note that the specified lang key gets modified in the constructor below.
 */
public record ProviderTooltipKey(String nbtKey, String langKey) {
    
    public ProviderTooltipKey( String nbtKey, String langKey ) {
        this.nbtKey = nbtKey;
        this.langKey = IDeadlyWorldApi.MOD_ID + ".jade.provider_element." + langKey;
    }
    
    /** @return A new instance with the given NBT key and lang key. */
    public static ProviderTooltipKey of( String nbtKey, String baseLangKey ) {
        return new ProviderTooltipKey( nbtKey, baseLangKey );
    }
}
