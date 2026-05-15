package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.BlockAutoGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnstableBlocksConfig extends AbstractConfigFile {
    
    public final UnstableBlocksConfig.AutoGen AUTO_GEN;
    
    /** Builds the config spec that should be used for this config. */
    UnstableBlocksConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options to customize auto-generated unstable blocks, including " +
                        "which to generate and how they behave."
        );
        AUTO_GEN = new UnstableBlocksConfig.AutoGen( this );
    }
    
    public static class AutoGen extends AbstractConfigCategory<UnstableBlocksConfig> {
        
        public final PredicateStringListField hostBlocks;
        public final InjectionWrapperField<StringField> fallbackBlock;
        
        public final EnumField<BlockAutoGen.NameStyle> nameStyle;
        
        public final DoubleField breakSpeedMulti;
        public final DoubleField explosionResistMulti;
        
        public final DoubleField projFallChance;
        public final DoubleField stepFallChance;
        
        public final IntField neighborUpdateTicks;
        
        AutoGen( UnstableBlocksConfig parent ) {
            super( parent, "auto_generated_blocks",
                    "Options that apply to automatic generation of this Deadly World's unstable " +
                            "blocks, as well as their behavior." );
            
            hostBlocks = SPEC.define( new PredicateStringListField( "host_blocks", "namespace:block_name",
                    buildDefaultUnstableBlocks(), ResourceLocation::isValidResourceLocation,
                    "A list of blocks to generate an \"unstable\" version for. The unstable version of a block " +
                            "looks identical, but has modified behavior (see below) and pops itself and other neighboring unstable blocks when stepped on.",
                    "Only blocks that are solid, full cubes with no block entity should be put on this list.",
                    "If any mod-added blocks on this list are not loaded by the time Deadly World loads its blocks, the " +
                            "game will crash (see \"block_auto_gen_dependencies\" in main config).",
                    "To connect to a server, this setting must be the same on both client and server or your connection " +
                            "will be refused for failing to synchronize registry data."
            ), RestartNote.GAME );
            fallbackBlock = SPEC.define( new InjectionWrapperField<>( new StringField( "fallback_block", keyToString( Blocks.SAND ),
                    "The vanilla fallback block to replace missing unstable blocks with. If the \"host_blocks\" " +
                            "list is changed and you load into a world that used to have unstable blocks that no longer " +
                            "exist, they will be replaced with this block."
            ), this::checkFallbackBlock ) );
            
            SPEC.newLine();
            
            nameStyle = SPEC.define( new EnumField<>( "name_style", BlockAutoGen.NameStyle.VANILLA,
                    "The style to use for unstable blocks' display names.",
                    "The available styles are:",
                    " * " + TomlHelper.enumToString( BlockAutoGen.NameStyle.VANILLA ) + ": Follows the vanilla name pattern (Dirt -> Unstable Dirt)",
                    " * " + TomlHelper.enumToString( BlockAutoGen.NameStyle.SUSPICIOUS ) + ": Puts the host name in quotes (Dirt -> \"Dirt\")",
                    " * " + TomlHelper.enumToString( BlockAutoGen.NameStyle.IDENTITY ) + ": Directly uses the host name (Dirt -> Dirt)"
            ) );
            
            SPEC.newLine();
            
            breakSpeedMulti = SPEC.define( new DoubleField( "break_speed_multi", 2.0, DoubleField.Range.NON_NEGATIVE,
                    "Break speed multiplier for unstable blocks. A value of 0 makes them unbreakable, " +
                            "while something really high like 3.4E38 makes them break instantly.",
                    "Base break speed of unstable blocks is double the host block's break speed, but is unaffected by tools."
            ) );
            explosionResistMulti = SPEC.define( new DoubleField( "explosion_resistance", 0.75, DoubleField.Range.NON_NEGATIVE,
                    "Explosion resistance for unstable blocks.",
                    "For reference, some vanilla block explosion resistances are: Dirt = 0.5, Stone = 6, Obsidian = 1200"
            ) );
            
            SPEC.newLine();
            
            projFallChance = SPEC.define( new DoubleField( "fall_chance.projectile", 0.3, DoubleField.Range.PERCENT,
                    "The chance for unstable blocks to fall when hit by a projectile."
            ) );
            stepFallChance = SPEC.define( new DoubleField( "fall_chance.step", 0.05, DoubleField.Range.PERCENT,
                    "The chance for unstable blocks to pop/break when stepped on by a player. This chance " +
                            "is rolled each tick (20 times per second) while standing on an unstable block."
            ) );
            
            SPEC.newLine();
            
            neighborUpdateTicks = SPEC.define( new IntField( "neighbor_update_tick_speed", 5, 1, 40,
                    "When an unstable block is destroyed, neighboring unstable blocks will also start to disintegrate.",
                    "This value determines how many ticks it takes before neighbors get destroyed." ) );
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
         * to {@link Blocks#SAND}.
         */
        public Block getFallbackBlock() {
            Block fallbackFallback = Blocks.SAND;
            
            ResourceLocation id = ResourceLocation.tryParse( fallbackBlock.field().get() );
            
            if( id == null ) return fallbackFallback;
            if( !ForgeRegistries.BLOCKS.containsKey( id ) ) return fallbackFallback;
            
            // noinspection ConstantConditions
            return ForgeRegistries.BLOCKS.getValue( id );
        }
        
        private List<String> buildDefaultUnstableBlocks() {
            List<Block> blocks = List.of(
                    // Overworld
                    Blocks.DIRT, Blocks.MUD, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND,
                    // Nether
                    Blocks.SOUL_SAND, Blocks.SOUL_SOIL
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