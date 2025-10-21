package fathertoast.deadlyworld.common.block.misc;

import fathertoast.deadlyworld.common.config.dimension.ChestConfig;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public enum ChestType {
    
    SIMPLE( "simple", ( dimConfig ) -> dimConfig.CHESTS.SIMPLE ),
    VALUABLE( "valuable", ( dimConfig ) -> dimConfig.CHESTS.VALUABLE ),
    TNT_TRAP( "tnt_trap", ( dimConfig ) -> dimConfig.CHESTS.TNT_TRAP ),
    INFESTED( "infested", ( dimConfig ) -> dimConfig.CHESTS.INFESTED ),
    SURPRISE( "surprise", ( dimConfig ) -> dimConfig.CHESTS.SURPRISE );
    
    private final String id;
    private final String displayName;
    /** A function that returns the feature config associated with this chest type for a given dimension config. */
    private final Function<DimensionConfigGroup, ChestConfig.ChestTypeCategory> configFunction;
    
    
    ChestType( String id, Function<DimensionConfigGroup, ChestConfig.ChestTypeCategory> configFunction ) {
        this( id, id.replace( "_", " " ) + " chests", configFunction );
    }
    
    ChestType( String id, String displayName, Function<DimensionConfigGroup, ChestConfig.ChestTypeCategory> configFunction ) {
        this.id = id;
        this.displayName = displayName;
        this.configFunction = configFunction;
    }
    
    public String getDisplayName() { return displayName; }
    
    public ResourceLocation getChestLootTable() { return DeadlyWorld.resourceLoc( References.CHEST_LOOT_PATH + this ); }
    
    public final ChestConfig.ChestTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configFunction.apply( dimConfigs ); }
    
    @Override
    public String toString() { return id; }
    
    public static ChestType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to fetch invalid chest type from index '{}'", index );
            return SIMPLE;
        }
        return values()[index];
    }
}