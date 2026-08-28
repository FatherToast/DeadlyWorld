package fathertoast.deadlyworld.datagen.lang;

import fathertoast.deadlyworld.common.block.misc.DeadlyInfestedBlock;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.unstable.UnstableBlock;
import fathertoast.deadlyworld.common.compat.jade.DWJadePlugin;
import fathertoast.deadlyworld.common.compat.jade.provider.DeadlySpawnerDataProvider;
import fathertoast.deadlyworld.common.compat.jade.provider.FloorTrapDataProvider;
import fathertoast.deadlyworld.common.compat.jade.provider.TowerDispenserDataProvider;
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
        exception( DWEntities.MINI_CHEST_MIMIC.get().getDescriptionId(), "Mini Mimic" );
        exception( DWEntities.JUKEBOX_MIMIC.get().getDescriptionId(), "Mimic" );
        exception( DWEntities.MINI_SPAWNER_MIMIC.get().getDescriptionId(), "Mini Mimic" );
    }
    
    @Override
    protected void addTranslations() {
        super.addTranslations();
        
        // Creative mode tabs
        creativeTab( DWCreativeModeTabs.ALL, "Deadly World" );
        creativeTab( DWCreativeModeTabs.PLACERS, "Deadly World - Feature Placers" );
        
        // Auto-gen block stuff
        add( BlockAutoGen.NameStyle.VANILLA.getLangKey( DeadlyInfestedBlock.BLOCK_KEY ), "Infested " + MsgArgs._1 );
        add( BlockAutoGen.NameStyle.SUSPICIOUS.getLangKey( DeadlyInfestedBlock.BLOCK_KEY ), "\"" + MsgArgs._1 + "\"" );
        add( BlockAutoGen.NameStyle.IDENTITY.getLangKey( DeadlyInfestedBlock.BLOCK_KEY ), MsgArgs._1 );
        add( BlockAutoGen.NameStyle.VANILLA.getLangKey( UnstableBlock.BLOCK_KEY ), "Unstable " + MsgArgs._1 );
        add( BlockAutoGen.NameStyle.SUSPICIOUS.getLangKey( UnstableBlock.BLOCK_KEY ), "\"" + MsgArgs._1 + "\"" );
        add( BlockAutoGen.NameStyle.IDENTITY.getLangKey( UnstableBlock.BLOCK_KEY ), MsgArgs._1 );
        
        // Sound event subtitles
        soundSubtitle( DWSoundEvents.TOWER_DISPENSER_SHOOT, "Tower Dispenser shoots" );
        soundSubtitle( DWSoundEvents.SPIKE_TRAP_CLICK, "Spike trap clicks" );
        soundSubtitle( DWSoundEvents.SEA_MINE_ARMING, "Sea Mine arming" );
        soundSubtitle( DWSoundEvents.MIMIC_APPEAR, "Mimic appears" );
        soundSubtitle( DWSoundEvents.CHEST_MIMIC_HURT, "Mimic hurts" );
        soundSubtitle( DWSoundEvents.CHEST_MIMIC_DEATH, "Mimic dies" );
        soundSubtitle( DWSoundEvents.SPAWNER_MIMIC_HURT, "Mimic hurts" );
        soundSubtitle( DWSoundEvents.SPAWNER_MIMIC_DEATH, "Mimic dies" );
        soundSubtitle( DWSoundEvents.MINI_CHEST_OPEN, "Mini Chest opens" );
        soundSubtitle( DWSoundEvents.MINI_CHEST_CLOSE, "Mini Chest closes" );
        
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
        
        // Misc stuff
        container( "mini_chest", "Mini Chest" );
        deathMessage( DWDamageTypes.SPIKE_TRAP,
                MsgArgs._1 + " was impaled on a spike trap",
                MsgArgs._1 + " was impaled on a spike trap while trying to escape " + MsgArgs._2 );
        deathMessage( DWDamageTypes.TRIGGER_SILVERFISH,
                MsgArgs._1 + " was consumed by anger",
                MsgArgs._1 + " was consumed by anger for " + MsgArgs._2 );
        add( DWEntities.MINI_SPIDER.get().getDescriptionId() + ".spooky", "Itchy Bitchy Spider" );
        
        // Auto-gen for registries
        items( DWItems.REGISTRY );
        blocks( DWBlocks.REGISTRY );
        entityTypes( DWEntities.REGISTRY );
        
        // Jade plugin config options
        jadeCfgOption( DeadlySpawnerDataProvider.ID, "Deadly Spawners" );
        jadeCfgOption( FloorTrapDataProvider.ID, "Floor Traps" );
        jadeCfgOption( TowerDispenserDataProvider.ID, "Tower Dispensers" );
        
        jadeCfgOption( DWJadePlugin.Config.SPAWNER_SPAWN_RANGE, "Spawn Range" );
        jadeCfgOption( DWJadePlugin.Config.SPAWNER_CHECK_SIGHT, "Check Sight" );
        jadeCfgOption( DWJadePlugin.Config.SPAWNER_DELAY_DATA, "Delay Data" );
        jadeCfgOption( DWJadePlugin.Config.SPAWNER_USE_FORGE_HOOK_SPAWNS, "Use Forge Hook Spawns" );
        jadeCfgOption( DWJadePlugin.Config.SPAWNER_IS_MIMIC, "Is Mimic" );
        jadeCfgOption( DWJadePlugin.Config.SPAWNER_SPAWNS_REMAINING, "Spawns Remaining" );
        jadeCfgOption( DWJadePlugin.Config.SPAWNER_DYNAMIC_SPAWN_LIST, "Dynamic Spawn List" );
        
        jadeCfgOption( DWJadePlugin.Config.FLOOR_TRAP_DECOY_TYPE, "Decoy Type" );
        jadeCfgOption( DWJadePlugin.Config.FLOOR_TRAP_CAMO, "Camo Block State" );
        jadeCfgOption( DWJadePlugin.Config.FLOOR_TRAP_ACTIVATION_RANGE, "Activation Range" );
        jadeCfgOption( DWJadePlugin.Config.FLOOR_TRAP_CHECK_SIGHT, "Check Sight" );
        jadeCfgOption( DWJadePlugin.Config.FLOOR_TRAP_RESET_TIME, "Reset Time" );
        jadeCfgOption( DWJadePlugin.Config.FLOOR_TRAP_MAX_TRIGGER_DELAY, "Max Trigger Delay" );
        jadeCfgOption( DWJadePlugin.Config.FLOOR_TRAP_TRIGGERS_REMAINING, "Triggers Remaining" );
        
        jadeCfgOption( DWJadePlugin.Config.TOWER_DISPENSER_ACTIVATION_RANGE, "Activation Range" );
        jadeCfgOption( DWJadePlugin.Config.TOWER_DISPENSER_CHECK_SIGHT, "Check Sight" );
        jadeCfgOption( DWJadePlugin.Config.TOWER_DISPENSER_ATTACK_DELAY, "Attack Delay" );
        jadeCfgOption( DWJadePlugin.Config.TOWER_DISPENSER_ATTACK_DAMAGE, "Attack Damage" );
        jadeCfgOption( DWJadePlugin.Config.TOWER_DISPENSER_PROJECTILE_PROPS, "Projectile Properties" );
        jadeCfgOption( DWJadePlugin.Config.TOWER_DISPENSER_STATE, "State" );
        
        // Jade provider elements
        jadeProviderElement( DeadlySpawnerDataProvider.SPAWN_RANGE, "Spawn range: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.CHECK_SIGHT, "Check sight: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.DELAY_PROGRESSION, "Delay progression: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.DELAY_RECOVERY, "Delay recovery: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.DELAY_BUILDUP, "Delay buildup: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.USE_FORGE_HOOK_SPAWNS, "Uses Forge hook spawns: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.IS_MIMIC, "Is mimic: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.SPAWNS_REMAINING, "Spawns remaining: " + MsgArgs._1 );
        jadeProviderElement( DeadlySpawnerDataProvider.DYNAMIC_SPAWN_LIST, "Dynamic spawn list:" );
        
        jadeProviderElement( FloorTrapDataProvider.DECOY_TYPE, "Decoy type: " + MsgArgs._1 );
        jadeProviderElement( FloorTrapDataProvider.CAMO, "Camo state: " + MsgArgs._1 );
        jadeProviderElement( FloorTrapDataProvider.ACTIVATION_RANGE, "Activation range: " + MsgArgs._1 );
        jadeProviderElement( FloorTrapDataProvider.CHECK_SIGHT, "Check sight: " + MsgArgs._1 );
        jadeProviderElement( FloorTrapDataProvider.MIN_RESET_TIME, "Min reset time: " + MsgArgs._1 );
        jadeProviderElement( FloorTrapDataProvider.MAX_RESET_TIME, "Max reset time: " + MsgArgs._1 );
        jadeProviderElement( FloorTrapDataProvider.MAX_TRIGGER_DELAY, "Max trigger delay: " + MsgArgs._1 );
        jadeProviderElement( FloorTrapDataProvider.TRIGGERS_REMAINING, "Triggers remaining: " + MsgArgs._1 );
        
        jadeProviderElement( TowerDispenserDataProvider.ACTIVATION_RANGE, "Activation range: " + MsgArgs._1 );
        jadeProviderElement( TowerDispenserDataProvider.CHECK_SIGHT, "Check sight: " + MsgArgs._1 );
        jadeProviderElement( TowerDispenserDataProvider.MIN_ATTACK_DELAY, "Min attack delay: " + MsgArgs._1 );
        jadeProviderElement( TowerDispenserDataProvider.MAX_ATTACK_DELAY, "Max attack delay: " + MsgArgs._1 );
        jadeProviderElement( TowerDispenserDataProvider.ATTACK_DAMAGE, "Attack damage: " + MsgArgs._1 );
        jadeProviderElement( TowerDispenserDataProvider.PROJECTILE_SPEED, "Projectile speed: " + MsgArgs._1 );
        jadeProviderElement( TowerDispenserDataProvider.PROJECTILE_VARIANCE, "Projectile variance: " + MsgArgs._1 );
        jadeProviderElement( TowerDispenserDataProvider.STATE, "State: " + MsgArgs._1 );
    }
}