package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.block.IDeadlyBlock;
import fathertoast.deadlyworld.common.block.misc.DeadlyInfestedBlock;
import fathertoast.deadlyworld.common.block.unstable.UnstableBlock;
import fathertoast.deadlyworld.common.config.dimension.FeatureConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Superclass for all Deadly World features.
 * <p>
 * This lets us slap in whatever convenience methods we want so that they are available to all features.
 */
public abstract class DeadlyFeature<FC extends FeatureConfiguration> extends Feature<FC> {
    protected static final Predicate<BlockState> IS_AIR = BlockBehaviour.BlockStateBase::isAir;
    
    /** Generates a debug marker above the position if it is enabled in the config. */
    public static void debugMarkerIfEnabled( WorldGenLevel level, BlockPos pos, FeatureConfig.FeatureTypeCategory config ) {
        if( config.debugMarker != null && config.debugMarker.get() ) debugMarker( level, pos );
    }
    
    /** Generates a debug marker above the position. */
    public static void debugMarker( WorldGenLevel level, BlockPos pos ) {
        DeadlyWorld.LOG.info( "Generating marker at {}", pos );
        
        final BlockState state = Blocks.GLASS.defaultBlockState();
        final BlockPos.MutableBlockPos cursor = pos.mutable().move( 0, 5, 0 );
        final int heightLimit = level.getMaxBuildHeight();
        while( cursor.getY() < heightLimit ) {
            level.setBlock( cursor, state, Block.UPDATE_CLIENTS );
            cursor.move( 0, 1, 0 );
        }
    }
    
    /**
     * Checks for other nearby blocks that extend {@link fathertoast.deadlyworld.common.block.IDeadlyBlock}
     * to help prevent bizarre clumping of traps.<br><br>
     *
     * @param diameter The "diameter" for the bounds (we are searching in a cube).
     *                 Larger values will make generation slow.
     * @return True if nearby traps/spawners were found inside the given bounds.
     */
    public static boolean hasNearbyTraps( WorldGenLevel level, BlockPos origin, int diameter ) {
        // TODO - This is a temporary solution, and not a very good one
        for( BlockPos blockPos : BlockPos.betweenClosed(
                origin.offset( -diameter, -diameter, -diameter ),
                origin.offset( diameter, diameter, diameter ) ) ) {
            
            if( level.getBlockState( blockPos ).getBlock() instanceof IDeadlyBlock )
                return true;
        }
        return false;
    }
    
    /**
     * Places a feature as a 'subfeature'. This provides the parent feature in the feature place context
     * of the subfeature's place method. Deadly World features skip all 'can place' logic when generated
     * as subfeatures, and may optionally pull data from the parent feature.
     *
     * @param subfeatureKey The registry key for the feature to place as a subfeature.
     * @param parent        The parent feature; if null, the dummy feature singleton is used.
     * @return True if the subfeature exists in the registry and was successfully placed.
     */
    public static boolean placeSubfeature( ServerLevel level, BlockPos pos,
                                           @Nullable ResourceLocation subfeatureKey, @Nullable ConfiguredFeature<?, ?> parent ) {
        return placeSubfeature( level, level.getChunkSource().getGenerator(), pos, subfeatureKey, parent );
    }
    
    /**
     * Places a feature as a 'subfeature'. This provides the parent feature in the feature place context
     * of the subfeature's place method. Deadly World features skip all 'can place' logic when generated
     * as subfeatures, and may optionally pull data from the parent feature.
     *
     * @param subfeatureKey The registry key for the feature to place as a subfeature.
     * @param parent        The parent feature; if null, the dummy feature singleton is used.
     * @return True if the subfeature exists in the registry and was successfully placed.
     */
    public static boolean placeSubfeature( WorldGenLevel level, ChunkGenerator chunkGenerator, BlockPos pos,
                                           @Nullable ResourceLocation subfeatureKey, @Nullable ConfiguredFeature<?, ?> parent ) {
        ConfiguredFeature<?, ?> subfeature = getFeature( level, subfeatureKey );
        return subfeature != null && placeSubfeature( level, chunkGenerator, pos, subfeature, parent );
    }
    
    /**
     * Places a feature as a 'subfeature'. This provides the parent feature in the feature place context
     * of the subfeature's place method. Deadly World features skip all 'can place' logic when generated
     * as subfeatures, and may optionally pull data from the parent feature.
     *
     * @param subfeature The feature to place as a subfeature.
     * @param parent     The parent feature; if null, the dummy feature singleton is used.
     * @return True if the subfeature was successfully placed.
     */
    public static boolean placeSubfeature( ServerLevel level, BlockPos pos,
                                           ConfiguredFeature<?, ?> subfeature, @Nullable ConfiguredFeature<?, ?> parent ) {
        return placeSubfeature( level, level.getChunkSource().getGenerator(), pos, subfeature, parent );
    }
    
    /**
     * Places a feature as a 'subfeature'. This provides the parent feature in the feature place context
     * of the subfeature's place method. Deadly World features skip all 'can place' logic when generated
     * as subfeatures, and may optionally pull data from the parent feature.
     *
     * @param subfeature The feature to place as a subfeature.
     * @param parent     The parent feature; if null, the dummy feature singleton is used.
     * @return True if the subfeature was successfully placed.
     */
    public static boolean placeSubfeature( WorldGenLevel level, ChunkGenerator chunkGenerator, BlockPos pos,
                                           ConfiguredFeature<?, ?> subfeature, @Nullable ConfiguredFeature<?, ?> parent ) {
        //noinspection rawtypes,unchecked
        return subfeature.feature().place( new FeaturePlaceContext(
                Optional.of( parent == null ? DummyFeature.CONFIGURED_INSTANCE : parent ),
                level, chunkGenerator, level.getRandom(), pos, subfeature.config() ) );
    }
    
    /** @return The configured feature from the registry, if it exists. */
    @Nullable
    public static ConfiguredFeature<?, ?> getFeature( WorldGenLevel level, @Nullable ResourceLocation featureKey ) {
        Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().registryOrThrow( Registries.CONFIGURED_FEATURE );
        return registry.get( featureKey );
    }
    
    
    public DeadlyFeature( Codec<FC> codec ) { super( codec ); }
    
    /** Convenience method for placing infested blocks with a block state provider. */
    protected void safeSetInfestedBlock( WorldGenLevel level, BlockPos pos, BlockStateProvider stateProvider, FloatProvider chanceProvider, RandomSource random, @Nullable Predicate<BlockState> predicate ) {
        if( random.nextFloat() < chanceProvider.sample( random ) )
            safeSetInfestedBlock( level, pos, stateProvider, random, predicate );
        else
            safeSetBlock( level, pos, stateProvider, random, predicate );
    }
    
    /** Convenience method for placing infested blocks with a block state provider. */
    protected void setInfestedBlock( LevelWriter level, BlockPos pos, BlockStateProvider stateProvider, FloatProvider chanceProvider, RandomSource random ) {
        if( random.nextFloat() < chanceProvider.sample( random ) )
            setInfestedBlock( level, pos, stateProvider, random );
        else
            setBlock( level, pos, stateProvider, random );
    }
    
    /** Convenience method for placing infested blocks with a block state provider. */
    protected void safeSetInfestedBlock( WorldGenLevel level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random, @Nullable Predicate<BlockState> predicate ) {
        safeSetBlock( level, pos, DeadlyInfestedBlock.tryInfestUnknown( stateProvider.getState( random, pos ) ), predicate );
    }
    
    /** Convenience method for placing infested blocks with a block state provider. */
    protected void setInfestedBlock( LevelWriter level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random ) {
        setBlock( level, pos, DeadlyInfestedBlock.tryInfestUnknown( stateProvider.getState( random, pos ) ) );
    }
    
    /** Convenience method for placing unstable blocks with a block state provider. */
    protected void safeSetUnstableBlock( WorldGenLevel level, BlockPos pos, BlockStateProvider stateProvider, FloatProvider chanceProvider, RandomSource random, @Nullable Predicate<BlockState> predicate ) {
        if( random.nextFloat() < chanceProvider.sample( random ) )
            safeSetUnstableBlock( level, pos, stateProvider, random, predicate );
        else
            safeSetBlock( level, pos, stateProvider, random, predicate );
    }
    
    /** Convenience method for placing unstable blocks with a block state provider. */
    protected void setUnstableBlock( LevelWriter level, BlockPos pos, BlockStateProvider stateProvider, FloatProvider chanceProvider, RandomSource random ) {
        if( random.nextFloat() < chanceProvider.sample( random ) )
            setUnstableBlock( level, pos, stateProvider, random );
        else
            setBlock( level, pos, stateProvider, random );
    }
    
    /** Convenience method for placing unstable blocks with a block state provider. */
    protected void safeSetUnstableBlock( WorldGenLevel level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random, @Nullable Predicate<BlockState> predicate ) {
        safeSetBlock( level, pos, UnstableBlock.tryDestabilizing( stateProvider.getState( random, pos ) ), predicate );
    }
    
    /** Convenience method for placing unstable blocks with a block state provider. */
    protected void safeSetUnstableBlock( WorldGenLevel level, BlockPos pos, BlockState state, @Nullable Predicate<BlockState> predicate ) {
        safeSetBlock( level, pos, UnstableBlock.tryDestabilizing( state ), predicate );
    }
    
    /** Convenience method for placing unstable blocks with a block state provider. */
    protected void setUnstableBlock( LevelWriter level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random ) {
        setBlock( level, pos, UnstableBlock.tryDestabilizing( stateProvider.getState( random, pos ) ) );
    }
    
    /** Convenience method for using safeSetBlock with a block state provider. */
    protected void safeSetBlock( WorldGenLevel level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random, @Nullable Predicate<BlockState> predicate ) {
        safeSetBlock( level, pos, stateProvider.getState( random, pos ), predicate );
    }
    
    @Override // We override this so the parameter names aren't mush, and also to allow null predicate
    protected void safeSetBlock( WorldGenLevel level, BlockPos pos, BlockState state, @Nullable Predicate<BlockState> predicate ) {
        if( predicate == null ) setBlock( level, pos, state );
        else super.safeSetBlock( level, pos, state, predicate );
    }
    
    /** Convenience method for using setBlock with a block state provider. */
    protected void setBlock( LevelWriter level, BlockPos pos, BlockStateProvider stateProvider, RandomSource random ) {
        setBlock( level, pos, stateProvider.getState( random, pos ) );
    }
    
    @Override // We override to use Block.UPDATE_CLIENTS rather than Block.UPDATE_ALL (to match safeSetBlock)
    protected void setBlock( LevelWriter level, BlockPos pos, BlockState state ) {
        level.setBlock( pos, state, Block.UPDATE_CLIENTS );
    }
    
    /**
     * Based on {@link StructurePiece#reorient(BlockGetter, BlockPos, BlockState)}, but heavily modified.
     *
     * @return The chest block with a somewhat randomized direction; tries to avoid making the chest face a wall.
     */
    protected BlockState randomizeChestDirection( BlockGetter level, BlockPos pos, BlockState chest,
                                                  RandomSource random, boolean favorSingleWall ) {
        if( !chest.hasProperty( HorizontalDirectionalBlock.FACING ) ) return chest;
        
        // Determine the directions that are not facing a wall
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final List<Direction> allowedDirs = new ArrayList<>();
        Direction wall = null;
        for( Direction direction : Direction.Plane.HORIZONTAL ) {
            cursor.setWithOffset( pos, direction );
            if( level.getBlockState( cursor ).isSolidRender( level, cursor ) ) {
                wall = direction;
            }
            else {
                allowedDirs.add( direction );
            }
        }
        
        // If only one direction is a wall, face directly away from it
        if( favorSingleWall && allowedDirs.size() == 3 && wall != null ) {
            return chest.setValue( HorizontalDirectionalBlock.FACING, wall.getOpposite() );
        }
        // We are surrounded by walls, just pick a random direction
        else if( allowedDirs.isEmpty() ) {
            return chest.setValue( HorizontalDirectionalBlock.FACING,
                    Direction.Plane.HORIZONTAL.getRandomDirection( random ) );
        }
        // Pick a random direction that is not facing a wall
        else {
            return chest.setValue( HorizontalDirectionalBlock.FACING,
                    allowedDirs.get( random.nextInt( allowedDirs.size() ) ) );
        }
    }
    
    /** @return True if any blocks immediately surrounding the origin in the horizontal plane are solid, but with no block above. */
    protected boolean isOnLip( WorldGenLevel level, BlockPos origin ) {
        return isOnLip( level, origin, new BlockPos.MutableBlockPos() );
    }
    
    /** @return True if any blocks immediately surrounding the origin in the horizontal plane are solid, but with no block above. */
    protected boolean isOnLip( WorldGenLevel level, BlockPos origin, BlockPos.MutableBlockPos cursor ) {
        for( Direction dir : Direction.Plane.HORIZONTAL ) {
            //noinspection deprecation
            if( level.getBlockState( cursor.setWithOffset( origin, dir ) ).isSolid() &&
                    !level.getBlockState( cursor.move( Direction.UP ) ).isSolid() ) {
                return true;
            }
        }
        return false;
    }
}