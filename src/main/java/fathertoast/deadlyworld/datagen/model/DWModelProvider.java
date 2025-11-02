package fathertoast.deadlyworld.datagen.model;

import fathertoast.deadlyworld.common.block.spawner.MiniSpawnerBlock;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class DWModelProvider extends DWAbstractModelProvider {
    
    public DWModelProvider( PackOutput output, ExistingFileHelper fileHelper ) {
        super( output, fileHelper );
    }
    
    @Override
    protected void registerStatesAndModels() {
        // Blocks and block-items
        DWBlocks.SPAWNERS.forEach( ( spawner ) -> {
            if( spawner.get() instanceof MiniSpawnerBlock ) { miniSpawner( spawner ); }
            else { simpleSpawner( spawner ); }
        } );
        simpleSpawner( DWBlocks.INACTIVE_BURIED_SPAWNER );
        DWBlocks.FLOOR_TRAPS.forEach( this::simpleFloorTrap );
        DWBlocks.TOWER_DISPENSERS.forEach( this::simpleTowerDispenser );
        DWBlocks.SEA_MINES.forEach( this::simpleSeaMine );
        
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.MUNDANE ), blockTexture( Blocks.COBBLESTONE ) );
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.POISON ), blockTexture( Blocks.MOSSY_COBBLESTONE ) );
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.FIERY ), blockTextureExtend( Blocks.BASALT, "_top" ) );
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.WITHERING ), blockTexture( Blocks.NETHER_BRICKS ) );
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.MECHANICAL_MUNDANE ), blockTexture( Blocks.STONE ) );
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.MECHANICAL_POISON ), blockTexture( Blocks.MOSSY_STONE_BRICKS ) );
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.MECHANICAL_FIERY ), blockTextureExtend( Blocks.POLISHED_BASALT, "_top" ) );
        spikeTrap( DWBlocks.spikeTrap( SpikeTrapType.MECHANICAL_WITHERING ), blockTexture( Blocks.RED_NETHER_BRICKS ) );
        
        emptyModelWithParticle( DWBlocks.MINI_CHEST, blockTexture( Blocks.OAK_PLANKS ) );
        emptyModelWithParticle( DWBlocks.RUNNY_LAVA, blockTextureExtend( Blocks.LAVA, "_still" ) );
        
        // Items
        simpleItem( DWItems.MIMIC_CORE );
        simpleItemWithParent( DWItems.CONTAINER_INFESTATION, Items.NETHER_WART );
        simpleItemWithParent( DWItems.CONTAINER_TRAP, Items.FIREWORK_STAR );
        simpleItemWithParent( DWItems.FEATURE_PLACER, Items.STRUCTURE_VOID );
        simpleItem( DWItems.RUNNY_LAVA_BUCKET );
        
        spawnEggs();
    }
    
    
    // ----------------------------------------------------- //
    //                        BLOCKS                         //
    // ----------------------------------------------------- //
    
    
    // ----------------------------------------------------- //
    //                        ITEMS                          //
    // ----------------------------------------------------- //
    
    private void spawnEggs() {
        final ModelFile.ExistingModelFile normalModel = itemModels().getExistingFile( mcLoc( "item/template_spawn_egg" ) );
        final ModelFile.ExistingModelFile miniModel = itemModels().getExistingFile( modLoc( "item/template/template_mini_spawn_egg" ) );
        
        for( RegistryObject<Item> regObj : DWItems.REGISTRY.getEntries() ) {
            if( regObj.get() instanceof ForgeSpawnEggItem ) {
                String name = Objects.requireNonNull( regObj.getId() ).getPath();
                boolean mini = name.startsWith( "mini" ) || name.startsWith( "micro" );
                
                itemModels().getBuilder( name ).parent( mini ? miniModel : normalModel );
            }
        }
    }
}