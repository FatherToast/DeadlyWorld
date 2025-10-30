package fathertoast.deadlyworld.datagen.lang;

import fathertoast.deadlyworld.common.block.unstable.UnstableBlock;
import fathertoast.deadlyworld.common.core.registry.BlockAutoGen;
import fathertoast.deadlyworld.common.block.misc.DeadlyInfestedBlock;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.core.registry.*;
import fathertoast.deadlyworld.common.item.AutoGenBlockItem;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import net.minecraft.data.PackOutput;

public class DWLangProvider extends DWAbstractLangProvider {
    
    public DWLangProvider( PackOutput output ) {
        super( output );
    }
    
    @Override
    protected void addExceptions() {
        // Spawners
        exception( DWBlocks.spawner( SpawnerType.SIMPLE ).get().getDescriptionId(), "Monster Spawner" );
        exception( DWBlocks.spawner( SpawnerType.STREAM ).get().getDescriptionId(), "Monster Stream Spawner" );
        exception( DWBlocks.spawner( SpawnerType.SWARM ).get().getDescriptionId(), "Monster Spawner of Swarms" );
        exception( DWBlocks.spawner( SpawnerType.BRUTAL ).get().getDescriptionId(), "Monster Spawner of Brutality" );
        exception( DWBlocks.spawner( SpawnerType.NEST ).get().getDescriptionId(), "Silverfish Nest" );
        exception( DWBlocks.spawner( SpawnerType.DUNGEON ).get().getDescriptionId(), "Dungeon Monster Spawner" );
        exception( DWBlocks.spawner( SpawnerType.MINI ).get().getDescriptionId(), "Mini Monster Spawner" );
        
        // Tower dispensers
        exception( DWBlocks.towerDispenser( TowerType.SIMPLE ).get().getDescriptionId(), "Arrow Tower Dispenser" );
        exception( DWBlocks.towerDispenser( TowerType.FIRE ).get().getDescriptionId(), "Flaming Arrow Tower Dispenser" );
        exception( DWBlocks.towerDispenser( TowerType.POTION ).get().getDescriptionId(), "Magic Arrow Tower Dispenser" );
        exception( DWBlocks.towerDispenser( TowerType.FIREBALL ).get().getDescriptionId(), "Fireball Tower Dispenser" );
        exception( DWBlocks.towerDispenser( TowerType.GATLING ).get().getDescriptionId(), "Gatling Arrow Tower Dispenser" );
        
        // Mimics
        exception( DWEntities.CHEST_MIMIC.get().getDescriptionId(), "Mimic" );
        exception( DWEntities.JUKEBOX_MIMIC.get().getDescriptionId(), "Mimic" );
        exception( DWEntities.MINI_SPAWNER_MIMIC.get().getDescriptionId(), "Mimic" );
    }
    
    @Override
    protected void addTranslations() {
        super.addTranslations();
        
        // Creative mode tabs
        creativeTab( DWCreativeModeTabs.ALL, "Deadly World" );
        creativeTab( DWCreativeModeTabs.PLACERS, "Deadly World - Feature Placers" );
        
        // Auto-gen block stuff
        add( BlockAutoGen.NameStyle.VANILLA.getLangKey( DeadlyInfestedBlock.BLOCK_KEY ), "Infested " + MsgPlaceholder.FIRST );
        add( BlockAutoGen.NameStyle.SUSPICIOUS.getLangKey( DeadlyInfestedBlock.BLOCK_KEY ), "\"" + MsgPlaceholder.FIRST + "\"" );
        add( BlockAutoGen.NameStyle.IDENTITY.getLangKey( DeadlyInfestedBlock.BLOCK_KEY ), MsgPlaceholder.FIRST );
        add( BlockAutoGen.NameStyle.VANILLA.getLangKey( UnstableBlock.BLOCK_KEY ), "Unstable " + MsgPlaceholder.FIRST );
        add( BlockAutoGen.NameStyle.SUSPICIOUS.getLangKey( UnstableBlock.BLOCK_KEY ), "\"" + MsgPlaceholder.FIRST + "\"" );
        add( BlockAutoGen.NameStyle.IDENTITY.getLangKey( UnstableBlock.BLOCK_KEY ), MsgPlaceholder.FIRST );
        
        // Sound event subtitles
        soundSubtitle( DWSoundEvents.TOWER_DISPENSER_SHOOT, "Tower Dispenser shoots" );
        soundSubtitle( DWSoundEvents.MIMIC_APPEAR, "Mimic appears" );
        soundSubtitle( DWSoundEvents.CHEST_MIMIC_HURT, "Mimic hurts" );
        soundSubtitle( DWSoundEvents.CHEST_MIMIC_DEATH, "Mimic dies" );
        soundSubtitle( DWSoundEvents.SPAWNER_MIMIC_HURT, "Mimic hurts" );
        soundSubtitle( DWSoundEvents.SPAWNER_MIMIC_DEATH, "Mimic dies" );
        soundSubtitle( DWSoundEvents.MINI_CHEST_OPEN, "Mini Chest opens" );
        soundSubtitle( DWSoundEvents.MINI_CHEST_CLOSE, "Mini Chest closes" );
        
        // Containers
        container( "mini_chest", "Mini Chest" );
        
        // Item tooltips
        tooltip( DWItems.FEATURE_PLACER, "Generates:" );
        tooltip( DWItems.CONTAINER_INFESTATION, "When triggered:" );
        tooltip( DWItems.CONTAINER_TRAP, "When triggered:" );
        tooltip( DWItems.CONTAINER_INFESTATION, "spiders", "Spawns mini spiders" );
        tooltip( DWItems.CONTAINER_INFESTATION, "silverfish", "Spawns silverfish" );
        tooltip( DWItems.CONTAINER_TRAP, "tnt", "Spawns primed TNT" );
        tooltip( DWItems.CONTAINER_TRAP, "lava", "Spews lava" );
        tooltip( DWItems.CONTAINER_TRAP, "runny_lava", "Spews runny lava" );
        tooltip( DWItems.CONTAINER_TRAP, "poison_gas", "Releases poison gas" );
        tooltip( DWItems.CONTAINER_TRAP, "wither_gas", "Releases withering gas" );
        add( AutoGenBlockItem.translationKey( DeadlyInfestedBlock.BLOCK_KEY ), "Infested" );
        add( AutoGenBlockItem.translationKey( UnstableBlock.BLOCK_KEY ), "Unstable" );
        
        deathMessage( DWDamageTypes.SPIKE_TRAP,
                MsgPlaceholder.FIRST + " was impaled on a spike trap",
                MsgPlaceholder.FIRST + " was impaled on a spike trap while trying to escape " + MsgPlaceholder.SECOND );
        deathMessage( DWDamageTypes.TRIGGER_SILVERFISH,
                MsgPlaceholder.FIRST + " was consumed by anger",
                MsgPlaceholder.FIRST + " was consumed by anger for " + MsgPlaceholder.SECOND );
        
        // Auto-gen for registries
        items( DWItems.REGISTRY );
        blocks( DWBlocks.REGISTRY );
        entityTypes( DWEntities.REGISTRY );
    }
}