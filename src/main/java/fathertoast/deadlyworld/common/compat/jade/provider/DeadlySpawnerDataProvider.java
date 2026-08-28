package fathertoast.deadlyworld.common.compat.jade.provider;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.entity.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

import static fathertoast.deadlyworld.common.compat.jade.DWJadePlugin.Config;
import static fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner.*;

public class DeadlySpawnerDataProvider extends BaseBlockEntityProvider {
    
    public static final ResourceLocation ID = DeadlyWorld.rl( "deadly_spawner" );
    
    public static final ProviderTooltipKey SPAWN_RANGE = ProviderTooltipKey.of( "SpawnRange", "deadly_spawner.spawn_range" );
    public static final ProviderTooltipKey CHECK_SIGHT = ProviderTooltipKey.of( TAG_CHECK_SIGHT, "deadly_spawner.decoy_type" );
    public static final ProviderTooltipKey DELAY_PROGRESSION = ProviderTooltipKey.of( TAG_DELAY_PROGRESSION, "deadly_spawner.delay_progression" );
    public static final ProviderTooltipKey DELAY_RECOVERY = ProviderTooltipKey.of( TAG_DELAY_RECOVERY, "deadly_spawner.delay_recovery" );
    public static final ProviderTooltipKey USE_FORGE_HOOK_SPAWNS = ProviderTooltipKey.of( TAG_USE_FORGE_HOOK_SPAWNS, "deadly_spawner.use_forge_hook_spawns" );
    public static final ProviderTooltipKey IS_MIMIC = ProviderTooltipKey.of( TAG_IS_MIMIC, "deadly_spawner.is_mimic" );
    public static final ProviderTooltipKey DELAY_BUILDUP = ProviderTooltipKey.of( TAG_DELAY_BUILDUP, "deadly_spawner.delay_buildup" );
    public static final ProviderTooltipKey SPAWNS_REMAINING = ProviderTooltipKey.of( TAG_SPAWNS_REMAINING, "deadly_spawner.spawns_remaining" );
    public static final ProviderTooltipKey DYNAMIC_SPAWN_LIST = ProviderTooltipKey.of( TAG_DYNAMIC_SPAWN_LIST, "deadly_spawner.dynamic_spawn_list" );
    
    
    /**
     * Callback used server side to return a custom synchronization compound tag.
     * <br>
     * Will only be called if the implementing class is registered via {@link IWailaCommonRegistration#registerBlockDataProvider} or {@link IWailaCommonRegistration#registerEntityDataProvider}.
     * <br>
     *
     * @param data     Current synchronization tag (might have been processed by other providers and might be processed by other providers).
     * @param accessor Contains the relevant information about the current environment.
     */
    @Override
    public void appendServerData( CompoundTag data, BlockAccessor accessor ) {
        if( accessor.getBlockEntity() instanceof DeadlySpawnerBlockEntity spawner ) {
            final ProgressiveDelaySpawner spawnerLogic = spawner.getSpawnerLogic();
            
            data.putInt( SPAWN_RANGE.nbtKey(), spawnerLogic.getSpawnRange() );
            data.putBoolean( CHECK_SIGHT.nbtKey(), spawnerLogic.checksSight() );
            data.putInt( DELAY_PROGRESSION.nbtKey(), spawnerLogic.getDelayProgression() );
            data.putFloat( DELAY_RECOVERY.nbtKey(), spawnerLogic.getDelayRecovery() );
            data.putFloat( DELAY_BUILDUP.nbtKey(), spawnerLogic.getDelayBuildup() );
            data.putBoolean( USE_FORGE_HOOK_SPAWNS.nbtKey(), spawnerLogic.usesForgeHookSpawns() );
            data.putBoolean( IS_MIMIC.nbtKey(), spawnerLogic.isMimic() );
            data.putInt( SPAWNS_REMAINING.nbtKey(), spawnerLogic.getRemainingSpawns() );
            
            if( spawnerLogic.getDynamicSpawnList() != null ) {
                NBTHelper.putStringList( data, DYNAMIC_SPAWN_LIST.nbtKey(), spawnerLogic.getDynamicSpawnList().toStringList() );
            }
        }
    }
    
    /**
     * Callback used to add renderable elements to the tooltip and modify existing elements to the tooltip.
     * <br>
     * Will only be called if the implementing class is registered via {@link IWailaClientRegistration#registerBlockComponent(IBlockComponentProvider, Class)}.
     * <br>
     * <p>
     * This method is only called on the client side. If you require data from the server, you should also implement
     * {@link IServerDataProvider#appendServerData(CompoundTag, Accessor)}
     * and add the data to the {@link CompoundTag} there, which can then be read back using {@link Accessor#getServerData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip  Current list of tooltip lines (might have been processed by other providers and might be processed
     *                 by other providers).
     * @param accessor Contains most of the relevant information about the current environment.
     * @param cfg      Current configuration of Waila.
     */
    @Override
    public void appendTooltip( ITooltip tooltip, BlockAccessor accessor, IPluginConfig cfg ) {
        if( accessor.getBlockEntity() instanceof DeadlySpawnerBlockEntity spawner ) {
            replaceDisplayName( tooltip, accessor, spawner.getSpawnerLogic() );
        }
        final CompoundTag serverData = accessor.getServerData();
        
        intTooltips( cfg, tooltip, Config.SPAWNER_SPAWN_RANGE, serverData, SPAWN_RANGE );
        booleanTooltips( cfg, tooltip, Config.SPAWNER_CHECK_SIGHT, serverData, CHECK_SIGHT );
        intTooltips( cfg, tooltip, Config.SPAWNER_DELAY_DATA, serverData, DELAY_PROGRESSION );
        floatTooltips( cfg, tooltip, Config.SPAWNER_DELAY_DATA, serverData, DELAY_RECOVERY, DELAY_BUILDUP );
        booleanTooltips( cfg, tooltip, Config.SPAWNER_USE_FORGE_HOOK_SPAWNS, serverData, USE_FORGE_HOOK_SPAWNS );
        booleanTooltips( cfg, tooltip, Config.SPAWNER_IS_MIMIC, serverData, IS_MIMIC );
        intTooltips( cfg, tooltip, Config.SPAWNER_SPAWNS_REMAINING, serverData, SPAWNS_REMAINING );
        stringListTooltips( cfg, tooltip, Config.SPAWNER_DYNAMIC_SPAWN_LIST, serverData, 3, DYNAMIC_SPAWN_LIST );
    }
    
    /** Appends the spawner's next spawn entity's type name to the display name. */
    private void replaceDisplayName( ITooltip tooltip, BlockAccessor accessor, BaseSpawner spawner ) {
        Entity nextSpawn = spawner.getOrCreateDisplayEntity( accessor.getLevel(), accessor.getLevel().getRandom(), accessor.getPosition() );
        
        if( nextSpawn != null ) {
            String blockName = Component.translatable( accessor.getBlock().getDescriptionId() ).getString();
            MutableComponent displayName = Component.translatable( "jade.spawner", blockName, nextSpawn.getDisplayName() );
            tooltip.remove( Identifiers.CORE_OBJECT_NAME );
            tooltip.add( 0, IThemeHelper.get().title( displayName ), Identifiers.CORE_OBJECT_NAME );
        }
    }
    
    /** @return True if this provider should be enabled by default. */
    @Override
    public boolean enabledByDefault() {
        return false;
    }
    
    /** @return The unique ID of this provider. Providers from different registries can have the same ID. */
    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
