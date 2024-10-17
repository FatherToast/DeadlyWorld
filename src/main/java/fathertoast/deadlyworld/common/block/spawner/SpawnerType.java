package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.deadlyworld.common.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.SpawnerConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Supplier;

public enum SpawnerType {
    
    // Standalone features
    DEFAULT( "simple", ( dimConfigs ) -> dimConfigs.SPAWNERS.LONE ),
    STREAM( "stream", ( dimConfigs ) -> dimConfigs.SPAWNERS.STREAM ),
    SWARM( "swarm", ( dimConfigs ) -> dimConfigs.SPAWNERS.SWARM ),
    BRUTAL( "brutal", ( dimConfigs ) -> dimConfigs.SPAWNERS.BRUTAL ) {
        /** Applies any additional modifiers to entities spawned by spawners of this type. */
        @Override
        public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, Level level, BlockPos pos ) {
            super.initEntity( entity, dimConfigs, level, pos );
            
            // Apply potion effects
            if( !(entity instanceof Creeper) ) {
                final boolean hide = dimConfigs.SPAWNERS.BRUTAL.ambientFx.get();
                if( dimConfigs.SPAWNERS.BRUTAL.fireResistance.get() ) {
                    entity.addEffect( new MobEffectInstance( MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
                if( dimConfigs.SPAWNERS.BRUTAL.waterBreathing.get() ) {
                    entity.addEffect( new MobEffectInstance( MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
            }
        }
    },
    NEST( "nest", "silverfish nest", ( dimConfigs ) -> dimConfigs.SPAWNERS.NEST ),
    MINI( "mini", ( dimConfigs ) -> dimConfigs.SPAWNERS.MINI ) {
        //        @Override
        //        public Supplier<DeadlySpawnerBlock> getBlock() { return MiniSpawnerBlock::new; }
    },
    
    // Subfeatures
    DUNGEON( "dungeon", true, ( dimConfigs ) -> dimConfigs.SPAWNERS.DUNGEON );
    
    /** The path for loot tables associated with these types. */
    public static final String LOOT_TABLE_PATH = "deadly_spawners/";
    
    public static final String CATEGORY = "spawner";
    
    /** The unique id for this spawner type. This is used to save and load from disk. */
    private final String id;
    /** A human-readable name for this spawner type. Used in config descriptions, usually followed by " spawner" or " spawners". */
    private final String displayName;
    /** A function that returns the feature config associated with this spawner type for a given dimension config. */
    private final Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configGetter;
    
    /** True if this spawner type is used as part of another feature. */
    private final boolean subfeature;
    
    SpawnerType( String name, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        this( name, false, configFunction );
    }
    
    SpawnerType( String name, boolean sub, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        this( name, name.replace( "_", " " ) + " spawner", sub, configFunction );
    }
    
    SpawnerType( String name, String prettyName, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        this( name, prettyName, false, configFunction );
    }
    
    SpawnerType( String name, String prettyName, boolean sub, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        id = name;
        displayName = prettyName;
        configGetter = configFunction;
        subfeature = sub;
    }
    
    public String getDisplayName() { return displayName; }
    
    /** @return True if this type is a subfeature; false if it is a standalone feature. */
    public final boolean isSubfeature() { return subfeature; }
    
    /** @return A Supplier of the Spawner Block to register for this Spawner Type */
    public Supplier<DeadlySpawnerBlock> getBlock() { return () -> new DeadlySpawnerBlock( this ); }
    
    /**
     * Returns a SpawnerType from ID.
     * If there exists no SpawnerType with the given ID, default to {@link SpawnerType#DEFAULT}
     *
     * @param ID The ID of the SpawnerType.
     * @return A SpawnerType matching the given ID.
     */
    public static SpawnerType getFromID( String ID ) {
        for( SpawnerType spawnerType : values() ) {
            if( spawnerType.toString().equals( ID ) ) {
                return spawnerType;
            }
        }
        return DEFAULT;
    }
    
    @Override
    public String toString() { return id; }
    
    public ResourceLocation getChestLootTable() {
        return DeadlyWorld.resourceLoc( References.CHEST_LOOT_PATH + LOOT_TABLE_PATH + this );
    }
    
    public SpawnerConfig.SpawnerTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configGetter.apply( dimConfigs ); }
    
    /* TODO - Move decoration to the Feature itself
    public abstract
    void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, DimensionConfig dimConfig, World world, Random random );
    */
    
    /** Applies any additional modifiers to entities spawned by spawners of this type. */
    public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, Level level, BlockPos pos ) {
        final SpawnerConfig.SpawnerTypeCategory config = getFeatureConfig( dimConfigs );
        
        // Apply attribute modifiers
        addModifier( entity, Attributes.FOLLOW_RANGE, config.addedFollowRange.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.MAX_HEALTH, config.addedMaxHealth.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.MAX_HEALTH, config.increasedMaxHealth.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        addModifier( entity, Attributes.KNOCKBACK_RESISTANCE, config.addedKnockbackResist.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ARMOR, config.addedArmor.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ARMOR_TOUGHNESS, config.addedArmorToughness.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ATTACK_DAMAGE, config.addedDamage.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ATTACK_DAMAGE, config.increasedDamage.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        addModifier( entity, Attributes.ATTACK_KNOCKBACK, config.addedKnockback.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.MOVEMENT_SPEED, config.increasedSpeed.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        addModifier( entity, Attributes.FLYING_SPEED, config.increasedSpeed.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        
        entity.setHealth( entity.getMaxHealth() );
    }
    
    /** Adds a custom attribute modifier to the entity. */
    private void addModifier( LivingEntity entity, Attribute attribute, double value, AttributeModifier.Operation operation ) {
        if( value != 0.0 ) {
            AttributeInstance attributeInstance = entity.getAttribute( attribute );
            if( attributeInstance != null ) {
                attributeInstance.addPermanentModifier(
                        new AttributeModifier( DeadlyWorld.MOD_ID + ":" + id + " spawner bonus", value, operation ) );
            }
        }
    }
    
    public static SpawnerType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to load invalid spawner type from index '{}'", index );
            return DEFAULT;
        }
        return values()[index];
    }
    
    private static void addAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        AttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() + amount );
        }
    }
    
    private static void multAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        AttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() * amount );
        }
    }
}