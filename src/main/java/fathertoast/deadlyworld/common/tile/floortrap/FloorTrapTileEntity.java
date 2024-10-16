package fathertoast.deadlyworld.common.tile.floortrap;

import fathertoast.deadlyworld.common.block.FloorTrapBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.FloorTrapConfig;
import fathertoast.deadlyworld.common.core.config.util.WeightedPotionList;
import fathertoast.deadlyworld.common.core.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.NBTHelper;
import fathertoast.deadlyworld.common.util.OnClient;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DropperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class FloorTrapTileEntity extends TileEntity implements ITickableTileEntity {

    // Attribute tags
    private static final String TAG_POTION_TYPE = "PotionType";
    private static final String TAG_CAMO_STATE = "CamoState";
    private static final String TAG_RESET_TIME = "ResetTime";
    private static final String TAG_MAX_TRIGGER_DELAY = "MaxTriggerDelay";
    private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
    private static final String TAG_CHECK_SIGHT = "CheckSight";
    private static final String TAG_TYPE_DATA = "TypeData";

    // Logic tags
    private static final String TAG_DELAY = "Delay";

    // Attributes
    @Nullable
    private BlockState camoState;
    private double activationRange;
    private boolean checkSight;
    private FloorTrapType trapType;

    // Used for potion traps
    private CompoundNBT typeData;
    @Nullable
    private ItemStack potionStack;

    // Logic
    private boolean pickedCamo = false;
    private int maxTriggerDelay;
    /** Count until the trap triggers after being tripped. -1 if the trap has not been tripped. */
    private int triggerDelay = -1;
    private Supplier<Integer> resetTime = () -> 20;

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
        if( level == null ) {
            DeadlyWorld.LOG.error( "Failed to load floor trap block entity at \"{}\"", this.getBlockPos() );
            return;
        }
        if( getBlockState().getBlock() instanceof FloorTrapBlock ) {
            DimensionConfigGroup dimConfigs = Config.getDimensionConfigs( level );
            FloorTrapType trapType = ((FloorTrapBlock) getBlockState().getBlock()).getTrapType();
            this.trapType = trapType;

            initializeFloorTrap( trapType, dimConfigs, level.random );
        }
        else {
            DeadlyWorld.LOG.error( "Attempted to initialize Floor Trap tile entity with the wrong type of block! TileEntity:{}, Block:{}",
                    this.getType().getRegistryName(),
                    getBlockState().getBlock().getRegistryName() );
        }
    }

    public void initializeFloorTrap( FloorTrapType trapType, DimensionConfigGroup dimConfig, Random random ) {
        FloorTrapConfig.FloorTrapTypeCategory trapConfig = trapType.getFeatureConfig( dimConfig );

        // Set attributes from the config
        activationRange = trapConfig.activationRange.get();
        checkSight = trapConfig.checkSight.get();
        maxTriggerDelay = 10;

        // Give each trap a partially random reset time. What could it be??? Slow lad or mega TNT barrage insane sicko mode???
        if (resetTime == null) {
            resetTime = () -> trapConfig.minResetTime.get() + random.nextInt(trapConfig.maxResetTime.get() - trapConfig.minResetTime.get());
        }

        // Set the potion effect if we are a potion trap
        if (trapConfig instanceof FloorTrapConfig.PotionTrapTypeCategory) {
            if (potionStack == null || potionStack.isEmpty()) {
                FloorTrapConfig.PotionTrapTypeCategory potionTrapConfig = (FloorTrapConfig.PotionTrapTypeCategory) trapConfig;

                EffectInstance effectInstance = potionTrapConfig.potionList.get().next(random);

                if (effectInstance == null)
                    effectInstance = new EffectInstance(Effects.HARM, 1, 0);

                potionStack = PotionUtils.setCustomEffects(new ItemStack(Items.SPLASH_POTION), Collections.singletonList(effectInstance));
                TrapHelper.setStackPotionColor(potionStack);
            }
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

    @OnClient
    @Nullable
    public BlockState getCamoState() {
        return camoState;
    }

    public PlayerEntity getTarget( ) {
        return TrapHelper.getNearestTrapValidPlayerInRange( level, getBlockPos().above( ), activationRange, checkSight, true );
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
        if (!pickedCamo) {
            if (camoState == null) {
                pickCamoState();
            }
            pickedCamo = true;
        }

        if( !level.isClientSide ) {
            // Run server-side logic
            if( triggerDelay == -1 ) {
                // Check if trap should be tripped
                boolean validPlayerInRange = trapType.spawnsMonster()
                        ? TrapHelper.isValidPlayerInRange( level, getBlockPos().above( ), activationRange, checkSight, true )
                        : TrapHelper.isValidTrapPlayerInRange( level, getBlockPos().above( ), activationRange, checkSight, true );

                if( validPlayerInRange ) {
                    tripTrap();
                    level.playSound( null, getBlockPos().getX( ) + 0.5, getBlockPos().getY( ) + 1, getBlockPos().getZ( ) + 0.5,
                            SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F );
                }
            }
            else {
                // Trap has been tripped or reset with a delay
                triggerDelay++;

                // Trigger trap
                if( triggerDelay >= maxTriggerDelay ) {
                    triggerTrap( );
                }
            }
        }
    }

    private void pickCamoState() {
        World world = getLevel();
        List<Direction> directions = Arrays.asList(Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH);
        Collections.shuffle(directions);

        for (Direction dir : directions) {
            BlockPos neighbor = getBlockPos().relative(dir);
            BlockState neighborState = world.getBlockState(neighbor);

            if (neighborState.isSolidRender(world, neighbor) && neighborState.isCollisionShapeFullBlock(world, neighbor)) {
                camoState = neighborState;
                return;
            }
        }
        camoState = Blocks.DROPPER.defaultBlockState().setValue(DropperBlock.FACING, Direction.UP);;
    }

    private void triggerTrap( ) {
        final DimensionConfigGroup dimConfig = Config.getDimensionConfigs(level);
        final FloorTrapType trapType = getTrapType( );

        trapType.triggerTrap( dimConfig, this );
        disableTrap(resetTime.get());
    }

    @Override
    public CompoundNBT save( CompoundNBT tag ) {
        tag = super.save( tag );

        // Attributes
        if (camoState != null) {
            tag.put( TAG_CAMO_STATE, NBTHelper.writeBlockState(camoState) );
        }
        tag.putInt( TAG_RESET_TIME, resetTime.get() );
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
        setCamoFromTag( tag );

        if ( tag.contains( TAG_RESET_TIME, TrapHelper.NBT_TYPE_PRIMITIVE )) {
            resetTime = () -> tag.getInt( TAG_RESET_TIME );
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
            setPotionFromTag( typeData );
        }
        else {
            typeData = getOrCreateTypeData();
        }

        // Logic
        if( tag.contains( TAG_DELAY, TrapHelper.NBT_TYPE_PRIMITIVE ) ) {
            triggerDelay = tag.getInt( TAG_DELAY );
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
            setCamoFromTag( tag );

            if ( tag.contains( TAG_TYPE_DATA, tag.getId() )) {
                CompoundNBT typeData = tag.getCompound(TAG_TYPE_DATA);

                if ( trapType == FloorTrapType.POTION ) {
                    setPotionFromTag( typeData );
                }
            }
        }
    }

    private void setCamoFromTag( CompoundNBT compoundNBT ) {
        if ( compoundNBT.contains( TAG_CAMO_STATE, compoundNBT.getId() )) {
            BlockState readState = NBTHelper.readBlockState( compoundNBT.getCompound( TAG_CAMO_STATE ));

            camoState = readState == null
                    ? Blocks.DROPPER.defaultBlockState().setValue(DropperBlock.FACING, Direction.UP)
                    : readState;
        }
    }

    private void setPotionFromTag( CompoundNBT typeData ) {
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
            potionStack = PotionUtils.setCustomEffects(new ItemStack(Items.SPLASH_POTION), Collections.singletonList(effectInstance));
            TrapHelper.setStackPotionColor(potionStack);
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

                trapData.put(TAG_POTION_TYPE, potionTag);
            }
        }
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
}
