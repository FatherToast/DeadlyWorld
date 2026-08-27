package fathertoast.deadlyworld.common.config.value;

import fathertoast.crust.api.config.common.value.collection.RegistryWeightedValueList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.collection.value.MobEffectStats;
import fathertoast.crust.api.config.common.value.collection.value.WeightedEntry;
import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

/**
 * A fuzzy weighted list used to randomly pick mob effect instances via {@link #nextPotion(RandomSource)}.
 *
 * @see MobEffectInstance
 * @see RegObjKey
 * @see MobEffectList
 * @see MobEffectWeightedListField
 */
@SuppressWarnings( "unused" )
public class MobEffectWeightedList extends RegistryWeightedValueList<MobEffect, MobEffectStats> {
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public MobEffectWeightedList() { super( ForgeRegistries.MOB_EFFECTS, MobEffectStats.CODEC ); }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link Builder} is much easier.
     */
    @SafeVarargs
    public MobEffectWeightedList( WeightedEntry<MobEffect, MobEffectStats>... keys ) {
        super( IRegWrapper.of( ForgeRegistries.MOB_EFFECTS ), MobEffectStats.CODEC, keys );
    }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link Builder} is much easier.
     */
    public MobEffectWeightedList( Collection<WeightedEntry<MobEffect, MobEffectStats>> keys ) {
        super( IRegWrapper.of( ForgeRegistries.MOB_EFFECTS ), MobEffectStats.CODEC, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public MobEffectWeightedList makeNew() { return new MobEffectWeightedList(); }
    
    
    /**
     * @return A randomly chosen mob effect from this list with its duration and amplifier, or
     * null if a null entry is selected or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public MobEffectInstance nextPotion( Random random ) { return nextPotion( JavaRandomSource.of( random ) ); }
    
    /**
     * @return A randomly chosen mob effect from this list with its duration and amplifier, or
     * null if a null entry is selected or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public MobEffectInstance nextPotion( RandomSource random ) { return MobEffectList.toInstance( next( random ) ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing registry weighted value lists smoother. */
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<MobEffect, MobEffectStats, MobEffectWeightedList, B> {
        
        public final IRegWrapper<MobEffect> registry = IRegWrapper.of( ForgeRegistries.MOB_EFFECTS );
        
        public Builder() { super( MobEffectStats.CODEC ); }
        
        /** @return A new fuzzy weighted value list reflecting the current state of this builder. */
        @Override
        public MobEffectWeightedList build() { return new MobEffectWeightedList( list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a mob effect instance based on the resource location. */
        public B put( int weight, String resLoc, int duration, int amplifier ) { return put( weight, RegObjKey.of( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the resource location. */
        public B put( int weight, ResourceLocation resLoc, int duration, int amplifier ) { return put( weight, RegObjKey.of( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the registry object. */
        public B put( int weight, RegistryObject<? extends MobEffect> regObj, int duration, int amplifier ) { return put( weight, RegObjKey.of( registry, regObj, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the resource key. */
        public B put( int weight, ResourceKey<? extends MobEffect> resKey, int duration, int amplifier ) { return put( weight, RegObjKey.of( registry, resKey, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a mob effect instance based on the registered object. Only suitable for vanilla mob effects. */
        public B put( int weight, MobEffect obj, int duration, int amplifier ) { return put( weight, RegObjKey.of( registry, obj, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag mob effect instance based on the resource location. Tag keys return a uniform random mob effect from the tag's contents when picked. */
        public B putTag( int weight, String resLoc, int duration, int amplifier ) { return put( weight, RegObjKey.ofTag( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a tag mob effect instance based on the resource location. Tag keys return a uniform random mob effect from the tag's contents when picked. */
        public B putTag( int weight, ResourceLocation resLoc, int duration, int amplifier ) { return put( weight, RegObjKey.ofTag( registry, resLoc, false ), MobEffectStats.of( duration, amplifier ) ); }
        
        /** Adds a tag mob effect instance based on the tag. Tag keys return a uniform random mob effect from the tag's contents when picked. */
        public B putTag( int weight, TagKey<MobEffect> tag, int duration, int amplifier ) { return put( weight, RegObjKey.ofTag( registry, tag, false ), MobEffectStats.of( duration, amplifier ) ); }
    }
}