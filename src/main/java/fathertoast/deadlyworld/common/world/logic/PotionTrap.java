package fathertoast.deadlyworld.common.world.logic;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.trap.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.TrapConfig;
import fathertoast.deadlyworld.common.world.levelgen.DeadlyFeature;
import fathertoast.deadlyworld.common.world.levelgen.settings.FloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.settings.PotionFloorTrapSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;

import javax.annotation.Nullable;

public class PotionTrap extends BaseTrap {

    public static final String POTION_KEY = "Potion";
    public static final String DYNAMIC_POTION_KEY = "DynamicPotion";

    private boolean dynamic;
    @Nullable
    private MobEffectInstance potion;


    public PotionTrap( DeadlyTrapBlockEntity trap ) {
        super( TrapType.POTION, trap, trap );
    }


    // Overridden in block
    @Override
    public void triggerTrap( ServerLevel level, BlockPos pos ) { }

    public void initializeTrap( WorldGenLevel level, BlockPos pos, RandomSource random, PotionFloorTrapSettings trapSettings ) {
        final TrapConfig.PotionTrapTypeCategory trapConfig = (TrapConfig.PotionTrapTypeCategory) trapType.getFeatureConfig( Config.getDimensionConfigs( level.getLevel() ) );
        DeadlyFeature.debugMarkerIfEnabled( level, pos, trapConfig );

        initializeTrap( getLevel(), pos, random,
                trapSettings.requiredPlayerRange().sample( random ),
                trapSettings.checkSightChance().sample( random ),
                trapSettings.resetTime().getMinValue(),
                trapSettings.resetTime().getMaxValue(),
                trapSettings.triggersRemaining().sample( random ),
                roll( random, trapSettings.dynamicChance().sample( random ) ),
                trapConfig.potionList.get().next( random ),
                roll( random, trapSettings.decoyChance().sample( random ) )
        );
    }

    public void initializeTrap( @Nullable Level level, BlockPos pos, RandomSource random ) {
        final TrapConfig.PotionTrapTypeCategory trapConfig = (TrapConfig.PotionTrapTypeCategory) TrapType.POTION.getFeatureConfig( Config.getDimensionConfigs( level ) );
        initializeTrap( level, pos, random,
                trapConfig.activationRange.get(), (float) trapConfig.checkSightChance.get(),
                trapConfig.resetTime.getMin(), trapConfig.resetTime.getMax(), trapConfig.triggersRemaining.get(),
                trapConfig.dynamicChance.rollChance( random ), trapConfig.potionList.get().next( random ),
                trapConfig.decoyChance.rollChance( random )
        );
    }

    public void initializeTrap( @Nullable Level level, BlockPos pos, RandomSource random, double activationRange,
                                float checkSightChance, int minResetTime, int maxResetTime, int triggersRemaining,
                                boolean dynamic, @Nullable MobEffectInstance potion, boolean spawnDecoy ) {
        super.initializeTrap( level, pos, random, activationRange, checkSightChance, minResetTime, maxResetTime, triggersRemaining, spawnDecoy );
        this.dynamic = dynamic;
        this.potion = potion;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    @Nullable
    public MobEffectInstance getPotionCopy() {
        return potion == null ? null : new MobEffectInstance( potion );
    }

    @Override
    public void load( @Nullable Level level, BlockPos pos, CompoundTag loadTag ) {
        super.load( level, pos, loadTag );

        if ( NBTHelper.containsNumber( loadTag, DYNAMIC_POTION_KEY ) )
            dynamic = loadTag.getBoolean( DYNAMIC_POTION_KEY );
        if ( NBTHelper.containsCompound( loadTag, POTION_KEY ))
            potion = MobEffectInstance.load( loadTag.getCompound( POTION_KEY ) );
    }

    @Override
    public CompoundTag save( CompoundTag saveTag ) {
        saveTag = super.save( saveTag );

        saveTag.putBoolean( DYNAMIC_POTION_KEY, dynamic );
        saveTag.put( POTION_KEY, potion == null ? new CompoundTag() : potion.save( new CompoundTag() ) );

        return saveTag;
    }
}
