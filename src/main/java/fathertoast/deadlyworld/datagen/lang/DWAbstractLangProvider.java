package fathertoast.deadlyworld.datagen.lang;

import fathertoast.deadlyworld.common.block.infested.DeadlyInfestedBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWCreativeModeTabs;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvent;
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

    /**
     * Called before exception translations are processed.<br>
     * Add any exceptions here.
     */
    protected abstract void addExceptions();

    /** Adds an exception translation to the map of translation exceptions. */
    protected void exception( String key, String translation ) {
        Objects.requireNonNull( key );
        Objects.requireNonNull( translation );
        exceptions.put( key, translation );
    }

    protected void creativeTab( DWCreativeModeTabs.CreativeTabRegObj regObj, String translation ) {
        // Assume display name is a translatable component
        try {
            add(((TranslatableContents) regObj.regObj().get().getDisplayName().getContents()).getKey(), translation);
        }
        catch ( ClassCastException e ) {
            e.printStackTrace();
            DeadlyWorld.LOG.error( "Attempted to generate localization for creative mode tab with a display name component that doesn't have translatable content!" );
        }
    }

    protected void soundSubtitle( RegistryObject<SoundEvent> regObj, String translation ) {
        String key = "sound_event." + regObj.getId().getNamespace() + ".subtitle." + regObj.getId().getPath();
        add( key, translation );
    }

    protected void tooltip( RegistryObject<? extends Item> regObj, @Nullable String subKey, String translation ) {
        StringBuilder builder = new StringBuilder( regObj.get().getDescriptionId() );
        builder.append( ".tooltip" );

        if ( subKey != null && !subKey.isEmpty() ) {
            builder.append( "." );
            builder.append( subKey );
        }
        add( builder.toString(), translation );
    }

    protected void tooltip( RegistryObject<? extends Item> regObj, String translation ) {
        tooltip( regObj, null, translation );
    }

    protected void container( String containerName, String translation ) {
        add( "container." + DeadlyWorld.MOD_ID + "." + containerName , translation );
    }

    protected void blocks( DeferredRegister<Block> registry ) {
        for ( RegistryObject<Block> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();

            // Ignore infested blocks, they are handled manually
            if ( regObj.get() instanceof DeadlyInfestedBlock )
                continue;

            // Key already exists in exceptions, next entry
            if ( exceptions.containsKey( key ) )
                continue;

            String translation = regObj.getId().getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );

            add( key, translation );
        }
    }

    protected void items( DeferredRegister<Item> registry ) {
        for ( RegistryObject<Item> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();

            // Assume block items have already been taken care of
            // since they use their block's description ID normally.
            if ( regObj.get() instanceof BlockItem )
                continue;

            // Key already exists in exceptions, next entry
            if ( exceptions.containsKey( key ) )
                continue;

            String translation = regObj.getId().getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );

            add( key, translation );
        }
    }

    protected void entityTypes( DeferredRegister<EntityType<?>> registry ) {
        for ( RegistryObject<EntityType<?>> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();

            // Key already exists in exceptions, next entry
            if ( exceptions.containsKey( key ) )
                continue;

            String translation = regObj.getId().getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );

            add( key, translation );
        }
    }

    protected void mobEffects( DeferredRegister<MobEffect> registry ) {
        for ( RegistryObject<MobEffect> regObj : registry.getEntries() ) {
            String key = regObj.get().getDescriptionId();

            // Key already exists in exceptions, next entry
            if ( exceptions.containsKey( key ) )
                continue;

            String translation = regObj.getId().getPath().replaceAll( "_", " " );
            translation = WordUtils.capitalizeFully( translation );

            add( key, translation );
        }
    }
}
