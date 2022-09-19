package fathertoast.deadlyworld.common.tile.floortrap;

import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.FloorTrapBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.FloorTrapConfig;
import fathertoast.deadlyworld.common.core.registry.DWTileEntities;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nonnull;
import java.util.Random;

public class FloorTrapTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int TO_TRIGGER_DELAY = 10;
    // Attribute tags
    private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
    private static final String TAG_CHECK_SIGHT = "CheckSight";
    private static final String TAG_TYPE_DATA = "TypeData";

    // Logic tags
    private static final String TAG_DELAY = "Delay";

    // Attributes
    private double activationRange;
    private boolean checkSight;
    private FloorTrapType trapType;
    private CompoundNBT typeData;

    // Logic
    /** Count until the trap triggers after being tripped. -1 if the trap has not been tripped. */
    private int triggerDelay = -1;

    public FloorTrapTileEntity() {
        super(DWTileEntities.FLOOR_TRAP.get());
    }

    public void resetTrap( ) { triggerDelay = -1; }

    public void tripTrap( ) { triggerDelay = 0; }

    public void tripTrapRandom( ) { triggerDelay = level.random.nextInt( TO_TRIGGER_DELAY ); }

    public void disableTrap( int duration ) { triggerDelay = -1 - duration; }

    public void disableTrap( ) { triggerDelay = TO_TRIGGER_DELAY; }

    public CompoundNBT getOrCreateTypeData( ) {
        if( typeData == null ) {
            typeData = new CompoundNBT( );
        }
        return typeData;
    }

    @Override
    public void onLoad() {
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
                if( triggerDelay == TO_TRIGGER_DELAY ) {
                    triggerTrap( );
                }
            }
        }
    }

    private void triggerTrap( ) {
        final DimensionConfigGroup dimConfig = Config.getDimensionConfigs(level);
        final FloorTrapType trapType = getTrapType( );

        trapType.triggerTrap( dimConfig, this );
    }

    @Override
    public CompoundNBT save( CompoundNBT tag ) {
        super.save( tag );

        // Attributes
        tag.putDouble( TAG_ACTIVATION_RANGE, activationRange );
        tag.putBoolean( TAG_CHECK_SIGHT, checkSight );
        if( typeData != null ) {
            tag.put( TAG_TYPE_DATA, typeData );
        }

        // Logic
        tag.putInt( TAG_DELAY, triggerDelay );

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
        if( tag.contains( TAG_TYPE_DATA, tag.getId( ) ) ) {
            typeData = tag.getCompound( TAG_TYPE_DATA );
        }
        else {
            typeData = null;
        }

        // Logic
        if( tag.contains( TAG_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            triggerDelay = tag.getInt( TAG_DELAY );
        }
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
}
