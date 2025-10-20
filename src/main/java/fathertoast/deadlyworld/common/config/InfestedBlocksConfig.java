package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.RegistryEntryList;
import fathertoast.deadlyworld.common.block.infested.NameStyle;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class InfestedBlocksConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    InfestedBlocksConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options to customize auto-generated infested blocks, including " +
                        "which to generate and how they behave."
        );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<InfestedBlocksConfig> {
        
        public final StringListField hostBlocks;
        public final StringListField dependencies;
        
        public final EnumField<NameStyle> nameStyle;
        
        public final RegistryEntryListField<Item> cleanseTools;
        public final IntField cleanseDamage;
        public final BooleanField cleanseSpawnsSilverfish;
        
        General( InfestedBlocksConfig parent ) {
            super( parent, "general",
                    "Options that apply to this mod's auto-generated infested blocks as a whole." );
            
            hostBlocks = SPEC.define( new StringListField( "host_blocks", "namespace:block_name",
                    buildDefaultSilverfishBlocks(),
                    "A list of blocks to generate an \"infested\" version for. The infested version of a block " +
                            "looks identical, but has modified physical properties and spawns a silverfish when broken.",
                    "All hosts for vanilla infested blocks are included here by default; this overrides the vanilla block in most cases.",
                    "Only blocks that are solid, full cubes with no block entity should be put on this list.",
                    "If any mod-added blocks on this list are not loaded by the time Deadly World loads its blocks, the " +
                            "game will crash (see setting below)."
            ), RestartNote.GAME );
            dependencies = SPEC.define( new StringListField( "dependencies", "mod_id",
                    new ArrayList<>(),
                    "By default (that is, when this list is empty), Deadly World will attempt to adjust load " +
                            "order such that it loads its blocks after all namespaces used in the \"host_blocks\" list " +
                            "and any blocks with a namespace that not equal to a loaded mod's id will be skipped.",
                    "If you enter any ids in this list, instead Deadly World will attempt to adjust load order after " +
                            "only the mods on this list and will not skip any blocks on the above list.",
                    "All load order adjustment is disabled if you only enter \"minecraft\" (or non-existent mods) in " +
                            "this list, if you prefer to just crash instead of mucking with load order mid-loading."
            ), RestartNote.GAME );
            
            SPEC.newLine();
            
            nameStyle = SPEC.define( new EnumField<>( "name_style", NameStyle.SUSPICIOUS,
                    "The style to use for infested blocks' display names.",
                    "The available styles are:",
                    " * " + TomlHelper.enumToString( NameStyle.VANILLA ) + ": Follows the vanilla name pattern (Dirt -> Infested Dirt)",
                    " * " + TomlHelper.enumToString( NameStyle.SUSPICIOUS ) + ": Puts the host name in quotes (Dirt -> \"Dirt\")",
                    " * " + TomlHelper.enumToString( NameStyle.IDENTITY ) + ": Directly uses the host name (Dirt -> Dirt)",
                    "Note: If you are using Jade, by default its block tooltip will not reveal \"infested\" blocks in " +
                            "survival mode, so this setting has little effect. You may set \"builtinCamouflage\" to false " +
                            "in 'jade.json' if you wish to see this name style in tooltips."
            ) );
            //TODO Jade compat setting to show host block's mod instead of Deadly World in block tooltips
            
            SPEC.newLine();
            
            cleanseTools = SPEC.define( new LazyRegistryEntryListField<>( "cleanse.tools",
                    new RegistryEntryList<>( ForgeRegistries.ITEMS, null, List.of( ItemTags.SHOVELS ) ),
                    "A list of items that can interact with infested blocks (including the vanilla ones) to " +
                            "revert them to their host block. This will release the silverfish infesting it and damage " +
                            "the tool, depending on the settings below.",
                    "For reference, the standard tool tags are: " +
                            TomlHelper.literalList( new RegistryEntryList<>( ForgeRegistries.ITEMS, null, List.of(
                                    ItemTags.SWORDS, ItemTags.AXES, ItemTags.HOES, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.TOOLS
                            ) ).toStringList() ) + "." ) );
            cleanseDamage = SPEC.define( new IntField( "cleanse.tool_damage", 2, IntField.Range.NON_NEGATIVE,
                    "The amount of damage tools take when successfully cleansing an infested block." ) );
            cleanseSpawnsSilverfish = SPEC.define( new BooleanField( "cleanse.spawns_silverfish", true,
                    "If true, cleansing an infested block spawns a silverfish." ) );
        }
        
        private List<String> buildDefaultSilverfishBlocks() {
            List<Block> blocks = List.of(
                    // Vanilla infested block 'overrides'
                    Blocks.STONE, Blocks.COBBLESTONE, Blocks.DEEPSLATE, Blocks.STONE_BRICKS,
                    Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS,
                    // Overworld
                    Blocks.DIRT, Blocks.MUD, Blocks.MUD_BRICKS, Blocks.CLAY, Blocks.MOSSY_COBBLESTONE, Blocks.BOOKSHELF,
                    Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE,
                    Blocks.TUFF, Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE,
                    Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE,
                    Blocks.CALCITE, Blocks.SMOOTH_BASALT, Blocks.COBBLED_DEEPSLATE, Blocks.CHISELED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE,
                    Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS,
                    Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.DARK_PRISMARINE,
                    // Overworld ores
                    Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
                    Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
                    Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
                    Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
                    // Nether
                    Blocks.NETHERRACK, Blocks.BASALT, Blocks.POLISHED_BASALT, Blocks.SOUL_SOIL, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE,
                    Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                    Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS, Blocks.CHISELED_NETHER_BRICKS, Blocks.RED_NETHER_BRICKS,
                    Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE,
                    // The End
                    Blocks.END_STONE, Blocks.END_STONE_BRICKS, Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR
            );
            List<String> strings = new ArrayList<>();
            //noinspection ConstantConditions
            blocks.forEach( ( block ) -> strings.add( ForgeRegistries.BLOCKS.getKey( block ).toString() ) );
            return strings;
        }
    }
}