package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.RegistryEntryValueListField;
import fathertoast.crust.api.config.common.value.RegistryEntryValueList;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static fathertoast.deadlyworld.common.util.References.*;

public class EnvHazardConfig extends AbstractConfigFile {
    
    /** The parent group containing this feature config. */
    public final DimensionConfigGroup DIMENSION_CONFIGS;
    
    public final BuriedBlocksCategory BURIED_BLOCKS;
    
    
    public EnvHazardConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir + ConfigUtil.noSpaces( "environmental hazards" ),
                "This config contains options for misc environmental hazard features specific to the " +
                        dimConfigs.longDimensionName() + "." );
        DIMENSION_CONFIGS = dimConfigs;
        
        if( Level.OVERWORLD.equals( dimConfigs.DIMENSION ) ) {
            SPEC.decreaseIndent();
            SPEC.newLine();
            SPEC.comment( "This config also functions as the default settings for environmental hazard features in any extra " +
                    "dimensions that do not have world gen configs (all dimensions not included in the \"" +
                    Config.MAIN.GENERAL.extraDimensions.getKey() + "\" list within the mod's main config file, \"" +
                    Config.MAIN.SPEC.NAME + "\")." );
            SPEC.increaseIndent();
        }
        
        BURIED_BLOCKS = new BuriedBlocksCategory( this, "buried_blocks" );
    }
    
    /** @return True if this config is for the overworld dimension. */
    protected boolean isOverworldDimension() { return Level.OVERWORLD.equals( DIMENSION_CONFIGS.DIMENSION ); }
    
    /** @return True if this config is for the Nether dimension. */
    protected boolean isNetherDimension() { return Level.NETHER.equals( DIMENSION_CONFIGS.DIMENSION ); }
    
    /** @return True if this config is for the End dimension. */
    protected boolean isEndDimension() { return Level.END.equals( DIMENSION_CONFIGS.DIMENSION ); }
    
    public static class BuriedBlocksCategory extends AbstractConfigCategory<EnvHazardConfig> {
        
        public final RegistryEntryValueListField<Block> list;
        
        
        public BuriedBlocksCategory( EnvHazardConfig parent, String categoryName ) {
            super( parent, categoryName, "Settings related to buried blocks in the world; single-block " +
                    "'ore veins' of varying kinds that generate in the ground, not exposed to air." );
            
            SPEC.describeRegistryEntryValueList();
            SPEC.newLine();
            
            list = SPEC.define( new RegistryEntryValueListField<>( "list",
                    new RegistryEntryValueList<>( null, () -> ForgeRegistries.BLOCKS, defaultBuriedBlocks() )
                            .setMultiValue( 3 ),
                    "This list defines buried block entries. Each entry consists of the following in the given order:",
                    "Block ID - for example \"" + ForgeRegistries.BLOCKS.getKey( Blocks.WATER ) +
                            "\" or \"" + ForgeRegistries.BLOCKS.getKey( Blocks.CACTUS ) + "\".",
                    "Min height - the lowest Y coordinate this block can generate at.",
                    "Max height - the highest Y coordinate this block can generate at.",
                    "Placements - the amount of times the world generator will try to place this block in a chunk.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
        
        @SuppressWarnings( "ConstantConditions" )
        private List<RegistryValueEntry<Block>> defaultBuriedBlocks() {
            if( PARENT.isNetherDimension() ) {
                return List.of(
                        new RegistryValueEntry<>( DWBlocks.INACTIVE_BURIED_SPAWNER.getId(), DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 12 ),
                        new RegistryValueEntry<>( ForgeRegistries.BLOCKS.getKey( Blocks.LAVA ), DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 10 )
                );
            }
            if( PARENT.isEndDimension() ) {
                return List.of(
                        new RegistryValueEntry<>( DWBlocks.INACTIVE_BURIED_SPAWNER.getId(), DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 12 ),
                        new RegistryValueEntry<>( DWBlocks.RUNNY_LAVA.getId(), DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 10 )
                );
            }
            // For the overworld, as well as any dimensions added by mods
            return List.of(
                    new RegistryValueEntry<>( DWBlocks.INACTIVE_BURIED_SPAWNER.getId(), DEPTH_LAVA, DEPTH_3, 5 ),
                    new RegistryValueEntry<>( ForgeRegistries.BLOCKS.getKey( Blocks.WATER ), DEPTH_4, DEPTH_SEA_LEVEL + 14, 10 ),
                    new RegistryValueEntry<>( ForgeRegistries.BLOCKS.getKey( Blocks.LAVA ), DEPTH_VOID, DEPTH_2, 5 ),
                    new RegistryValueEntry<>( DWBlocks.RUNNY_LAVA.getId(), DEPTH_VOID, DEPTH_4, 1 )
            );
        }
    }
}