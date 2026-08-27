package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.StringField;
import fathertoast.crust.api.config.common.field.collection.BlockStateValueListField;
import fathertoast.crust.api.config.common.field.collection.RegistryValueListField;
import fathertoast.crust.api.config.common.value.collection.BlockStateValueList;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.value.BuriedBlockStats;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import static fathertoast.deadlyworld.common.util.References.*;

public class EnvHazardConfig extends AbstractConfigFile {
    
    /** The parent group containing this feature config. */
    public final DimensionConfigGroup DIMENSION_CONFIGS;
    
    public final BuriedBlocksCategory BURIED_BLOCKS;
    
    
    public EnvHazardConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir + ConfigUtil.noSpaces( "environmental hazards" ), false,
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
        
        public final BlockStateValueListField<BuriedBlockStats> list;
        
        public final StringField chestLootTable;
        
        public BuriedBlocksCategory( EnvHazardConfig parent, String categoryName ) {
            super( parent, categoryName, "Settings related to buried blocks in the world; single-block " +
                    "'ore veins' of varying kinds that generate in the ground, not exposed to air." );
            
            RegistryValueListField.describe( SPEC );
            SPEC.newLine();
            
            list = SPEC.define( new BlockStateValueListField<>( "list", defaultBuriedBlocks(),
                    "This list defines buried block entries. Each entry consists of the following in the given order:",
                    "Block ID - for example \"" + ForgeRegistries.BLOCKS.getKey( Blocks.WATER ) +
                            "\" or \"" + ForgeRegistries.BLOCKS.getKey( Blocks.CACTUS ) + "\".",
                    "Min height - the lowest Y coordinate this block can generate at.",
                    "Max height - the highest Y coordinate this block can generate at.",
                    "Placements - the amount of times the world generator will try to place this block in a chunk.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            
            SPEC.newLine();
            
            chestLootTable = SPEC.define( new StringField( "chest_loot_table", defaultChestLootTable().toString(),
                    "The loot table to apply to any 'buried' chest blocks generated in this dimension.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
        
        private ResourceLocation defaultChestLootTable() {
            if( PARENT.isNetherDimension() ) return defaultChestLootTable( Level.NETHER );
            if( PARENT.isEndDimension() ) return defaultChestLootTable( Level.END );
            // For the overworld, as well as any dimensions added by mods
            return defaultChestLootTable( Level.OVERWORLD );
        }
        
        public static ResourceLocation defaultChestLootTable( ResourceKey<Level> dimension ) {
            return DeadlyWorld.rl( References.CHEST_LOOT_PATH + dimension.location().getPath() + "_buried" );
        }
        
        private BlockStateValueList<BuriedBlockStats> defaultBuriedBlocks() {
            var builder = new BlockStateValueList.Builder<>( BuriedBlockStats.CODEC );
            if( PARENT.isNetherDimension() ) {
                return builder
                        .put( Blocks.CHEST, BuriedBlockStats.of( DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 5 ) )
                        .put( DWBlocks.INACTIVE_BURIED_SPAWNER, BuriedBlockStats.of( DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 10 ) )
                        .put( Blocks.LAVA, BuriedBlockStats.of( DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 10 ) )
                        .build();
            }
            if( PARENT.isEndDimension() ) {
                return builder
                        .put( Blocks.CHEST, BuriedBlockStats.of( DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 5 ) )
                        .put( DWBlocks.INACTIVE_BURIED_SPAWNER, BuriedBlockStats.of( DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 10 ) )
                        .put( DWBlocks.RUNNY_LAVA, BuriedBlockStats.of( DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 10 ) )
                        .build();
            }
            // For the overworld, as well as any dimensions added by mods
            return builder
                    .put( Blocks.CHEST, BuriedBlockStats.of( DEPTH_LAVA, DEPTH_1, 2.5 ) )
                    .put( DWBlocks.INACTIVE_BURIED_SPAWNER, BuriedBlockStats.of( DEPTH_LAVA, DEPTH_3, 5 ) )
                    .put( Blocks.WATER, BuriedBlockStats.of( DEPTH_4, DEPTH_SEA_LEVEL + 14, 8 ) )
                    .put( Blocks.LAVA, BuriedBlockStats.of( DEPTH_VOID, DEPTH_2, 5 ) )
                    .put( DWBlocks.RUNNY_LAVA, BuriedBlockStats.of( DEPTH_VOID, DEPTH_4, 1 ) )
                    .build();
        }
    }
}