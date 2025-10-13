package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.block.IDeadlyBlock;
import fathertoast.deadlyworld.common.config.dimension.FeatureConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import javax.annotation.Nullable;
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
    
    // TODO - This is a temporary solution, and not a very good one
    
    /**
     * Checks for other nearby blocks that extend {@link fathertoast.deadlyworld.common.block.IDeadlyBlock}
     * to help prevent bizarre clumping of traps.<br><br>
     *
     * @param diameter The "diameter" for the bounds (we are searching in a cube).
     *                 Larger values will make generation slow.
     * @return True if nearby traps/spawners were found inside the given bounds.
     */
    public static boolean hasNearbyTraps( WorldGenLevel level, BlockPos origin, int diameter ) {
        for( BlockPos blockPos : BlockPos.betweenClosed(
                origin.offset( -diameter, -diameter, -diameter ),
                origin.offset( diameter, diameter, diameter ) ) ) {
            
            if( level.getBlockState( blockPos ).getBlock() instanceof IDeadlyBlock )
                return true;
        }
        return false;
    }
    
    public DeadlyFeature( Codec<FC> codec ) { super( codec ); }
    
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
    
    /** Convenience method for placing a regular chest with loot. */
    protected void placeChest( WorldGenLevel level, BlockPos pos, RandomSource random, ResourceLocation lootTable, @Nullable Predicate<BlockState> predicate ) {
        placeChest( level, pos, Blocks.CHEST, random, lootTable, predicate );
    }
    
    /** Convenience method for placing a trapped chest with loot. */
    protected void placeTrappedChest( WorldGenLevel level, BlockPos pos, RandomSource random, ResourceLocation lootTable, @Nullable Predicate<BlockState> predicate ) {
        placeChest( level, pos, Blocks.TRAPPED_CHEST, random, lootTable, predicate );
    }
    
    /** Convenience method for placing a specific type of chest with loot. */
    protected void placeChest( WorldGenLevel level, BlockPos pos, Block chest, RandomSource random, ResourceLocation lootTable, @Nullable Predicate<BlockState> predicate ) {
        safeSetBlock( level, pos, StructurePiece.reorient( level, pos, chest.defaultBlockState() ), predicate );
        RandomizableContainerBlockEntity.setLootTable( level, random, pos, lootTable );
    }
    
    /** @return True if any blocks immediately surrounding the origin in the horizontal plane are solid, but with no block above. */
    protected boolean isOnLip( WorldGenLevel level, BlockPos origin ) {
        return isOnLip( level, origin, new BlockPos.MutableBlockPos() );
    }
    
    /** @return True if any blocks immediately surrounding the origin in the horizontal plane are solid, but with no block above. */
    protected boolean isOnLip( WorldGenLevel level, BlockPos origin, BlockPos.MutableBlockPos cursor ) {
        for( Direction dir : Direction.Plane.HORIZONTAL ) {
            if( level.getBlockState( cursor.setWithOffset( origin, dir ) ).isSolid() &&
                    !level.getBlockState( cursor.move( Direction.UP ) ).isSolid() ) {
                return true;
            }
        }
        return false;
    }
}