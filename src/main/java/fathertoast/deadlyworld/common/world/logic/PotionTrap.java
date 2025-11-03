package fathertoast.deadlyworld.common.world.logic;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.entity.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.FloorTrapConfig;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import fathertoast.deadlyworld.common.world.levelgen.PotionFloorTrapSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;

import javax.annotation.Nullable;

public class PotionTrap extends BaseTrap {
    
    public static final String POTION_KEY = "Potion";
    public static final String DYNAMIC_POTION_KEY = "DynamicPotion";
    
    private boolean dynamic;
    @Nullable
    private MobEffectInstance potion;
    
    
    public PotionTrap( DeadlyTrapBlockEntity trap ) {
        super( FloorTrapType.POTION, trap, trap );
    }
    
    
    // Overridden in block
    @Override
    public void triggerTrap( ServerLevel level, BlockPos pos ) { }
    
    public void initializeTrap( WorldGenLevel level, BlockPos pos, RandomSource random, PotionFloorTrapSettings trapSettings ) {
        final FloorTrapConfig.PotionTrapTypeCategory trapConfig = (FloorTrapConfig.PotionTrapTypeCategory) trapType.getConfig( level.getLevel() );
        DeadlyFeature.debugMarkerIfEnabled( level, pos, trapConfig );
        
        initializeTrap( level, level.getLevel().dimension(), pos, random,
                trapSettings.camoChance().sample( random ) > random.nextFloat(),
                trapSettings.decoyChance().sample( random ) > random.nextFloat(),
                trapSettings.requiredPlayerRange().sample( random ),
                trapSettings.checkSightChance().sample( random ) > random.nextFloat(),
                trapSettings.triggersRemaining().sample( random ),
                trapSettings.resetTime().getMinValue(),
                trapSettings.resetTime().getMaxValue(),
                trapSettings.dynamicChance().sample( random ) > random.nextFloat(),
                trapConfig.potionList.get().next( random )
        );
    }
    
    @Override
    public void initializeTrap( Level level, BlockPos pos, RandomSource random ) {
        final FloorTrapConfig.PotionTrapTypeCategory trapConfig = (FloorTrapConfig.PotionTrapTypeCategory) FloorTrapType.POTION.getConfig( level );
        initializeTrap( level, level.dimension(), pos, random, trapConfig.camoChance.rollChance( random ), trapConfig.decoyChance.rollChance( random ),
                trapConfig.activationRange.get(), trapConfig.checkSightChance.rollChance( random ),
                trapConfig.triggersRemaining.get(), trapConfig.resetTime.getMin(), trapConfig.resetTime.getMax(),
                trapConfig.dynamicChance.rollChance( random ), trapConfig.potionList.get().next( random )
        );
    }
    
    public void initializeTrap( LevelAccessor level, ResourceKey<Level> dimension, BlockPos pos, RandomSource random, boolean useCamo, boolean spawnDecoy,
                                double activationRange, boolean checkSight, int triggersRemaining, int minResetTime, int maxResetTime,
                                boolean dynamic, @Nullable MobEffectInstance potion ) {
        super.initializeTrap( level, dimension, pos, random, useCamo, spawnDecoy, activationRange, checkSight, triggersRemaining, minResetTime, maxResetTime );
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
        
        if( NBTHelper.containsNumber( loadTag, DYNAMIC_POTION_KEY ) )
            dynamic = loadTag.getBoolean( DYNAMIC_POTION_KEY );
        if( NBTHelper.containsCompound( loadTag, POTION_KEY ) )
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