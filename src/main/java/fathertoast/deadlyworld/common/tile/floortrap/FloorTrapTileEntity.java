package fathertoast.deadlyworld.common.tile.floortrap;

import fathertoast.deadlyworld.common.block.FloorTrapBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.FloorTrapConfig;
import fathertoast.deadlyworld.common.core.config.util.WeightedPotionList;
import fathertoast.deadlyworld.common.core.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FloorTrapTileEntity extends TileEntity implements ITickableTileEntity {

    // Attribute tags
    private static final String TAG_RESET_TIME = "ResetTime";
    private static final String TAG_MAX_TRIGGER_DELAY = "MaxTriggerDelay";
    private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
    private static final String TAG_CHECK_SIGHT = "CheckSight";
    private static final String TAG_TYPE_DATA = "TypeData";

    // Logic tags
    private static final String TAG_DELAY = "Delay";

    // Attributes
    private double activationRange;
    private boolean checkSight;
    private FloorTrapType trapType;
    // Used for potion traps
    private CompoundNBT typeData;
    @Nullable
    private ItemStack potionStack;

    // Logic
    private int maxTriggerDelay;
    /** Count until the trap triggers after being tripped. -1 if the trap has not been tripped. */
    private int triggerDelay = -1;
    private int resetTime;

    public FloorTrapTileEntity() {
        super(DWTileEntities.FLOOR_TRAP.get());
    }

    public void resetTrap( ) { triggerDelay = -1; }

    public void tripTrap( ) { triggerDelay = 0; }

    public void tripTrapRandom( ) { triggerDelay = level.random.nextInt( maxTriggerDelay ); }

    /** Disables the trap for X ticks, resetting it when the count reaches -1 again. */
    public void disableTrap( int duration ) { triggerDelay = -1 - duration; }

    public void disableTrap( ) { triggerDelay = maxTriggerDelay; }

    @Override
    public void onLoad() {
        super.onLoad();
        if( getLevel() == null ) {
            DeadlyWorld.LOG.error( "Failed to load floor trap block entity at \"{}\"", this.getBlockPos() );
            return;
        }
        if( getBlockState().getBlock() instanceof FloorTrapBlock ) {
            DimensionConfigGroup dimConfigs = Config.getDimensionConfigs( getLevel() );
            FloorTrapType trapType = ((FloorTrapBlock) getBlockState().getBlock()).getTrapType();
            this.trapType = trapType;

            initializeFloorTrap( trapType, dimConfigs, getLevel().random );
        }
        else {
            // TODO - Was too tired to use my brain, revisit this
            DeadlyWorld.LOG.error( "Aaaaauughh" );
        }
    }

    public void initializeFloorTrap( FloorTrapType trapType, DimensionConfigGroup dimConfig, Random random ) {
        FloorTrapConfig.FloorTrapTypeCategory trapConfig = trapType.getFeatureConfig( dimConfig );

        // Set attributes from the config
        activationRange = trapConfig.activationRange.get();
        checkSight = trapConfig.checkSight.get();
        // Give each trap a unique trigger delay for funzies. What could it possible be?? Chill trap? Ultra no-chill TNT barrage trap???????
        maxTriggerDelay = 10;
        resetTime = trapConfig.minResetTime.get() + random.nextInt(trapConfig.maxResetTime.get() - trapConfig.minResetTime.get());

        // Set the potion effect if we are a potion trap
        if (trapConfig instanceof FloorTrapConfig.PotionTrapTypeCategory) {
            FloorTrapConfig.PotionTrapTypeCategory potionTrapConfig = (FloorTrapConfig.PotionTrapTypeCategory) trapConfig;

            EffectInstance effectInstance = potionTrapConfig.potionList.get().next(random);

            if (effectInstance == null)
                effectInstance = new EffectInstance(Effects.HARM, 1, 0);

            potionStack = PotionUtils.setCustomEffects(new ItemStack(Items.SPLASH_POTION), Collections.singletonList(effectInstance));
        }
    }

    public CompoundNBT getOrCreateTypeData() {
        return typeData == null ? new CompoundNBT() : typeData;
    }

    @Nonnull
    private FloorTrapType getTrapType( ) {
        if( level != null ) {
            return trapType;
        }
        return FloorTrapType.TNT;
    }

    public PlayerEntity getTarget( ) {
        return TrapHelper.getNearestValidPlayerInRange( level, getBlockPos().above( ), activationRange, checkSight, true );
    }

    @Nonnull
    public ItemStack getPotionStack(WeightedPotionList potionList, Random random) {
        if (potionStack == null || potionStack.isEmpty()) {
            EffectInstance effectInstance = potionList.next(random);
            effectInstance = effectInstance == null ? new EffectInstance(Effects.HARM, 1, 0) : effectInstance;
            return PotionUtils.setCustomEffects(new ItemStack(Items.SPLASH_POTION), Collections.singletonList(effectInstance));
        }
        return potionStack;
    }

    @Override
    public void tick( ) {
        if( !level.isClientSide ) {
            // Run server-side logic
            if( triggerDelay == -1 ) {
                // Check if trap should be tripped
                if( TrapHelper.isValidPlayerInRange( level, getBlockPos().above( ), activationRange, checkSight, true ) ) {
                    tripTrap( );
                    level.playSound( null, getBlockPos().getX( ) + 0.5, getBlockPos().getY( ) + 1, getBlockPos().getZ( ) + 0.5,
                            SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F );
                }
            }
            else {
                // Trap has been tripped or reset with a delay
                triggerDelay++;

                // Trigger trap
                if( triggerDelay == maxTriggerDelay ) {
                    triggerTrap( );
                }
            }
        }
    }

    private void triggerTrap( ) {
        final DimensionConfigGroup dimConfig = Config.getDimensionConfigs(level);
        final FloorTrapType trapType = getTrapType( );

        trapType.triggerTrap( dimConfig, this );
        disableTrap(-resetTime);
    }

    @Override
    public CompoundNBT save( CompoundNBT tag ) {
        super.save( tag );

        // Attributes
        tag.putInt( TAG_RESET_TIME, resetTime );
        tag.putInt( TAG_MAX_TRIGGER_DELAY, maxTriggerDelay );
        tag.putDouble( TAG_ACTIVATION_RANGE, activationRange );
        tag.putBoolean( TAG_CHECK_SIGHT, checkSight );


        typeData = getOrCreateTypeData();
        maybeSavePotionStack(typeData);
        tag.put( TAG_TYPE_DATA, typeData );

        // Logic
        tag.putInt( TAG_DELAY, triggerDelay );

        return tag;
    }

    @Override
    public void load( BlockState state, CompoundNBT tag ) {
        super.load( state, tag );

        // Attributes
        if ( tag.contains( TAG_RESET_TIME, TrapHelper.NBT_TYPE_PRIMITIVE )) {
            resetTime = tag.getInt( TAG_RESET_TIME );
        }
        if ( tag.contains( TAG_MAX_TRIGGER_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            maxTriggerDelay = tag.getInt( TAG_MAX_TRIGGER_DELAY );
        }
        if( tag.contains( TAG_ACTIVATION_RANGE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            activationRange = tag.getFloat( TAG_ACTIVATION_RANGE );
        }
        if( tag.contains( TAG_CHECK_SIGHT, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            checkSight = tag.getBoolean( TAG_CHECK_SIGHT );
        }

        if ( tag.contains( TAG_TYPE_DATA, tag.getId() )) {
            typeData = tag.getCompound( TAG_TYPE_DATA );
            maybeLoadPotionStack(typeData);
        }
        else {
            typeData = getOrCreateTypeData();
        }

        // Logic
        if( tag.contains( TAG_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            triggerDelay = tag.getInt( TAG_DELAY );
        }
    }

    private void maybeSavePotionStack( CompoundNBT trapData ) {
        if (potionStack != null && !potionStack.isEmpty()) {
            List<EffectInstance> effects = PotionUtils.getCustomEffects(potionStack);

            if (!effects.isEmpty()) {
                EffectInstance effect = effects.get(0);
                CompoundNBT potionTag = new CompoundNBT();

                potionTag.putString("Effect", effect.getEffect().getRegistryName().toString());
                potionTag.putInt("Duration", effect.getDuration());
                potionTag.putInt("Amplifier", effect.getAmplifier());

                trapData.put("PotionType", potionTag);
            }
        }
    }

    private void maybeLoadPotionStack( CompoundNBT trapData ) {
        final String TAG_POTION_TYPE = "PotionType";
        EffectInstance effectInstance;

        if ( trapData.contains(TAG_POTION_TYPE, Constants.NBT.TAG_COMPOUND )) {
            CompoundNBT potionData = trapData.getCompound( TAG_POTION_TYPE );

            if ( potionData.contains( "Effect", TrapHelper.NBT_TYPE_STRING )
                    && potionData.contains( "Duration", TrapHelper.NBT_TYPE_PRIMITIVE )
                    && potionData.contains( "Amplifier", TrapHelper.NBT_TYPE_PRIMITIVE )) {

                Effect effect = Effects.HARM;
                int duration;
                int amplifier;

                ResourceLocation effectId = ResourceLocation.tryParse(potionData.getString("Effect"));

                if (effectId != null) {
                    if (ForgeRegistries.POTIONS.containsKey(effectId)) {
                        effect = ForgeRegistries.POTIONS.getValue(effectId);
                    }
                }
                duration = potionData.getInt("Duration");
                amplifier = potionData.getInt("Amplifier");

                effectInstance = new EffectInstance(effect, duration, amplifier);
            }
            else {
                effectInstance = new EffectInstance(Effects.HARM, 1, 0);
            }
            potionStack = PotionUtils.setCustomEffects(new ItemStack(Items.SPLASH_POTION), Collections.singletonList(effectInstance));
        }
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
}
