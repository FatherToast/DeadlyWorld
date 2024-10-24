package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Predicate;

/**
 * Superclass for all Deadly World features.
 * <p>
 * This lets us slap in whatever convenience methods we want so that they are available to all features.
 */
public abstract class DeadlyFeature<FC extends FeatureConfiguration> extends Feature<FC> {
    protected static final Predicate<BlockState> IS_AIR = BlockBehaviour.BlockStateBase::isAir;
    
    public DeadlyFeature( Codec<FC> codec ) { super( codec ); }
    
    /** Convenience method for using safeSetBlock with a block state provider. */
    protected void safeSetBlock( WorldGenLevel level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random, Predicate<BlockState> predicate ) {
        safeSetBlock( level, pos, stateProvider.getState( random, pos ), predicate );
    }
    
    @Override // We just override this so the parameter names aren't mush
    protected void safeSetBlock( WorldGenLevel level, BlockPos pos, BlockState state, Predicate<BlockState> predicate ) {
        super.safeSetBlock( level, pos, state, predicate );
    }
    
    /** Convenience method for using setBlock with a block state provider. */
    protected void setBlock( LevelWriter level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random ) {
        setBlock( level, pos, stateProvider.getState( random, pos ) );
    }
    
    @Override // We override to use Block.UPDATE_CLIENTS rather than Block.UPDATE_ALL (to match safeSetBlock)
    protected void setBlock( LevelWriter level, BlockPos pos, BlockState state ) {
        level.setBlock( pos, state, Block.UPDATE_CLIENTS );
    }
}