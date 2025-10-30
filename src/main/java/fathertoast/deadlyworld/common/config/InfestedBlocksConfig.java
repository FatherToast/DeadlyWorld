package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.RegistryEntryList;
import fathertoast.deadlyworld.common.core.registry.BlockAutoGen;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InfestedBlocksConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final AutoGen AUTO_GEN;
    
    /** Builds the config spec that should be used for this config. */
    InfestedBlocksConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options to customize auto-generated infested blocks, including " +
                        "which to generate and how they behave."
        );
        
        GENERAL = new General( this );
        AUTO_GEN = new AutoGen( this );
    }
    
    public static class General extends AbstractConfigCategory<InfestedBlocksConfig> {
        
        public final RegistryEntryListField<Item> cleanseTools;
        public final IntField cleanseDamage;
        public final BooleanField cleanseSpawnsSilverfish;
        
        General( InfestedBlocksConfig parent ) {
            super( parent, "general",
                    "Options that apply to infested blocks as a whole " +
                            "(not just Deadly World's infested blocks)." );
            
            cleanseTools = SPEC.define( new LazyRegistryEntryListField<>( "cleanse.tools",
                    new RegistryEntryList<>( ForgeRegistries.ITEMS, null, List.of( ItemTags.SHOVELS ) ),
                    "A list of items that can interact with infested blocks to revert them to their host " +
                            "block. This will release the silverfish infesting it and damage the tool, depending on " +
                            "the settings below.",
                    "For reference, the standard tool tags are: " +
                            TomlHelper.literalList( new RegistryEntryList<>( ForgeRegistries.ITEMS, null, List.of(
                                    ItemTags.SWORDS, ItemTags.AXES, ItemTags.HOES, ItemTags.PICKAXES, ItemTags.SHOVELS, ItemTags.TOOLS
                            ) ).toStringList() ) + "." ) );
            cleanseDamage = SPEC.define( new IntField( "cleanse.tool_damage", 2, IntField.Range.NON_NEGATIVE,
                    "The amount of damage tools take when successfully cleansing an infested block." ) );
            cleanseSpawnsSilverfish = SPEC.define( new BooleanField( "cleanse.spawns_silverfish", true,
                    "If true, cleansing an infested block spawns a silverfish." ) );
        }
    }
    
    public static class AutoGen extends AbstractConfigCategory<InfestedBlocksConfig> {
        
        public final PredicateStringListField hostBlocks;
        public final InjectionWrapperField<StringField> fallbackBlock;
        
        public final EnumField<BlockAutoGen.NameStyle> nameStyle;
        
        public final DoubleField breakSpeedMulti;
        public final DoubleField explosionResistMulti;
        
        public final DoubleField aggressiveChance;
        
        public final DoubleField projBreakChance;
        public final DoubleField stepBreakChance;
        
        AutoGen( InfestedBlocksConfig parent ) {
            super( parent, "auto_generated_blocks",
                    "Options that apply to automatic generation of this Deadly World's infested " +
                            "blocks, as well as their behavior." );
            
            hostBlocks = SPEC.define( new PredicateStringListField( "host_blocks", "namespace:block_name",
                    buildDefaultSilverfishBlocks(), ResourceLocation::isValidResourceLocation,
                    "A list of blocks to generate an \"infested\" version for. The infested version of a block " +
                            "looks identical, but has modified behavior (see below) and spawns a silverfish when broken.",
                    "Only blocks that are solid, full cubes with no block entity should be put on this list.",
                    "All hosts for vanilla infested blocks are included here by default; this overrides the vanilla block in most cases.",
                    "If any mod-added blocks on this list are not loaded by the time Deadly World loads its blocks, the " +
                            "game will crash (see \"dependencies\" below).",
                    "To connect to a server, this setting must be the same on both client and server or your connection " +
                            "will be refused for failing to synchronize registry data."
            ), RestartNote.GAME );
            fallbackBlock = SPEC.define( new InjectionWrapperField<>( new StringField( "fallback_block", keyToString( Blocks.INFESTED_STONE ),
                    "The vanilla fallback block to replace missing infested blocks with. If the \"host_blocks\" " +
                            "list is changed and you load into a world that used to have infested blocks that no longer " +
                            "exist, they will be replaced with this block."
            ), this::checkFallbackBlock ) );
            
            SPEC.newLine();
            
            nameStyle = SPEC.define( new EnumField<>( "name_style", BlockAutoGen.NameStyle.SUSPICIOUS,
                    "The style to use for infested blocks' display names.",
                    "The available styles are:",
                    " * " + TomlHelper.enumToString( BlockAutoGen.NameStyle.VANILLA ) + ": Follows the vanilla name pattern (Dirt -> Infested Dirt)",
                    " * " + TomlHelper.enumToString( BlockAutoGen.NameStyle.SUSPICIOUS ) + ": Puts the host name in quotes (Dirt -> \"Dirt\")",
                    " * " + TomlHelper.enumToString( BlockAutoGen.NameStyle.IDENTITY ) + ": Directly uses the host name (Dirt -> Dirt)",
                    "Note: If you are using Jade, by default its block tooltip will not reveal \"infested\" blocks in " +
                            "survival mode, so this setting has little effect. Set \"builtinCamouflage\" to false in " +
                            "'jade.json' if you wish to see this name style in its tooltips."
            ) );
            
            SPEC.newLine();
            
            breakSpeedMulti = SPEC.define( new DoubleField( "break_speed_multi", 2.0, DoubleField.Range.NON_NEGATIVE,
                    "Break speed multiplier for infested blocks. A value of 0 makes them unbreakable, " +
                            "while something really high like 3.4E38 makes them break instantly.",
                    "Base break speed of infested blocks is double the host block's break speed, but is unaffected by tools."
            ) );
            explosionResistMulti = SPEC.define( new DoubleField( "explosion_resistance", 0.75, DoubleField.Range.NON_NEGATIVE,
                    "Explosion resistance for infested blocks.",
                    "For reference, some vanilla block explosion resistances are: Dirt = 0.5, Stone = 6, Obsidian = 1200"
            ) );
            
            SPEC.newLine();
            
            aggressiveChance = SPEC.define( new DoubleField( "aggressive_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance for silverfish to immediately start calling for reinforcements when they " +
                            "spawn from an infested block being broken. Does not trigger for silverfish spawned by " +
                            "'cleansing', but can trigger from silverfish summoned by this effect. With a high enough " +
                            "chance, silverfish tend to chain this effect to pop all infested blocks."
            ) );
            
            SPEC.newLine();
            
            projBreakChance = SPEC.define( new DoubleField( "break_chance.projectile", 0.3, DoubleField.Range.PERCENT,
                    "The chance for infested blocks to break when hit by a projectile."
            ) );
            stepBreakChance = SPEC.define( new DoubleField( "break_chance.step", 0.01, DoubleField.Range.PERCENT,
                    "The chance for infested blocks to break when stepped on by a player. This chance " +
                            "is rolled each tick (20 times per second) while standing on an infested block, so it " +
                            "should probably be kept pretty low." // what are you doing, step break chance?
            ) );
        }
        
        /** Logs a warning if the fallback block field value is invalid. */
        private void checkFallbackBlock( StringField field ) {
            String value = field.get();
            if( !ResourceLocation.isValidResourceLocation( value )
                    || !ForgeRegistries.BLOCKS.containsKey( ResourceLocation.parse( value ) ) )
                DeadlyWorld.LOG.warn( "\"{}\" contains an invalid ID that is either malformed or doesn't exist in the block registry! Value: {}",
                        field.getKey(), value );
        }
        
        /**
         * @return The fallback block to use for missing mappings.
         * If the configured fallback is invalid, we fall back
         * to {@link Blocks#INFESTED_STONE}.
         */
        public Block getFallbackBlock() {
            Block fallbackFallback = Blocks.INFESTED_STONE; // Lol
            
            ResourceLocation id = ResourceLocation.tryParse( fallbackBlock.field().get() );
            
            if( id == null ) return fallbackFallback;
            if( !ForgeRegistries.BLOCKS.containsKey( id ) ) return fallbackFallback;
            
            // noinspection ConstantConditions
            return ForgeRegistries.BLOCKS.getValue( id );
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
            blocks.forEach( ( block ) -> strings.add( keyToString( block ) ) );
            return strings;
        }
    }
    
    private static String keyToString( Block block ) {
        return Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( block ) ).toString();
    }
}