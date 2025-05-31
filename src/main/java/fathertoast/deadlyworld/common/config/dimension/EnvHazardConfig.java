package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.RegistryEntryValueListField;
import fathertoast.crust.api.config.common.value.RegistryEntryValueList;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.config.Config;
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

    public final BuriedLiquidsCategory BURIED_LIQUIDS;


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

        BURIED_LIQUIDS = new BuriedLiquidsCategory( this, "buried_liquids" );
    }

    public static class BuriedLiquidsCategory extends AbstractConfigCategory<EnvHazardConfig> {

        public final RegistryEntryValueListField<Block> buriedLiquids;


        public BuriedLiquidsCategory( EnvHazardConfig parent, String categoryName ) {
            super( parent, categoryName, "Settings related to buried liquids in the world; single fluid source blocks of varying " +
                    "kinds that generate in the ground and not exposed to air." );

            SPEC.describeRegistryEntryValueList();
            SPEC.newLine();

            buriedLiquids = SPEC.define( new RegistryEntryValueListField<>( "buried_liquids",
                    new RegistryEntryValueList<>( null, () -> ForgeRegistries.BLOCKS, defaultBuriedLiquids() )
                            .setMultiValue( 3 ),
                    "This list defines buried liquid entries. Each entry consists of the following in the given order:",
                    "Liquid source block registry ID - for example \"minecraft:water\" or \"minecraft:lava\".",
                    "Min height - the lowest Y coordinate this liquid can generate at.",
                    "Max height - the highest Y coordinate this liquid can generate at.",
                    "Placements - the amount of times the world generator will try to place this liquid in a chunk.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }

        @SuppressWarnings( "ConstantConditions" )
        private List<RegistryValueEntry<Block>> defaultBuriedLiquids(  ) {
            if ( Level.NETHER.equals( PARENT.DIMENSION_CONFIGS.DIMENSION ) ) {
                return List.of(
                    new RegistryValueEntry<>( ForgeRegistries.BLOCKS.getKey( Blocks.LAVA ), DEPTH_NETHER_VOID, DEPTH_NETHER_CEIL, 10 )
                );
            }
            return List.of(
                    new RegistryValueEntry<>( ForgeRegistries.BLOCKS.getKey( Blocks.WATER ), DEPTH_4, DEPTH_0, 20 ),
                    new RegistryValueEntry<>( ForgeRegistries.BLOCKS.getKey( Blocks.LAVA ), DEPTH_VOID, DEPTH_3, 10 )
            );
        }
    }
}
