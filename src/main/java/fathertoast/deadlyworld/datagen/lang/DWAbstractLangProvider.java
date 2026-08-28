package fathertoast.deadlyworld.datagen.lang;

import fathertoast.deadlyworld.common.compat.jade.provider.ProviderTooltipKey;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWCreativeModeTabs;
import fathertoast.deadlyworld.common.core.registry.IAutoGenBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for our lang provider.
 * Keeping convenience methods here so the
 * implementation doesn't get super bloated and insane looking.
 * <br><br>
 * For now, we only generate for american english (en_us).
 */
public abstract class DWAbstractLangProvider extends LanguageProvider {
    
    /** A map of key-translation pairs that should override auto-generated entries. */
    private final Map<String, String> exceptions = new HashMap<>();
    
    
    public DWAbstractLangProvider( PackOutput output ) {
        super( output, DeadlyWorld.MOD_ID, "en_us" );
    }
    
    @Override
    protected void addTranslations() {
        addExceptions();
        exceptions.forEach( this::add );
    }
    
    /** Called before exception translations are processed. Add any exceptions here. */
    protected abstract void addExceptions();
    
    /** Adds an exception translation to the map of translation exceptions. */
    protected void exception( String key, String translation ) {
        Objects.requireNonNull( key );
        Objects.requireNonNull( translation );
        exceptions.put( key, translation );
    }
    
    /** Adds a creative mode tab name translation for the given creative tab using the tab's registry key. */
    protected void creativeTab( DWCreativeModeTabs.CreativeTabRegObj regObj, String translation ) {
        // Assume display name is a translatable component
        try {
            add( ((TranslatableContents) regObj.regObj().get().getDisplayName().getContents()).getKey(), translation );
        }
        catch( ClassCastException e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
            DeadlyWorld.LOG.error( "Attempted to generate localization for creative mode tab with a display name component that doesn't have translatable content!" );
        }
    }
    
    /** Adds a subtitle translation for the given sound event. */
    protected void soundSubtitle( RegistryObject<SoundEvent> regObj, String translation ) {
        String key = "sound_event." + Objects.requireNonNull( regObj.getId() ).getNamespace() + ".subtitle." + regObj.getId().getPath();
        add( key, translation );
    }
    
    /**
     * Adds an item sub-tooltip translation for the given item,
     * creating a translation key with the format <b>"item.{namespace}.{path}.tooltip.{subKey}"</b>.
     */
    protected void tooltip( RegistryObject<? extends Item> regObj, @Nullable String subKey, String translation ) {
        StringBuilder builder = new StringBuilder( regObj.get().getDescriptionId() );
        builder.append( ".tooltip" );
        
        if( subKey != null && !subKey.isEmpty() ) {
            builder.append( "." );
            builder.append( subKey );
        }
        add( builder.toString(), translation );
    }
    
    /**
     * Adds an item tooltip translation for the given item,
     * creating a translation key with the format <b>"item.{namespace}.{path}.tooltip"</b>.
     */
    protected void tooltip( RegistryObject<? extends Item> regObj, String translation ) {
        tooltip( regObj, null, translation );
    }
    
    /**
     * Adds death message translations for the given damage type key.
     *
     * @param damageTypeKey The registry key for the damage type.
     * @param message       The translated death message displayed when a player dies without any other entity being involved.
     * @param chasedMessage The translated death message displayed when a player dies after recently being attacked by an entity.
     */
    protected void deathMessage( ResourceKey<DamageType> damageTypeKey, String message, String chasedMessage ) {
        String typeName = damageTypeKey.location().getPath();
        String baseKey = "death.attack." + DeadlyWorld.MOD_ID + "." + typeName;
        add( baseKey, message );
        add( baseKey + ".player", chasedMessage );
    }
    
    /**
     * Adds a container name translation for the given container ID.<br>
     * We assume we are only adding for our own containers.
     */
    @SuppressWarnings( "SameParameterValue" )
    protected void container( String containerName, String translation ) {
        add( "container." + DeadlyWorld.MOD_ID + "." + containerName, translation );
    }
    
    /** Adds a translation string for the given Jade config option ID. */
    protected void jadeCfgOption( ResourceLocation cfgOptionId, String translation ) {
        add( "config.jade.plugin_" + cfgOptionId.getNamespace() + "." + cfgOptionId.getPath(), translation );
    }
    
    /** Adds a translation string for the given Jade provider element key. */
    protected void jadeProviderElement( ProviderTooltipKey key, String translation ) {
        add( key.langKey(), translation );
    }
    
    /** Auto-generates translations for all blocks in the provided deferred register. */
    @SuppressWarnings( "deprecation" )
    protected void blocks( DeferredRegister<Block> registry ) {
        for( RegistryObject<Block> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();
            
            // Ignore auto-gen blocks, they are handled separately
            if( regObj.get() instanceof IAutoGenBlock )
                continue;
            
            // Key already exists in exceptions, next entry
            if( exceptions.containsKey( key ) )
                continue;
            
            String translation = Objects.requireNonNull( regObj.getId() ).getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );
            
            add( key, translation );
        }
    }
    
    /** Auto-generates translations for all items in the provided deferred register. */
    @SuppressWarnings( "deprecation" )
    protected void items( DeferredRegister<Item> registry ) {
        for( RegistryObject<Item> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();
            
            // Assume block items have already been taken care of
            // since they use their block's description ID normally.
            if( regObj.get() instanceof BlockItem )
                continue;
            
            // Key already exists in exceptions, next entry
            if( exceptions.containsKey( key ) )
                continue;
            
            String translation = Objects.requireNonNull( regObj.getId() ).getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );
            
            add( key, translation );
        }
    }
    
    /** Auto-generates translations for all entity types in the provided deferred register. */
    @SuppressWarnings( { "deprecation", "SameParameterValue" } )
    protected void entityTypes( DeferredRegister<EntityType<?>> registry ) {
        for( RegistryObject<EntityType<?>> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();
            
            // Key already exists in exceptions, next entry
            if( exceptions.containsKey( key ) )
                continue;
            
            String translation = Objects.requireNonNull( regObj.getId() ).getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );
            
            add( key, translation );
        }
    }
    
    /** Auto-generates translations for all mob effects in the provided deferred register. */
    @SuppressWarnings( "deprecation" )
    protected void mobEffects( DeferredRegister<MobEffect> registry ) {
        for( RegistryObject<MobEffect> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();
            
            // Key already exists in exceptions, next entry
            if( exceptions.containsKey( key ) )
                continue;
            
            String translation = Objects.requireNonNull( regObj.getId() ).getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );
            
            add( key, translation );
        }
    }
    
    static class MsgArgs {
        protected static final String _1 = "%1$s";
        protected static final String _2 = "%2$s";
        protected static final String _3 = "%3$s";
    }
}