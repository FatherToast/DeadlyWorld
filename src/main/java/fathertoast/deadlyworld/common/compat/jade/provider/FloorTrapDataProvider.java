package fathertoast.deadlyworld.common.compat.jade.provider;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.api.lib.DWRegistries;
import fathertoast.deadlyworld.common.block.entity.FloorTrapBlockEntity;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import static fathertoast.deadlyworld.common.compat.jade.DWJadePlugin.Config;
import static fathertoast.deadlyworld.common.world.logic.BaseTrap.*;

public class FloorTrapDataProvider extends BaseBlockEntityProvider {
    
    public static final ResourceLocation ID = DeadlyWorld.rl( "floor_trap" );
    
    public static final ProviderTooltipKey DECOY_TYPE = ProviderTooltipKey.of( TAG_DECOY_TYPE, "floor_trap.decoy_type" );
    public static final ProviderTooltipKey CAMO = ProviderTooltipKey.of( TAG_CAMO, "floor_trap.camo" );
    public static final ProviderTooltipKey ACTIVATION_RANGE = ProviderTooltipKey.of( TAG_ACTIVATION_RANGE, "floor_trap.activation_range" );
    public static final ProviderTooltipKey CHECK_SIGHT = ProviderTooltipKey.of( TAG_CHECK_SIGHT, "floor_trap.check_sight" );
    public static final ProviderTooltipKey MIN_RESET_TIME = ProviderTooltipKey.of( TAG_MIN_RESET_TIME, "floor_trap.min_reset_time" );
    public static final ProviderTooltipKey MAX_RESET_TIME = ProviderTooltipKey.of( TAG_MAX_RESET_TIME, "floor_trap.max_reset_time" );
    public static final ProviderTooltipKey MAX_TRIGGER_DELAY = ProviderTooltipKey.of( TAG_MAX_TRIGGER_DELAY, "floor_trap.max_trigger_delay" );
    public static final ProviderTooltipKey TRIGGERS_REMAINING = ProviderTooltipKey.of( TAG_TRIGGERS_REMAINING, "floor_trap.triggers_remaining" );
    
    
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
        if( accessor.getBlockEntity() instanceof FloorTrapBlockEntity floorTrap ) {
            final BaseTrap trapLogic = floorTrap.getTrapLogic();
            
            if( trapLogic.getDecoyType() != null ) {
                NBTHelper.putRegistryEntry( data, DWRegistries.DECOY_TYPE_REGISTRY.get(), DECOY_TYPE.nbtKey(), trapLogic.getDecoyType() );
            }
            NBTHelper.putBlockState( data, CAMO.nbtKey(), trapLogic.getCamoState() );
            data.putFloat( ACTIVATION_RANGE.nbtKey(), (float) trapLogic.getActivationRange() );
            data.putBoolean( CHECK_SIGHT.nbtKey(), trapLogic.checksSight() );
            data.putInt( MIN_RESET_TIME.nbtKey(), trapLogic.getMinResetTime() );
            data.putInt( MAX_RESET_TIME.nbtKey(), trapLogic.getMaxResetTime() );
            data.putInt( MAX_TRIGGER_DELAY.nbtKey(), trapLogic.getMaxTriggerDelay() );
            data.putInt( TRIGGERS_REMAINING.nbtKey(), trapLogic.getTriggersRemaining() );
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
        final CompoundTag serverData = accessor.getServerData();
        
        stringTooltips( cfg, tooltip, Config.FLOOR_TRAP_DECOY_TYPE, serverData, DECOY_TYPE );
        blockStateTooltips( cfg, tooltip, Config.FLOOR_TRAP_CAMO, serverData, CAMO );
        floatTooltips( cfg, tooltip, Config.FLOOR_TRAP_ACTIVATION_RANGE, serverData, ACTIVATION_RANGE );
        booleanTooltips( cfg, tooltip, Config.FLOOR_TRAP_CHECK_SIGHT, serverData, CHECK_SIGHT );
        intTooltips( cfg, tooltip, Config.FLOOR_TRAP_RESET_TIME, serverData, MIN_RESET_TIME, MAX_RESET_TIME );
        intTooltips( cfg, tooltip, Config.FLOOR_TRAP_MAX_TRIGGER_DELAY, serverData, MAX_TRIGGER_DELAY );
        intTooltips( cfg, tooltip, Config.FLOOR_TRAP_TRIGGERS_REMAINING, serverData, TRIGGERS_REMAINING );
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
