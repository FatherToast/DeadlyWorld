package fathertoast.deadlyworld.datagen.model;

import fathertoast.deadlyworld.common.block.spike_trap.BaseSpikeTrapBlock;
import fathertoast.deadlyworld.common.block.spike_trap.MechanicalSpikeTrapBlock;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.Objects;

/**
 * Base class for our blockstate/model provider.
 * Keeping convenience methods here so the
 * implementation doesn't get super bloated and insane looking.
 */
public abstract class DWAbstractModelProvider extends BlockStateProvider {

    /** Default particle texture key. */
    protected static final String PARTICLE_KEY = "particle";


    public DWAbstractModelProvider( PackOutput output, ExistingFileHelper exFileHelper ) {
        super( output, DeadlyWorld.MOD_ID, exFileHelper );
    }


    // ----------------------------------------------------- //
    //                        BLOCKS                         //
    // ----------------------------------------------------- //

    /** Generates state definition and models for simple deadly spawners. */
    protected void simpleSpawner( RegistryObject<? extends Block> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();

        simpleBlockWithItem( regObj.get(), models()
                .withExistingParent( name, "block/spawner" )
                .renderType( "cutout" )
        );
    }

    /** Generates state definition and models for mini spawner blocks. */
    protected void miniSpawner( RegistryObject<? extends Block> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        final Block block = regObj.get();
        final ResourceLocation textureLoc = blockTexture( block );

        getVariantBuilder( regObj.get() ).forAllStatesExcept( state -> {
            Direction dir = state.getValue( BlockStateProperties.FACING );
            return ConfiguredModel.builder()
                    .modelFile( models()
                            .withExistingParent( name, templateLoc( "template_mini_spawner" ) )
                            .texture( "all", textureLoc )
                            .texture( PARTICLE_KEY, textureLoc )
                    )
                    .rotationX( dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0 )
                    .rotationY( dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360 )
                    .build();
        }, BlockStateProperties.WATERLOGGED );

        itemModels().getBuilder( name ).parent( models().getExistingFile( regObj.getId() ) );
    }

    /** Generates state definition and models for simple floor traps. */
    protected void simpleFloorTrap( RegistryObject<? extends Block> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();

        simpleBlock( regObj.get(), models().getBuilder( name )
                .texture( PARTICLE_KEY, blockTexture( Blocks.COBBLESTONE ) )
        );
        itemModels().getBuilder( name ).parent( models().getExistingFile( mcLoc( "block/dropper_vertical" ) ) );
    }

    /** Generates state definition and models for simple tower dispensers. */
    protected void simpleTowerDispenser( RegistryObject<? extends Block> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();

        simpleBlockWithItem( regObj.get(), models().cubeColumn( name,
                mcLoc( "block/dispenser_front_vertical" ),
                mcLoc( "block/furnace_top" ) )
        );
    }

    /** Generates state definition and models for simple sea mines. */
    protected void simpleSeaMine( RegistryObject<? extends Block> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        final Block block = regObj.get();

        simpleBlockWithItem( regObj.get(), models()
                .withExistingParent( name, templateLoc( "template_sea_mine" ) )
                .texture( "mine", blockTexture( block ) )
                .texture( PARTICLE_KEY, blockTexture( Blocks.COBBLESTONE ) )
        );
        ModelFile chainless = models()
                .withExistingParent( name + "_chainless", templateLoc( "template_sea_mine" ) )
                .texture( "mine", blockTextureExtend( block, "_chainless" ) )
                .texture( PARTICLE_KEY, blockTexture( Blocks.COBBLESTONE ) );
        itemModels().getBuilder( name ).parent( chainless );
    }

    protected void spikeTrap( RegistryObject<? extends BaseSpikeTrapBlock> regObj,
                                    ResourceLocation baseTexture, ResourceLocation spikeTexture, ResourceLocation overlay ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        final Block block = regObj.get();

        getVariantBuilder( block ).forAllStates( (state) -> {
            Direction facing = state.getValue( MechanicalSpikeTrapBlock.FACING );
            ResourceLocation model = templateLoc( "template_spike_trap" );

            return ConfiguredModel.builder()
                    .modelFile( models()
                            .withExistingParent( name, model )
                            .texture( "base", baseTexture )
                            .texture( "overlay", overlay )
                            .texture( "spikes", spikeTexture )
                            .texture( PARTICLE_KEY, blockTexture( Blocks.STONE ) )
                    )
                    .rotationX( facing == Direction.DOWN ? 180 : facing.getAxis().isHorizontal() ? 90 : 0 )
                    .rotationY( facing.getAxis().isVertical() ? 0 : ( ((int) facing.toYRot() ) + 180 ) % 360 )
                    .build();
        });
        itemModels().getBuilder( name ).parent( models().getExistingFile( modLoc( "block/" + name ) ) );
    }

    protected void mechanicalSpikeTrap( RegistryObject<? extends BaseSpikeTrapBlock> regObj,
                              ResourceLocation baseTexture, ResourceLocation spikeTexture, ResourceLocation overlay ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        final Block block = regObj.get();

        getVariantBuilder( block ).forAllStates( (state) -> {
            boolean pressed = state.getValue( MechanicalSpikeTrapBlock.PRESSED );
            Direction facing = state.getValue( MechanicalSpikeTrapBlock.FACING );

            ResourceLocation pressedModel = templateLoc( "template_spike_trap_pressed" );
            ResourceLocation noSpikesModel = templateLoc( "template_spike_trap_spikeless" );

            if ( pressed ) {
                return ConfiguredModel.builder()
                        .modelFile(models()
                                .withExistingParent( name + "_pressed", pressedModel )
                                .texture( "base", baseTexture )
                                .texture( "overlay", overlay )
                                .texture( "spikes", spikeTexture )
                                .texture( PARTICLE_KEY, blockTexture( Blocks.STONE ) )
                        )
                        .rotationX( facing == Direction.DOWN ? 180 : facing.getAxis().isHorizontal() ? 90 : 0 )
                        .rotationY( facing.getAxis().isVertical() ? 0 : ( ((int) facing.toYRot() ) + 180 ) % 360 )
                        .build();
            }
            else {
                return ConfiguredModel.builder()
                        .modelFile( models()
                                .withExistingParent( name, noSpikesModel )
                                .texture( "base", baseTexture )
                                .texture( "overlay", overlay )
                                .texture( PARTICLE_KEY, blockTexture( Blocks.STONE ) )
                        )
                        .rotationX( facing == Direction.DOWN ? 180 : facing.getAxis().isHorizontal() ? 90 : 0 )
                        .rotationY( facing.getAxis().isVertical() ? 0 : ( ((int) facing.toYRot() ) + 180 ) % 360 )
                        .build();
            }
        });
        itemModels().getBuilder( name ).parent( models().getExistingFile( modLoc( "block/" + name + "_pressed" ) ) );
    }

    protected void spikeTrap( RegistryObject<? extends BaseSpikeTrapBlock> regObj, ResourceLocation baseTexture, ResourceLocation spikeTexture ) {
        if ( regObj.get() instanceof MechanicalSpikeTrapBlock )
            mechanicalSpikeTrap( regObj, baseTexture, spikeTexture, modBlockTexture( "regular_spike_trap_overlay" ) );
        else
            spikeTrap( regObj, baseTexture, spikeTexture, modBlockTexture( "regular_spike_trap_overlay" ) );
    }

    protected void spikeTrap( RegistryObject<? extends BaseSpikeTrapBlock> regObj, ResourceLocation baseTexture ) {
        ResourceLocation spikeTexture = modBlockTexture( "normal_spikes" );
        spikeTrap( regObj, baseTexture, spikeTexture );
    }

    protected void pitfallTrap( RegistryObject<? extends Block> regObj, Block parentBlock ) {
        ModelFile model = models().pressurePlate( regObj.getId().getPath(), blockTexture( parentBlock ) );
        simpleBlockWithItem( regObj.get(), model );
    }


    /**
     * Generates state definition and a block model for blocks
     * that have no block model data except particle texture,
     * and either doesn't have an item model or uses a special one (block-entity based usually).
     */
    protected void emptyModelWithParticle( RegistryObject<? extends Block> regObj, ResourceLocation particleTexture ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();

        ConfiguredModel model = new ConfiguredModel( models().getBuilder( name )
                .texture( PARTICLE_KEY, particleTexture ) );

        getVariantBuilder( regObj.get() ).partialState().setModels( model );
    }

    /** Helper method for getting the location of a DeadlyWorld template block model. */
    protected static ResourceLocation templateLoc( String name ) {
        return DeadlyWorld.rl( "block/template/" + name );
    }

    /** @return A block texture location of the vanilla namespace and the given path. */
    protected static ResourceLocation mcBlockTexture( String path ) {
        return ResourceLocation.withDefaultNamespace( ModelProvider.BLOCK_FOLDER + "/" + path );
    }

    /** @return A block texture location of DeadlyWorld's namespace and the given path. */
    protected static ResourceLocation modBlockTexture( String path ) {
        return DeadlyWorld.rl( ModelProvider.BLOCK_FOLDER + "/" + path );
    }

    /** @return A block texture location using the given block's ID, with an additional suffix. */
    protected static ResourceLocation blockTextureExtend( Block block, String suffix ) {
        final ResourceLocation id = Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( block ) );
        return ResourceLocation.fromNamespaceAndPath( id.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + id.getPath() + suffix );
    }


    // ----------------------------------------------------- //
    //                        ITEMS                          //
    // ----------------------------------------------------- //

    /**
     * Generates a simple item model for the given item.<br>
     * Uses the item's ID to determine the location of the texture to use.
     */
    protected void simpleItem( RegistryObject<? extends Item> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        itemModels().getBuilder( name ).parent( itemModels().getExistingFile( mcLoc( "item/generated" ) ) )
                .texture( "layer0", modItemTexture( name ) );
    }

    /**
     * Generates a simple item model for the given item.
     *
     * @param regObj     The registry object containing the item to generate a model for.
     * @param textureLoc The location of the texture to use for the model.
     */
    protected void simpleItem( RegistryObject<? extends Item> regObj, ResourceLocation textureLoc ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        itemModels().getBuilder( name ).parent( itemModels().getExistingFile( mcLoc( "item/generated" ) ) )
                .texture( "layer0", textureLoc );
    }

    /**
     * Generates a simple item model for the given item.<br>
     *
     * @param regObj The registry object containing the item to generate a model for.
     * @param parent The vanilla item to get the texture off. We naively assume the item's
     *               ID can be translated into a location of an existing texture for that item.
     */
    protected void simpleItemWithParent( RegistryObject<? extends Item> regObj, Item parent ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        final ResourceLocation parentId = Objects.requireNonNull( ForgeRegistries.ITEMS.getKey( parent ) );

        if( !parentId.getNamespace().equals( ResourceLocation.DEFAULT_NAMESPACE ) )
            throw new IllegalArgumentException( "\"simpleItemWithParent\" should not be called with a non-vanilla parent item!" );

        itemModels().getBuilder( name ).parent( itemModels().getExistingFile( mcLoc( "item/generated" ) ) )
                .texture( "layer0", mcItemTexture( parentId.getPath() ) );
    }

    /** @return An item texture location of the vanilla namespace and the given path. */
    protected static ResourceLocation mcItemTexture( String path ) {
        return ResourceLocation.withDefaultNamespace( ModelProvider.ITEM_FOLDER + "/" + path );
    }

    /** @return An item texture location of DeadlyWorld's namespace and the given path. */
    protected static ResourceLocation modItemTexture( String path ) {
        return DeadlyWorld.rl( ModelProvider.ITEM_FOLDER + "/" + path );
    }
}