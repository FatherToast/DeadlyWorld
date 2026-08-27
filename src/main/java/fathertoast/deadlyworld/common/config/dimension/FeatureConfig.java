package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import static fathertoast.deadlyworld.common.util.References.DEPTH_NETHER_CEIL;
import static fathertoast.deadlyworld.common.util.References.DEPTH_NETHER_LAVA;

/**
 * A config file for one set of features (e.g. spawners). Establishes framework and config options used by all features -
 * namely, common feature placement settings.
 */
public abstract class FeatureConfig extends AbstractConfigFile {
    /** The name of this feature (e.g. "spawners"). */
    final String FEATURE_NAME;
    
    /** Set to true if this config should default all placements to 0 for whatever reason. */
    boolean DISABLED;
    
    /** The parent group containing this feature config. */
    public final DimensionConfigGroup DIMENSION_CONFIGS;
    
    FeatureConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs, String name ) {
        super( manager, dir + ConfigUtil.noSpaces( name + "s" ), false,
                "This config contains options for all " + name + " features specific to the " +
                        dimConfigs.longDimensionName() + "." );
        DIMENSION_CONFIGS = dimConfigs;
        FEATURE_NAME = name + "s";
        
        if( Level.OVERWORLD.equals( dimConfigs.DIMENSION ) ) {
            SPEC.decreaseIndent();
            SPEC.newLine();
            SPEC.comment( "This config also functions as the default settings for " + name + " features in any extra " +
                    "dimensions that do not have world gen configs (all dimensions not included in the \"" +
                    Config.MAIN.GENERAL.extraDimensions.getKey() + "\" list within the mod's main config file, \"" +
                    Config.MAIN.SPEC.NAME + "\")." );
            SPEC.increaseIndent();
        }
    }
    
    /**
     * If this config is for a dimension that has no water (that we know of) to generate water features in,
     * sets all default placements to 0 and adds a comment to explain.
     */
    protected void flagAsWaterFeature() {
        if( Level.NETHER.equals( DIMENSION_CONFIGS.DIMENSION ) || Level.END.equals( DIMENSION_CONFIGS.DIMENSION ) ) {
            DISABLED = true;
            
            SPEC.decreaseIndent();
            SPEC.newLine();
            SPEC.titledComment( ChatFormatting.RED + "! WARNING !",
                    "This config basically does nothing because the " + DIMENSION_CONFIGS.dimensionName() +
                            " does not have any naturally generating water for " + FEATURE_NAME + " to generate in." );
            SPEC.increaseIndent();
        }
    }
    
    /** @return True if this config is for the overworld dimension. */
    protected boolean isOverworldDimension() { return Level.OVERWORLD.equals( DIMENSION_CONFIGS.DIMENSION ); }
    
    /** @return True if this config is for the Nether dimension. */
    protected boolean isNetherDimension() { return Level.NETHER.equals( DIMENSION_CONFIGS.DIMENSION ); }
    
    /** @return True if this config is for the End dimension. */
    protected boolean isEndDimension() { return Level.END.equals( DIMENSION_CONFIGS.DIMENSION ); }
    
    
    public static class FeatureTypeCategory extends AbstractConfigCategory<FeatureConfig> {
        /** The name of this feature type (e.g. "simple spawners"). */
        final String FEATURE_TYPE_NAME;
        
        @Nullable
        public final BooleanField debugMarker;
        
        @Nullable
        public final DoubleField countPerChunk;
        
        @Nullable
        public final IntField.RandomRange heights;
        
        /**
         * Creates a new feature or subfeature category.
         * For features, this creates three config options, so begin subclass constructors by entering a new line in the spec.
         * For subfeatures, this strictly sets up the base category, so do NOT start with a new line.
         */
        FeatureTypeCategory( FeatureConfig parent, String name,
                             double placements, int minHeight, int maxHeight ) {
            super( parent, ConfigUtil.noSpaces( name + "_" + parent.FEATURE_NAME ),
                    "Options to customize " + name + " " + parent.FEATURE_NAME + " specific to the " +
                            parent.DIMENSION_CONFIGS.longDimensionName() + "." );
            FEATURE_TYPE_NAME = name + " " + parent.FEATURE_NAME;
            
            if( isSubfeature() ) {
                debugMarker = null;
                countPerChunk = null;
                heights = null;
            }
            else {
                boolean isNether = isNetherDimension();
                
                debugMarker = SPEC.define( new BooleanField( "testing_markers", false,
                        "When set to true, places a 1x1 column of glass to the height limit from a few " +
                                "blocks above each generated " + FEATURE_TYPE_NAME + ". This is game-breaking and " +
                                "laggy. Also prints a message to the console.",
                        "Consider using a tool to strip away all stone/dirt/etc. or xray via spectator mode after " +
                                "world gen for more intensive testing.",
                        DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
                
                SPEC.newLine();
                
                countPerChunk = SPEC.define( new DoubleField( "placements",
                        parent.DISABLED ? 0.0 : placements, DoubleField.Range.NON_NEGATIVE,
                        "The number of placement attempts per chunk (16x16 blocks) for " + FEATURE_TYPE_NAME + ". " +
                                "A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt).",
                        DimensionConfigHelper.MESSAGE_PLACED_FEATURE_OVERRIDE ) );
                
                SPEC.newLine();
                
                heights = new IntField.RandomRange( SPEC, "height",
                        isNether ? DEPTH_NETHER_LAVA : minHeight,
                        isNether ? DEPTH_NETHER_CEIL : maxHeight,
                        IntField.Range.ANY,
                        "The minimum and maximum (inclusive) heights/y-values " + FEATURE_TYPE_NAME + " can generate at.",
                        DimensionConfigHelper.MESSAGE_PLACED_FEATURE_OVERRIDE );
            }
        }
        
        /** @return True if this config is for a subfeature. */
        public final boolean isSubfeature() { return this instanceof SubfeatureCategory; }
        
        /** @return True if this config is for the overworld dimension. */
        protected boolean isOverworldDimension() { return PARENT.isOverworldDimension(); }
        
        /** @return True if this config is for the Nether dimension. */
        protected boolean isNetherDimension() { return PARENT.isNetherDimension(); }
        
        /** @return True if this config is for the End dimension. */
        protected boolean isEndDimension() { return PARENT.isEndDimension(); }
        
        // Helper methods for commonly used fields below
        
        protected IntField standardActivationRangeIntField( int defaultValue ) {
            return new IntField( "required_player_range", defaultValue, 0, Short.MAX_VALUE,
                    "These " + PARENT.FEATURE_NAME + " are active as long as a player is within this distance (spherical distance).",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
        }
        
        protected DoubleField standardActivationRangeField( double defaultValue ) {
            return new DoubleField( "required_player_range", defaultValue, DoubleField.Range.NON_NEGATIVE,
                    "These " + PARENT.FEATURE_NAME + " are active as long as a player is within this distance (spherical distance).",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
        }
        
        protected DoubleField standardCheckSightField( boolean defaultValue ) {
            return new DoubleField( "sight_check_chance", defaultValue ? 1.0 : 0.0, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to generate as requiring a sight check.",
                    "When the sight check is enabled, " + FEATURE_TYPE_NAME + " will only activate when they have " +
                            "direct line-of-sight to a player within activation range. Their delays will continue to " +
                            "tick down, but they will wait to actually activate until they have line-of-sight.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
        }
    }
    
    /**
     * A config feature category that represents a subfeature.
     * <p>
     * Any feature category implementing this interface should have all placement-sensitive config options stripped,
     * since these options will be handled by the primary feature.
     */
    public interface SubfeatureCategory {}
}