package fathertoast.deadlyworld.common.config.value;


import fathertoast.crust.api.config.common.value.collection.FuzzyValueList;
import fathertoast.crust.api.config.common.value.collection.RegistryValueList;
import fathertoast.crust.api.config.common.value.collection.key.IMultiKey;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.MobEffectStats;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;

/**
 * A fuzzy list used to iterate over mob effect instances via {@link #potionEntries()}.
 *
 * @see MobEffectInstance
 * @see RegObjKey
 * @see MobEffectWeightedList
 * @see MobEffectListField
 */
@SuppressWarnings( "unused" )
public class MobEffectList extends RegistryValueList<MobEffect, MobEffectStats> {
    
    /** @return The key-value pair converted to a new mob effect instance. */
    @Nullable
    public static MobEffectInstance toInstance( @Nullable FuzzyValueList.Pair<MobEffect, MobEffectStats> pair ) {
        return pair == null ? null : new MobEffectInstance( pair.key(),
                pair.value().duration.get(), pair.value().amplifier.get() );
    }
    
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public MobEffectList() { super( ForgeRegistries.MOB_EFFECTS, MobEffectStats.CODEC ); }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryValueList.Builder} is much easier.
     */
    @SafeVarargs
    public MobEffectList( FuzzyEntry<MobEffect, MobEffectStats>... keys ) {
        super( IRegWrapper.of( ForgeRegistries.MOB_EFFECTS ), MobEffectStats.CODEC, keys );
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link RegistryValueList.Builder} is much easier.
     */
    public MobEffectList( Collection<FuzzyEntry<MobEffect, MobEffectStats>> keys ) {
        super( IRegWrapper.of( ForgeRegistries.MOB_EFFECTS ), MobEffectStats.CODEC, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public MobEffectList makeNew() { return new MobEffectList(); }
    
    
    /**
     * @return An iterator over the key-value pairs represented by the keys in this list that can be used in
     * an enhanced for loop. The iterator skips over null objects, but it may still return null in some cases.
     */
    public Iterator<MobEffectInstance> potionEntries() {
        return new IMultiKey.ConverterIterator<>( entries(), MobEffectList::toInstance );
    }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry value lists smoother. */
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<MobEffect, MobEffectStats, MobEffectList, B> {
        
        public final IRegWrapper<MobEffect> registry = IRegWrapper.of( ForgeRegistries.MOB_EFFECTS );
        
        public Builder() { super( MobEffectStats.CODEC ); }
        
        /** @return A new fuzzy value list reflecting the current state of this builder. */
        @Override
        public MobEffectList build() { return new MobEffectList( list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a mob effect instance based on the resource location. */
        public B put( String resLoc, int duration, int amplifier ) { return put( RegObjKey.of( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the resource location. */
        public B put( ResourceLocation resLoc, int duration, int amplifier ) { return put( RegObjKey.of( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the registry object. */
        public B put( RegistryObject<? extends MobEffect> regObj, int duration, int amplifier ) { return put( RegObjKey.of( registry, regObj, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the resource key. */
        public B put( ResourceKey<? extends MobEffect> resKey, int duration, int amplifier ) { return put( RegObjKey.of( registry, resKey, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the registered object. Only suitable for vanilla mob effects. */
        public B put( MobEffect obj, int duration, int amplifier ) { return put( RegObjKey.of( registry, obj, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag mob effect instance based on the resource location. Tag keys add the tag's entire contents to the iterator, with the same duration & amplifier for each. */
        public B putTag( String resLoc, int duration, int amplifier ) { return put( RegObjKey.ofTag( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a tag mob effect instance based on the resource location. Tag keys add the tag's entire contents to the iterator, with the same duration & amplifier for each. */
        public B putTag( ResourceLocation resLoc, int duration, int amplifier ) { return put( RegObjKey.ofTag( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a tag mob effect instance based on the tag. Tag keys add the tag's entire contents to the iterator, with the same duration & amplifier for each. */
        public B putTag( TagKey<MobEffect> tag, int duration, int amplifier ) { return put( RegObjKey.ofTag( registry, tag, false ), MobEffectStats.of( duration, amplifier ) ); }
    }
}