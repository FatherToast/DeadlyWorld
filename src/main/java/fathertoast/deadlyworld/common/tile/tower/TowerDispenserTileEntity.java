package fathertoast.deadlyworld.common.tile.tower;

import fathertoast.deadlyworld.common.block.TowerDispenserBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.TowerDispenserConfig;
import fathertoast.deadlyworld.common.core.config.util.WeightedPotionList;
import fathertoast.deadlyworld.common.core.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TowerDispenserTileEntity extends TileEntity implements ITickableTileEntity {

    // Attribute tags
    private static final String TAG_POTION_TYPE = "PotionType";
    private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
    private static final String TAG_CHECK_SIGHT = "CheckSight";
    private static final String TAG_DAMAGE = "Damage";
    private static final String TAG_DELAY_MIN = "DelayMin";
    private static final String TAG_DELAY_MAX = "DelayMax";
    private static final String TAG_TYPE_DATA = "TypeData";

    // Logic tags
    private static final String TAG_DELAY = "Delay";

    // Attributes
    private TowerType towerType;
    private double activationRange;
    private boolean checkSight;

    private float attackDamage;

    private int minAttackDelay;
    private int maxAttackDelay;

    // For potion tower
    @Nullable
    private EffectInstance potionEffect;
    private CompoundNBT typeData;


    // Logic
    /** Whether this tower trap is active. Reduces the number of times we need to iterate over the player list. */
    private boolean activated;
    /** Countdown until the next activation check. */
    private int activationDelay;

    /** Countdown until the next attack attempt. If this is set below 0, the countdown is reset without attempting to attack. */
    private int attackDelay = 10;


    public TowerDispenserTileEntity() { super( DWTileEntities.TOWER_DISPENSER.get() ); }

    public TowerDispenserTileEntity(TileEntityType<?> type) {
        super(type);
    }

    // Initializing the tile entity here
    // when it is safe to do so.
    @Override
    public void onLoad() {
        if( level == null ) {
            DeadlyWorld.LOG.error( "Failed to load tower dispenser block entity at \"{}\"", this.getBlockPos() );
            return;
        }
        if( getBlockState().getBlock() instanceof TowerDispenserBlock) {
            DimensionConfigGroup dimConfigs = Config.getDimensionConfigs( level );
            TowerType towerType = ((TowerDispenserBlock) getBlockState().getBlock()).getTowerType();
            this.towerType = towerType;

            this.initializeTowerTrap( towerType, dimConfigs, level.random );
        }
        else {
            DeadlyWorld.LOG.error( "Attempted to initialize Tower Dispenser block entity with the wrong type of block! TileEntity:{}, Block:{}",
                    this.getType().getRegistryName(),
                    getBlockState().getBlock().getRegistryName() );
        }
    }

    public void initializeTowerTrap( TowerType towerType, DimensionConfigGroup dimConfig, Random random ) {
        TowerDispenserConfig.TowerDispenserTypeCategory towerConfig = towerType.getFeatureConfig( dimConfig );

        // Set attributes from the config
        activationRange = towerConfig.activationRange.get();
        checkSight = towerConfig.checkSight.get();

        attackDamage = (float) towerConfig.attackDamage.get();

        minAttackDelay = towerConfig.minAttackDelay.get();
        maxAttackDelay = towerConfig.maxAttackDelay.get();

        if ( towerConfig instanceof TowerDispenserConfig.PotionTowerDispenserTypeCategory ) {
            if (potionEffect == null) {
                TowerDispenserConfig.PotionTowerDispenserTypeCategory potionTowerConfig = (TowerDispenserConfig.PotionTowerDispenserTypeCategory) towerConfig;

                EffectInstance effectInstance = potionTowerConfig.potionList.get().next(random);

                if (effectInstance == null)
                    effectInstance = new EffectInstance(Effects.HARM, 1, 0);

                potionEffect = effectInstance;
            }
        }
    }

    public CompoundNBT getOrCreateTypeData() {
        return typeData == null ? new CompoundNBT() : typeData;
    }

    private TowerType getTowerType( ) {
        if( level != null ) {
            BlockState state = level.getBlockState(getBlockPos());

            if (state.getBlock() instanceof TowerDispenserBlock) {
                return ((TowerDispenserBlock) state.getBlock()).getTowerType();
            }
        }
        return TowerType.SIMPLE;
    }

    @Nonnull
    public EffectInstance getPotionEffect(WeightedPotionList potionList, Random random) {
        if (potionEffect == null) {
            EffectInstance effectInstance = potionList.next(random);

            if (effectInstance == null)
                effectInstance = new EffectInstance( Effects.HARM, 1, 0 );
            potionEffect = effectInstance;
        }
        return potionEffect;
    }

    @Override
    public void tick( ) {
        // Update activation status
        if( activationDelay > 0 ) {
            activationDelay--;
        }
        else {
            activationDelay = 4;
            activated = TrapHelper.isValidPlayerInRange( level, getBlockPos(), activationRange, false, false );
        }

        if( level.isClientSide ) {
            // Run client-side effects
            if( activated ) {
                BlockPos pos = getBlockPos();
                double yPos = pos.getY( ) + level.random.nextFloat( );
                double xPos, zPos;

                float faceOffset = 1.0625F;
                if( level.random.nextBoolean( ) ) {
                    faceOffset = 1 - faceOffset;
                }
                if( level.random.nextBoolean( ) ) {
                    xPos = pos.getX( ) + faceOffset;
                    zPos = pos.getZ( ) + level.random.nextFloat( );
                }
                else {
                    xPos = pos.getX( ) + level.random.nextFloat( );
                    zPos = pos.getZ( ) + faceOffset;
                }
                level.addParticle( ParticleTypes.SMOKE, xPos, yPos, zPos, 0.0, 0.0, 0.0 );
            }
        }
        else {
            // Run server-side logic
            if( activated ) {
                if( attackDelay < 0 ) {
                    resetTimer( );
                }

                if( attackDelay > 0 ) {
                    // Tower is on cooldown
                    attackDelay--;
                }
                else {
                    // Attempt to attack the nearest player
                    PlayerEntity target = TrapHelper.getNearestValidPlayerInRange( level, getBlockPos(), activationRange, checkSight, false );
                    if( target == null ) {
                        // Failed sight check; impose a small delay so we don't spam ray traces
                        attackDelay = 6 + level.random.nextInt( 8 );
                    }
                    else {
                        attack( target );
                    }
                }
            }
        }
    }

    private void attack( PlayerEntity target ) {
        resetTimer( );

        final DimensionConfigGroup dimConfig = Config.getDimensionConfigs( level );
        final TowerType towerType = getTowerType( );

        BlockPos pos = getBlockPos();
        Vector3d centerPos = new Vector3d( pos.getX(), pos.getY(), pos.getZ() ).add( 0.5, 0.5, 0.5 );
        Vector3d targetPos = new Vector3d( target.getX(), target.getBoundingBox( ).minY + target.getBbHeight() / 3.0F, target.getZ() );
        Vector3d vecToTarget = targetPos.subtract( centerPos );

        if( Math.abs( vecToTarget.x ) < 0.5 && Math.abs( vecToTarget.z ) < 0.5 ) {
            // Target is directly above or below the tower, can't hit it
            return;
        }
        double distanceH = Math.sqrt( vecToTarget.x * vecToTarget.x + vecToTarget.z * vecToTarget.z );

        // Determine the offset to spawn the arrow at so it doesn't clip the dispenser block
        Vector3d offset;
        if( Math.abs( vecToTarget.x ) < Math.abs( vecToTarget.z ) ) {
            offset = new Vector3d(
                    vecToTarget.x / distanceH,
                    0.0,
                    vecToTarget.z < 0.0 ? -1.0 : 1.0
            );
        }
        else if( Math.abs( vecToTarget.x ) > Math.abs( vecToTarget.z ) ) {
            offset = new Vector3d(
                    vecToTarget.x < 0.0 ? -1.0 : 1.0,
                    0.0,
                    vecToTarget.z / distanceH
            );
        }
        else {
            offset = new Vector3d(
                    vecToTarget.x < 0.0 ? -1.0 : 1.0,
                    0.0,
                    vecToTarget.z < 0.0 ? -1.0 : 1.0
            );
        }

        // Allow the tower type to actually execute the attack
        towerType.triggerAttack( dimConfig, this, target, centerPos, offset, vecToTarget, distanceH );

        level.playSound( null, centerPos.x, centerPos.y, centerPos.z, SoundEvents.DISPENSER_LAUNCH, SoundCategory.BLOCKS,
                1.0F, 1.0F / (level.random.nextFloat( ) * 0.4F + 0.8F) );
    }

    @SuppressWarnings("ConstantConditions")
    public void shootArrow( Vector3d center, Vector3d offset, Vector3d vecToTarget, double distanceH, float velocity, float variance, AbstractArrowEntity arrow ) {
        final double spawnOffset = 0.6;

        arrow.pickup = AbstractArrowEntity.PickupStatus.DISALLOWED;
        arrow.setBaseDamage( attackDamage / velocity );
        arrow.moveTo( center.x + offset.x * spawnOffset, center.y, center.z + offset.z * spawnOffset, 0.0F, 0.0F );
        arrow.shoot( vecToTarget.x, vecToTarget.y + distanceH * 0.2F, vecToTarget.z, velocity, variance );

        level.addFreshEntity( arrow );
    }

    private void resetTimer( ) {
        if( !level.isClientSide ) {
            if( maxAttackDelay <= minAttackDelay ) {
                // Spawn delay is a constant
                attackDelay = minAttackDelay;
            }
            else {
                attackDelay = minAttackDelay + level.random.nextInt( maxAttackDelay - minAttackDelay );
            }
        }
    }

    @Override
    public CompoundNBT save( CompoundNBT tag ) {
        super.save( tag );

        // Attributes
        tag.putDouble( TAG_ACTIVATION_RANGE, activationRange );
        tag.putBoolean( TAG_CHECK_SIGHT, checkSight );

        tag.putFloat( TAG_DAMAGE, attackDamage );

        tag.putInt( TAG_DELAY_MIN, minAttackDelay );
        tag.putInt( TAG_DELAY_MAX, maxAttackDelay );

        typeData = getOrCreateTypeData();
        maybeSavePotionEffect( typeData );
        tag.put( TAG_TYPE_DATA, typeData );

        // Logic
        tag.putInt( TAG_DELAY, attackDelay );

        return tag;
    }

    @Override
    public void load( BlockState state, CompoundNBT tag ) {
        super.load( state, tag );

        // Attributes
        if( tag.contains( TAG_ACTIVATION_RANGE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            activationRange = tag.getFloat( TAG_ACTIVATION_RANGE );
        }
        if( tag.contains( TAG_CHECK_SIGHT, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            checkSight = tag.getBoolean( TAG_CHECK_SIGHT );
        }

        if( tag.contains( TAG_DAMAGE, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            attackDamage = tag.getFloat( TAG_DAMAGE );
        }

        if( tag.contains( TAG_DELAY_MIN, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            minAttackDelay = tag.getInt( TAG_DELAY_MIN );
        }
        if( tag.contains( TAG_DELAY_MAX, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            maxAttackDelay = tag.getInt( TAG_DELAY_MAX );
        }

        if ( tag.contains( TAG_TYPE_DATA, tag.getId() )) {
            typeData = tag.getCompound( TAG_TYPE_DATA );
            setPotionEffectFromTag( typeData );
        }
        else {
            typeData = getOrCreateTypeData();
        }

        // Logic
        if( tag.contains( TAG_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            attackDelay = tag.getInt( TAG_DELAY );
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket( this.worldPosition, 0, this.getUpdateTag() );
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save( new CompoundNBT() );
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt ) {
        if( this.level.isClientSide ) {
            super.handleUpdateTag( this.getBlockState(), pkt.getTag() );

            CompoundNBT tag = pkt.getTag();

            if ( tag.contains( TAG_TYPE_DATA, tag.getId() )) {
                CompoundNBT typeData = tag.getCompound(TAG_TYPE_DATA);

                if ( towerType == TowerType.POTION ) {
                    setPotionEffectFromTag( typeData );
                }
            }
        }
    }

    private void setPotionEffectFromTag( CompoundNBT typeData ) {
        EffectInstance effectInstance;

        if ( typeData.contains( TAG_POTION_TYPE, typeData.getId() )) {
            CompoundNBT potionTag = typeData.getCompound( TAG_POTION_TYPE );

            if (potionTag.contains("Effect", TrapHelper.NBT_TYPE_STRING)
                    && potionTag.contains("Duration", TrapHelper.NBT_TYPE_PRIMITIVE)
                    && potionTag.contains("Amplifier", TrapHelper.NBT_TYPE_PRIMITIVE)) {

                Effect effect = Effects.HARM;
                int duration;
                int amplifier;

                ResourceLocation effectId = ResourceLocation.tryParse(potionTag.getString("Effect"));

                if (effectId != null) {
                    if (ForgeRegistries.POTIONS.containsKey(effectId)) {
                        effect = ForgeRegistries.POTIONS.getValue(effectId);
                    }
                }
                duration = potionTag.getInt("Duration");
                amplifier = potionTag.getInt("Amplifier");

                effectInstance = new EffectInstance(effect, duration, amplifier);
            }
            else {
                effectInstance = new EffectInstance(Effects.HARM, 1, 0);
            }
            potionEffect = effectInstance;
        }
    }

    private void maybeSavePotionEffect( CompoundNBT towerData ) {
        if (potionEffect != null) {
            CompoundNBT potionTag = new CompoundNBT();

            potionTag.putString("Effect", potionEffect.getEffect().getRegistryName().toString());
            potionTag.putInt("Duration", potionEffect.getDuration());
            potionTag.putInt("Amplifier", potionEffect.getAmplifier());

            towerData.put(TAG_POTION_TYPE, potionTag);
        }
    }

    @Override
    public boolean onlyOpCanSetNbt( ) { return true; }
}
