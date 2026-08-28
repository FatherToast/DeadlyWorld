package fathertoast.deadlyworld.common.compat.jade.provider;

import fathertoast.deadlyworld.common.block.entity.TowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.logic.BaseTower;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import static fathertoast.deadlyworld.common.compat.jade.DWJadePlugin.Config;
import static fathertoast.deadlyworld.common.world.logic.BaseTower.*;

public class TowerDispenserDataProvider extends BaseBlockEntityProvider {
    
    public static final ResourceLocation ID = DeadlyWorld.rl( "tower_dispenser" );
    
    public static final ProviderTooltipKey ACTIVATION_RANGE = ProviderTooltipKey.of( TAG_ACTIVATION_RANGE, "tower_dispenser.activation_range" );
    public static final ProviderTooltipKey CHECK_SIGHT = ProviderTooltipKey.of( TAG_CHECK_SIGHT, "tower_dispenser.check_sight" );
    public static final ProviderTooltipKey MIN_ATTACK_DELAY = ProviderTooltipKey.of( TAG_MIN_ATTACK_DELAY, "tower_dispenser.min_attack_delay" );
    public static final ProviderTooltipKey MAX_ATTACK_DELAY = ProviderTooltipKey.of( TAG_MIN_ATTACK_DELAY, "tower_dispenser.max_attack_delay" );
    public static final ProviderTooltipKey ATTACK_DAMAGE = ProviderTooltipKey.of( TAG_ATTACK_DAMAGE, "tower_dispenser.attack_damage" );
    public static final ProviderTooltipKey PROJECTILE_SPEED = ProviderTooltipKey.of( TAG_PROJECTILE_SPEED, "tower_dispenser.projectile_speed" );
    public static final ProviderTooltipKey PROJECTILE_VARIANCE = ProviderTooltipKey.of( TAG_PROJECTILE_VARIANCE, "tower_dispenser.projectile_variance" );
    public static final ProviderTooltipKey STATE = ProviderTooltipKey.of( "State", "tower_dispenser.state" );
    
    
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
        if( accessor.getBlockEntity() instanceof TowerDispenserBlockEntity towerDispenser ) {
            final BaseTower towerLogic = towerDispenser.getTowerLogic();
            
            data.putFloat( ACTIVATION_RANGE.nbtKey(), (float) towerLogic.getActivationRange() );
            data.putBoolean( CHECK_SIGHT.nbtKey(), towerLogic.checksSight() );
            data.putInt( MIN_ATTACK_DELAY.nbtKey(), towerLogic.getMinAttackDelay() );
            data.putInt( MAX_ATTACK_DELAY.nbtKey(), towerLogic.getMaxAttackDelay() );
            data.putFloat( ATTACK_DAMAGE.nbtKey(), towerLogic.getAttackDamage() );
            data.putFloat( PROJECTILE_SPEED.nbtKey(), towerLogic.getProjectileSpeed() );
            data.putFloat( PROJECTILE_VARIANCE.nbtKey(), towerLogic.getProjectileVariance() );
            data.putString( STATE.nbtKey(), towerLogic.getState().name() );
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
        
        floatTooltips( cfg, tooltip, Config.TOWER_DISPENSER_ACTIVATION_RANGE, serverData, ACTIVATION_RANGE );
        booleanTooltips( cfg, tooltip, Config.TOWER_DISPENSER_CHECK_SIGHT, serverData, CHECK_SIGHT );
        intTooltips( cfg, tooltip, Config.TOWER_DISPENSER_ATTACK_DELAY, serverData, MIN_ATTACK_DELAY, MAX_ATTACK_DELAY );
        floatTooltips( cfg, tooltip, Config.TOWER_DISPENSER_ATTACK_DAMAGE, serverData, ATTACK_DAMAGE );
        floatTooltips( cfg, tooltip, Config.TOWER_DISPENSER_PROJECTILE_PROPS, serverData, PROJECTILE_SPEED, PROJECTILE_VARIANCE );
        stringTooltips( cfg, tooltip, Config.TOWER_DISPENSER_STATE, serverData, STATE );
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
