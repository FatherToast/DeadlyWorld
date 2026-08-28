package fathertoast.deadlyworld.common.compat.jade;

import fathertoast.deadlyworld.common.block.entity.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.entity.FloorTrapBlockEntity;
import fathertoast.deadlyworld.common.block.entity.TowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
import fathertoast.deadlyworld.common.compat.jade.provider.DeadlySpawnerDataProvider;
import fathertoast.deadlyworld.common.compat.jade.provider.FloorTrapDataProvider;
import fathertoast.deadlyworld.common.compat.jade.provider.TowerDispenserDataProvider;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.IAutoGenBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;

@WailaPlugin
public class DWJadePlugin implements IWailaPlugin {
    
    @Override
    public void register( IWailaCommonRegistration reg ) {
        reg.registerBlockDataProvider( new DeadlySpawnerDataProvider(), DeadlySpawnerBlockEntity.class );
        reg.registerBlockDataProvider( new FloorTrapDataProvider(), FloorTrapBlockEntity.class );
        reg.registerBlockDataProvider( new TowerDispenserDataProvider(), TowerDispenserBlockEntity.class );
    }
    
    @Override
    public void registerClient( IWailaClientRegistration reg ) {
        addConfigEntries( reg );
        
        reg.registerBlockComponent( new DeadlySpawnerDataProvider(), DeadlySpawnerBlock.class );
        reg.registerBlockComponent( new FloorTrapDataProvider(), FloorTrapBlock.class );
        reg.registerBlockComponent( new TowerDispenserDataProvider(), TowerDispenserBlock.class );
        
        reg.addRayTraceCallback( ( hitResult, accessor, originalAccessor ) -> {
            if( accessor instanceof BlockAccessor blockAccessor && !accessor.getPlayer().isCreative() &&
                    blockAccessor.getBlock() instanceof IAutoGenBlock autoGenBlock ) {
                accessor.getServerData().putString( "givenName",
                        Component.Serializer.toJson( blockAccessor.getBlock().getName() ) );
                return reg.blockAccessor().from( blockAccessor )
                        .fakeBlock( new ItemStack( autoGenBlock.getOriginBlock() ) ).build();
            }
            return accessor;
        } );
    }
    
    /** Defines this plugin's Jade config entries. */
    private void addConfigEntries( IWailaClientRegistration reg ) {
        reg.addConfig( Config.SPAWNER_SPAWN_RANGE, true );
        reg.addConfig( Config.SPAWNER_CHECK_SIGHT, true );
        reg.addConfig( Config.SPAWNER_DELAY_DATA, true );
        reg.addConfig( Config.SPAWNER_USE_FORGE_HOOK_SPAWNS, true );
        reg.addConfig( Config.SPAWNER_IS_MIMIC, true );
        reg.addConfig( Config.SPAWNER_SPAWNS_REMAINING, true );
        reg.addConfig( Config.SPAWNER_DYNAMIC_SPAWN_LIST, true );
        
        reg.addConfig( Config.FLOOR_TRAP_DECOY_TYPE, true );
        reg.addConfig( Config.FLOOR_TRAP_CAMO, true );
        reg.addConfig( Config.FLOOR_TRAP_ACTIVATION_RANGE, true );
        reg.addConfig( Config.FLOOR_TRAP_CHECK_SIGHT, true );
        reg.addConfig( Config.FLOOR_TRAP_RESET_TIME, true );
        reg.addConfig( Config.FLOOR_TRAP_MAX_TRIGGER_DELAY, true );
        reg.addConfig( Config.FLOOR_TRAP_TRIGGERS_REMAINING, true );
        
        reg.addConfig( Config.TOWER_DISPENSER_ACTIVATION_RANGE, true );
        reg.addConfig( Config.TOWER_DISPENSER_CHECK_SIGHT, true );
        reg.addConfig( Config.TOWER_DISPENSER_ATTACK_DELAY, true );
        reg.addConfig( Config.TOWER_DISPENSER_ATTACK_DAMAGE, true );
        reg.addConfig( Config.TOWER_DISPENSER_PROJECTILE_PROPS, true );
        reg.addConfig( Config.TOWER_DISPENSER_STATE, true );
    }
    
    
    /** Contains the IDs of every Jade config entry added by this plugin. */
    public interface Config {
        ResourceLocation SPAWNER_SPAWN_RANGE = id( DeadlySpawnerDataProvider.ID, "spawn_range" );
        ResourceLocation SPAWNER_CHECK_SIGHT = id( DeadlySpawnerDataProvider.ID, "check_sight" );
        ResourceLocation SPAWNER_DELAY_DATA = id( DeadlySpawnerDataProvider.ID, "delay_data" );
        ResourceLocation SPAWNER_USE_FORGE_HOOK_SPAWNS = id( DeadlySpawnerDataProvider.ID, "use_forge_hook_spawns" );
        ResourceLocation SPAWNER_IS_MIMIC = id( DeadlySpawnerDataProvider.ID, "is_mimic" );
        ResourceLocation SPAWNER_SPAWNS_REMAINING = id( DeadlySpawnerDataProvider.ID, "spawns_remaining" );
        ResourceLocation SPAWNER_DYNAMIC_SPAWN_LIST = id( DeadlySpawnerDataProvider.ID, "dynamic_spawn_list" );
        
        ResourceLocation FLOOR_TRAP_DECOY_TYPE = id( FloorTrapDataProvider.ID, "decoration_type" );
        ResourceLocation FLOOR_TRAP_CAMO = id( FloorTrapDataProvider.ID, "camo" );
        ResourceLocation FLOOR_TRAP_ACTIVATION_RANGE = id( FloorTrapDataProvider.ID, "activation_range" );
        ResourceLocation FLOOR_TRAP_CHECK_SIGHT = id( FloorTrapDataProvider.ID, "check_sight" );
        ResourceLocation FLOOR_TRAP_RESET_TIME = id( FloorTrapDataProvider.ID, "reset_time" );
        ResourceLocation FLOOR_TRAP_MAX_TRIGGER_DELAY = id( FloorTrapDataProvider.ID, "max_trigger_delay" );
        ResourceLocation FLOOR_TRAP_TRIGGERS_REMAINING = id( FloorTrapDataProvider.ID, "triggers_remaining" );
        
        ResourceLocation TOWER_DISPENSER_ACTIVATION_RANGE = id( TowerDispenserDataProvider.ID, "activation_range" );
        ResourceLocation TOWER_DISPENSER_CHECK_SIGHT = id( TowerDispenserDataProvider.ID, "check_sight" );
        ResourceLocation TOWER_DISPENSER_ATTACK_DELAY = id( TowerDispenserDataProvider.ID, "attack_delay" );
        ResourceLocation TOWER_DISPENSER_ATTACK_DAMAGE = id( TowerDispenserDataProvider.ID, "attack_damage" );
        ResourceLocation TOWER_DISPENSER_PROJECTILE_PROPS = id( TowerDispenserDataProvider.ID, "projectile_properties" );
        ResourceLocation TOWER_DISPENSER_STATE = id( TowerDispenserDataProvider.ID, "state" );
        
        /** Convenience method for creating a resource location with the Deadly World namespace. */
        static ResourceLocation id( String path ) {
            return DeadlyWorld.rl( path );
        }
        
        /**
         * Convenience method for creating a resource location with the Deadly World namespace.
         *
         * @param parent A resource location whose path should be used as the base path.
         */
        static ResourceLocation id( ResourceLocation parent, String path ) {
            return id( parent.getPath() + "." + path );
        }
    }
}