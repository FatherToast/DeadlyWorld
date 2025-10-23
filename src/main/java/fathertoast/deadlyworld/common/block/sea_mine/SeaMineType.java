package fathertoast.deadlyworld.common.block.sea_mine;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.deadlyworld.common.block.IFeatureConfigProvider;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.TowerConfig;
import fathertoast.deadlyworld.common.config.dimension.WaterTrapConfig;
import fathertoast.deadlyworld.common.config.field.WeightedPotionList;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings( "ConstantConditions" )
public enum SeaMineType implements IFeatureConfigProvider<WaterTrapConfig.SeaMineCategory> {

    NORMAL( "normal", 6.0F,
            new WeightedPotionList(),
            ( dimConfigs ) -> dimConfigs.WATER_TRAPS.NORMAL_SEA_MINE ),
    PUFFER( "puffer", 4.0F,
            new WeightedPotionList(
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.POISON ), 100, 280, 0 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.POISON ), 60, 280, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.POISON ), 10, 280, 2 )
            ),
            ( dimConfigs ) -> dimConfigs.WATER_TRAPS.PUFFER_SEA_MINE ),
    GUARDIAN( "guardian", 4.5F,
            new WeightedPotionList(
                    new RegistryValueEntry<>( ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, CrustObjects.ID.WEIGHT ), 100, 400, 2 ),
                    new RegistryValueEntry<>( ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, CrustObjects.ID.WEIGHT ), 20, 400, 4 )

            ),
            ( dimConfigs ) -> dimConfigs.WATER_TRAPS.GUARDIAN_SEA_MINE );

    public static final String BLOCK_CATEGORY = "sea_mine";

    /** The unique id for this sea mine type. This is used to save and load from disk. */
    private final String id;
    private final String displayName;

    /** The default explosion power for this mine type. */
    private final float defaultExplosionPower;
    /** The default potion list for this mine type. */
    private final WeightedPotionList defaultPotions;
    /** A function that returns the feature config associated with this sea mine type for a given dimension config. */
    private final Function<DimensionConfigGroup, WaterTrapConfig.SeaMineCategory> configGetter;


    SeaMineType( String name, float explosionPower, WeightedPotionList potions, Function<DimensionConfigGroup, WaterTrapConfig.SeaMineCategory> configFunction ) {
        this( name, name.replace( "_", " " ) + " sea mines", explosionPower, potions, configFunction );
    }

    SeaMineType( String name, String prettyName, float explPower, WeightedPotionList potions, Function<DimensionConfigGroup, WaterTrapConfig.SeaMineCategory> configFunction ) {
        id = name;
        displayName = prettyName;
        defaultExplosionPower = explPower;
        defaultPotions = potions;
        configGetter = configFunction;
    }

    public String getDisplayName() { return displayName; }

    /** @return A Supplier of the Sea Mine Block to register for this Sea Mine Type */
    public Supplier<SeaMineBlock> getBlock() { return () -> new SeaMineBlock( this ); }

    /** @return The default explosion power for this Sea Mine type. */
    public float defaultExplosionPower() {
        return defaultExplosionPower;
    }

    /** @return The default potion list for this Sea Mine type. */
    public WeightedPotionList defaultPotions() {
        return defaultPotions;
    }

    /**
     * Returns a SeaMineType from ID.
     * If there exists no SeaMineType with the given ID, default to {@link SeaMineType#NORMAL}
     *
     * @param ID The ID of the SeaMineType.
     * @return A SeaMineType matching the given ID.
     */
    public static SeaMineType getFromID( String ID ) {
        for( SeaMineType seaMineType : values() ) {
            if( seaMineType.toString().equals( ID ) ) {
                return seaMineType;
            }
        }
        return NORMAL;
    }

    @Override
    public String toString() { return id; }

    @Override
    public WaterTrapConfig.SeaMineCategory getConfig(Level level ) {
        return configGetter.apply( Config.getDimensionConfigs( level ) );
    }

    @Override
    public WaterTrapConfig.SeaMineCategory getConfig( DimensionConfigGroup dimConfig ) {
        return configGetter.apply( dimConfig );
    }

    public static SeaMineType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to fetch invalid sea mine type from index '{}'", index );
            return NORMAL;
        }
        return values()[index];
    }
}
