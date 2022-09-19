package fathertoast.deadlyworld.common.tile.spawner;

import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.MiniSpawnerBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.SpawnerConfig;
import fathertoast.deadlyworld.common.util.References;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public enum SpawnerType implements IStringSerializable {
    
    // Standalone features
    DEFAULT( "simple", ( dimConfigs ) -> dimConfigs.SPAWNERS.LONE ),
    STREAM( "stream", ( dimConfigs ) -> dimConfigs.SPAWNERS.STREAM ),
    SWARM( "swarm", ( dimConfigs ) -> dimConfigs.SPAWNERS.SWARM ),
    BRUTAL( "brutal", ( dimConfigs ) -> dimConfigs.SPAWNERS.BRUTAL ) {
        /** Applies any additional modifiers to entities spawned by spawners of this type. */
        @Override
        public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, World world, BlockPos pos ) {
            super.initEntity( entity, dimConfigs, world, pos );
            
            // Apply potion effects
            if( !(entity instanceof CreeperEntity) ) {
                final boolean hide = dimConfigs.SPAWNERS.BRUTAL.ambientFx.get();
                if( dimConfigs.SPAWNERS.BRUTAL.fireResistance.get() ) {
                    entity.addEffect( new EffectInstance( Effects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
                if( dimConfigs.SPAWNERS.BRUTAL.waterBreathing.get() ) {
                    entity.addEffect( new EffectInstance( Effects.WATER_BREATHING, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
            }
        }
    },
    NEST( "nest", "silverfish nest", ( dimConfigs ) -> dimConfigs.SPAWNERS.NEST ),
    MINI( "mini", ( dimConfigs ) -> dimConfigs.SPAWNERS.MINI ) {
        @Override
        public Supplier<DeadlySpawnerBlock> getBlock() {
            return MiniSpawnerBlock::new;
        }
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
    private final Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction;
    
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
        this.id = name;
        this.displayName = prettyName;
        this.configFunction = configFunction;
        this.subfeature = sub;
    }
    
    @Override
    public String getSerializedName() { return id; }

    public String getDisplayName() {
        return displayName;
    }
    
    /** @return True if this type is a subfeature; false if it is a standalone feature. */
    public final boolean isSubfeature() { return subfeature; }
    
    /** @return A Supplier of the Spawner Block to register for this Spawner Type */
    public Supplier<DeadlySpawnerBlock> getBlock() { return () -> new DeadlySpawnerBlock(this); }

    /**
     * Returns a SpawnerType from ID.
     * If there exists no SpawnerType with the given ID, default to {@link SpawnerType#DEFAULT}
     *
     * @param ID The ID of the SpawnerType.
     * @return A SpawnerType matching the given ID.
     */
    @Nonnull
    public static SpawnerType getFromID( String ID ) {
        for( SpawnerType spawnerType : values() ) {
            if( spawnerType.getSerializedName().equals( ID ) ) {
                return spawnerType;
            }
        }
        return DEFAULT;
    }
    
    @Override
    public String toString() { return getSerializedName(); }
    
    public ResourceLocation getChestLootTable() {
        return DeadlyWorld.resourceLoc( References.CHEST_LOOT_PATH + LOOT_TABLE_PATH + this );
    }
    
    public SpawnerConfig.SpawnerTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configFunction.apply( dimConfigs ); }
    
    /* TODO - Move decoration to the Feature itself
    public abstract
    void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, DimensionConfig dimConfig, World world, Random random );
    */
    
    /** Applies any additional modifiers to entities spawned by spawners of this type. */
    public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, World world, BlockPos pos ) {
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
            ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );
            if( attributeInstance != null ) {
                attributeInstance.addPermanentModifier(
                        new AttributeModifier( DeadlyWorld.MOD_ID + ":" + this.id + " spawner bonus", value, operation ) );
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
        ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() + amount );
        }
    }
    
    private static void multAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() * amount );
        }
    }
}