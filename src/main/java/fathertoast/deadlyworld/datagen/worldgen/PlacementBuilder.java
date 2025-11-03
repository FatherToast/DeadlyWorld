package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.deadlyworld.common.config.dimension.FeatureConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigCountProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigHeightProvider;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to make creating placements for world gen more readable/understandable.
 * <p>
 * Order of calls matters, placement modifiers are applied sequentially.
 */
@SuppressWarnings( "unused" )
public class PlacementBuilder {
    private final List<PlacementModifier> modifiers = new ArrayList<>();
    
    /** @return The output of this builder. */
    public List<PlacementModifier> build() { return modifiers; }
    
    /** Adds a specified placement modifier. */
    public PlacementBuilder then( PlacementModifier modifier ) {
        modifiers.add( modifier );
        return this;
    }
    
    /** Multiplies the number of placements based on the feature config's setting. */
    public PlacementBuilder multiply( FeatureConfig.FeatureTypeCategory config ) {
        //noinspection ConstantConditions
        return multiply( ConfigCountProvider.of( config.countPerChunk ) ); // throws NPE if you use a subfeature
    }
    
    /** Multiplies the number of placements based on the config field's setting. */
    public PlacementBuilder multiply( DoubleField countPerChunk ) {
        return multiply( ConfigCountProvider.of( countPerChunk ) );
    }
    
    /** Multiplies the number of placements. */
    public PlacementBuilder multiply( int count ) { return then( CountPlacement.of( count ) ); }
    
    /** Multiplies the number of placements. */
    public PlacementBuilder multiply( IntProvider count ) { return then( CountPlacement.of( count ) ); }
    
    /** Randomizes the x and z coord of each placement. */
    public PlacementBuilder spreadInChunk() { return then( InSquarePlacement.spread() ); }
    
    /** Randomizes the y coord of each placement. */
    public PlacementBuilder spreadInChunkHeight() { return spreadInHeightsUniform( VerticalAnchor.BOTTOM, VerticalAnchor.TOP ); }
    
    /** Randomizes the y coord of each placement based on the feature config's setting. */
    public PlacementBuilder spreadInHeights( FeatureConfig.FeatureTypeCategory config ) {
        //noinspection ConstantConditions
        return spreadInHeights( ConfigHeightProvider.of( config.heights ) ); // throws NPE if you use a subfeature
    }
    
    /** Randomizes the y coord of each placement within normal ocean ranges. */
    public PlacementBuilder spreadInOceanHeights() {
        return spreadInHeightsUniform( VerticalAnchor.BOTTOM, VerticalAnchor.absolute( References.DEPTH_SEA_LEVEL ) );
    }
    
    /** Randomizes the y coord of each placement with a uniform distribution. */
    public PlacementBuilder spreadInHeightsUniform( VerticalAnchor bottom, VerticalAnchor top ) { return then( HeightRangePlacement.uniform( bottom, top ) ); }
    
    /** Randomizes the y coord of each placement with a triangle distribution. */
    public PlacementBuilder spreadInHeightsTriangle( VerticalAnchor bottom, VerticalAnchor top ) { return then( HeightRangePlacement.triangle( bottom, top ) ); }
    
    /** Randomizes the y coord of each placement. */
    public PlacementBuilder spreadInHeights( HeightProvider range ) { return then( HeightRangePlacement.of( range ) ); }
    
    /** Moves each placement in a specific direction until the supplied predicate is satisfied, up to max distance. */
    public PlacementBuilder move( Direction direction, BlockPredicate until, int maxDistance ) {
        return then( EnvironmentScanPlacement.scanningFor( direction, until, maxDistance ) );
    }
    
    /**
     * Moves each placement in a specific direction until the supplied predicate is satisfied, up to max distance.
     * Placements that become invalid are canceled.
     */
    public PlacementBuilder move( Direction direction, BlockPredicate until, BlockPredicate valid, int maxDistance ) {
        return then( EnvironmentScanPlacement.scanningFor( direction, until, valid, maxDistance ) );
    }
    
    /** Moves each placement's y coord by a specified amount. */
    public PlacementBuilder moveVertical( int distance ) {
        return then( RandomOffsetPlacement.vertical( ConstantInt.of( distance ) ) );
    }
    
    /** Cancels each placement that is not at least a certain distance below the world surface heightmap. */
    public PlacementBuilder requireBelowSurface( int distance ) {
        return then( SurfaceRelativeThresholdFilter.of( Heightmap.Types.WORLD_SURFACE_WG, Integer.MIN_VALUE, -distance ) );
    }
    
    /** Cancels each placement that is not at least a certain distance below the ocean floor heightmap. */
    public PlacementBuilder requireBelowOceanFloor( int distance ) {
        return then( SurfaceRelativeThresholdFilter.of( Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -distance ) );
    }
    
    /** Cancels each placement that is not at least a certain distance above the world surface heightmap. */
    public PlacementBuilder requireAboveSurface( int distance ) {
        return then( SurfaceRelativeThresholdFilter.of( Heightmap.Types.WORLD_SURFACE_WG, distance, Integer.MAX_VALUE ) );
    }
    
    /** Cancels each placement that is not at least a certain distance above the ocean floor heightmap. */
    public PlacementBuilder requireAboveOceanFloor( int distance ) {
        return then( SurfaceRelativeThresholdFilter.of( Heightmap.Types.OCEAN_FLOOR_WG, distance, Integer.MAX_VALUE ) );
    }
    
    /** Cancels each placement that is not in a biome that supports it. */
    public PlacementBuilder requireBiome() { return then( BiomeFilter.biome() ); }
}