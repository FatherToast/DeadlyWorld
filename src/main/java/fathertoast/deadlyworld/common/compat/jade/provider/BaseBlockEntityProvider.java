package fathertoast.deadlyworld.common.compat.jade.provider;

import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.List;
import java.util.function.Function;

/** A base class for block entity data providers. Contains various helper methods. */
public abstract class BaseBlockEntityProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    
    /**
     * Adds a conditional component from a compound tag to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add a component to.
     * @param cfgOptionId The ID of the config option to check before adding a component to the tooltip.
     * @param elementKey  The {@link ProviderTooltipKey element key} to make a component with.
     * @param nbtGetter   A function that retrieves a piece of data from a compound tag to display in the tooltip.
     */
    protected void addTooltip( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                               ProviderTooltipKey elementKey, Function<String, Object> nbtGetter ) {
        if( cfg.get( cfgOptionId ) ) {
            tooltip.add( Component.translatable( elementKey.langKey(), nbtGetter.apply( elementKey.nbtKey() ) ) );
        }
    }
    
    /**
     * Adds X conditional boolean components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with boolean values.
     */
    protected void booleanTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                    CompoundTag serverData, ProviderTooltipKey... tooltipKeys ) {
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsNumber( serverData, key.nbtKey() ) ) {
                addTooltip( cfg, tooltip, cfgOptionId, key, serverData::getBoolean );
            }
        }
    }
    
    /**
     * Adds X conditional int components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with int values.
     */
    protected void intTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                CompoundTag serverData, ProviderTooltipKey... tooltipKeys ) {
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsNumber( serverData, key.nbtKey() ) ) {
                addTooltip( cfg, tooltip, cfgOptionId, key, serverData::getInt );
            }
        }
    }
    
    /**
     * Adds X conditional float components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with float values.
     */
    protected void floatTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                  CompoundTag serverData, ProviderTooltipKey... tooltipKeys ) {
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsNumber( serverData, key.nbtKey() ) ) {
                addTooltip( cfg, tooltip, cfgOptionId, key, serverData::getFloat );
            }
        }
    }
    
    /**
     * Adds X conditional double components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with double values.
     */
    protected void doubleTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                   CompoundTag serverData, ProviderTooltipKey... tooltipKeys ) {
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsNumber( serverData, key.nbtKey() ) ) {
                addTooltip( cfg, tooltip, cfgOptionId, key, serverData::getDouble );
            }
        }
    }
    
    /**
     * Adds X conditional long components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with long values.
     */
    protected void longTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                 CompoundTag serverData, ProviderTooltipKey... tooltipKeys ) {
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsNumber( serverData, key.nbtKey() ) ) {
                addTooltip( cfg, tooltip, cfgOptionId, key, serverData::getLong );
            }
        }
    }
    
    /**
     * Adds X conditional string components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with string values.
     */
    protected void stringTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                   CompoundTag serverData, ProviderTooltipKey... tooltipKeys ) {
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsString( serverData, key.nbtKey() ) ) {
                addTooltip( cfg, tooltip, cfgOptionId, key, serverData::getString );
            }
        }
    }
    
    /**
     * Adds X conditional string list components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param indent      The indentation to use for each string in the string list to add to the tooltip.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with string list values.
     */
    protected void stringListTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                       CompoundTag serverData, int indent, ProviderTooltipKey... tooltipKeys ) {
        if( !cfg.get( cfgOptionId ) ) return;
        indent = Math.max( indent, 0 );
        
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsStringList( serverData, key.nbtKey() ) ) {
                final List<String> strings = NBTHelper.getStringList( serverData, key.nbtKey() );
                final String indentation = " ".repeat( indent );
                tooltip.add( Component.translatable( key.langKey() ) );
                strings.forEach( ( s ) -> tooltip.add( Component.literal( indentation + s ) ) );
            }
        }
    }
    
    /**
     * Adds X conditional block state components to the given tooltip.
     *
     * @param cfg         The Jade config accessor.
     * @param tooltip     The tooltip to add one or more components to.
     * @param cfgOptionId The ID of the config option to check before adding components to the tooltip.
     * @param serverData  The compound tag to fetch data from. This is expected to be the server data tag provided by Jade.
     * @param tooltipKeys The tooltip keys to create tooltip entries from.
     *                    Each tooltip key provided are expected to be associated with block state values.
     */
    protected void blockStateTooltips( IPluginConfig cfg, ITooltip tooltip, ResourceLocation cfgOptionId,
                                       CompoundTag serverData, ProviderTooltipKey... tooltipKeys ) {
        for( ProviderTooltipKey key : tooltipKeys ) {
            if( NBTHelper.containsCompound( serverData, key.nbtKey() ) ) {
                addTooltip( cfg, tooltip, cfgOptionId, key,
                        ( s ) -> NBTHelper.readBlockState( serverData.getCompound( s ) ) );
            }
        }
    }
}
