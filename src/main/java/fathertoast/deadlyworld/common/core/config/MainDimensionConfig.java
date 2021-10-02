package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.field.BlockListField;
import fathertoast.deadlyworld.common.core.config.field.BooleanField;
import fathertoast.deadlyworld.common.core.config.field.DoubleField;
import fathertoast.deadlyworld.common.core.config.field.EntityListField;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.common.core.config.util.BlockList;
import fathertoast.deadlyworld.common.core.config.util.EntityEntry;
import fathertoast.deadlyworld.common.core.config.util.EntityList;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.io.File;

public class MainDimensionConfig extends Config.AbstractConfig {
    /** The parent group containing this config. */
    public final DimensionConfigGroup DIMENSION_CONFIGS;
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    MainDimensionConfig( File dir, DimensionConfigGroup dimConfigs ) {
        super( dir, "_main",
                "This config contains general options that apply to the entire",
                dimConfigs.longDimensionName() + ".",
                "This includes a master on/off setting for world gen in this dimension."
        );
        DIMENSION_CONFIGS = dimConfigs;
        
        SPEC.newLine();
        SPEC.describeEntityList();
        SPEC.newLine();
        SPEC.describeBlockList();
        
        GENERAL = new General( SPEC, dimConfigs );
    }
    
    public static class General extends Config.AbstractCategory {
        
        public final BooleanField enabled;
        
        General( ToastConfigSpec parent, DimensionConfigGroup dimConfigs ) {
            super( parent, "general",
                    "Options that apply to all world generation in the " + dimConfigs.dimensionName() + "." );
            
            enabled = SPEC.define( new BooleanField( "enabled_for_dimension", true,
                    "If true, the mod's world generation will be enabled for the " + dimConfigs.dimensionName() + ".",
                    "Set this to false to prevent Deadly World from doing anything at all in this dimension.",
                    "The other configs for this dimension will still apply for Deadly World objects placed in this dimension by,",
                    "other means, such as creative mode players, commands, other mods, etc." ) );
        }
    }
}