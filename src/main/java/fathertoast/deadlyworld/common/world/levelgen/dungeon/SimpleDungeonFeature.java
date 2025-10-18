package fathertoast.deadlyworld.common.world.levelgen.dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DungeonConfig;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Predicate;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.levelgen.feature.MonsterRoomFeature}.
 * <br><br>
 * Generates mostly the same way, except a list of subfeatures is used to pick what generates in the middle.
 */
public class SimpleDungeonFeature extends DeadlyFeature<SimpleDungeonFeature.Configuration> {
    public record Configuration(
            BlockStateProvider baseProvider,
            BlockStateProvider floorMixProvider,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<SimpleDungeonFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "base_provider" ).forGetter( SimpleDungeonFeature.Configuration::baseProvider ),
                BlockStateProvider.CODEC.fieldOf( "floor_mix_provider" ).forGetter( SimpleDungeonFeature.Configuration::floorMixProvider ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( SimpleDungeonFeature.Configuration::cannotReplace )
        ).apply( instance, SimpleDungeonFeature.Configuration::new ) );
    }
    
    public SimpleDungeonFeature() {
        this( Configuration.CODEC );
    }
    
    public SimpleDungeonFeature( Codec<Configuration> codec ) {
        super( codec );
    }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        final BlockState AIR = Blocks.AIR.defaultBlockState();
        final BlockPos origin = context.origin();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos cursor2 = new BlockPos.MutableBlockPos();
        
        int xOffset = random.nextInt( 2 ) + 2;
        int minX = -xOffset - 1;
        int maxX = xOffset + 1;
        
        int zOffset = random.nextInt( 2 ) + 2;
        int minZ = -zOffset - 1;
        int maxZ = zOffset + 1;
        
        // Check if we can place here
        if( notSubfeature ) {
            // Scan for a moderate area of solid blocks that is close to a cave opening
            int openWallSpace = 0;
            for( int x = minX; x <= maxX; ++x ) {
                for( int y = -1; y <= 4; ++y ) {
                    for( int z = minZ; z <= maxZ; ++z ) {
                        cursor.setWithOffset( origin, x, y, z );
                        
                        // Return if we don't have a solid floor or ceiling
                        if( (y == -1 || y == 4) && !level.getBlockState( cursor ).isSolid() ) {
                            return false;
                        }
                        
                        // Check if each wall position at Y 0 plus the above block are air blocks
                        if( y == 0 && (x == minX || x == maxX || z == minZ || z == maxZ) &&
                                level.isEmptyBlock( cursor ) &&
                                level.isEmptyBlock( cursor.move( Direction.UP ) ) ) {
                            openWallSpace++;
                        }
                    }
                }
            }
            // Are there enough "holes" where the walls should be? If not, abort
            if( openWallSpace < 1 || openWallSpace > 6 ) return false;
        }
        
        // Build the dungeon room
        for( int x = minX; x <= maxX; ++x ) {
            for( int y = 3; y >= -1; --y ) {
                for( int z = minZ; z <= maxZ; ++z ) {
                    cursor.setWithOffset( origin, x, y, z );
                    BlockState state = level.getBlockState( cursor );
                    
                    // Hollow out the inside of the room with air
                    if( y != -1 && x != minX && z != minZ && x != maxX && z != maxZ ) {
                        // We leave existing chests and trap things alone,
                        // so if another dungeon generated nearby they can merge without much problem.
                        if( !state.is( Blocks.CHEST ) && !state.is( DWTags.Blocks.SPAWNERS.blockTag() ) && !state.is( DWTags.Blocks.TOWER_DISPENSERS.blockTag() ) ) {
                            safeSetBlock( level, cursor, AIR, predicate );
                        }
                    }
                    else if( cursor.getY() >= level.getMinBuildHeight() && !level.getBlockState(
                            cursor2.setWithOffset( cursor, Direction.DOWN ) ).isSolid() ) {
                        level.setBlock( cursor, AIR, 2 );
                    }
                    else if( (state.isSolid() || !state.getFluidState().is( Fluids.EMPTY )) && !state.is( Blocks.CHEST ) ) {
                        // Fill in the floor with a mix of mossy and normal cobble
                        if( y == -1 && random.nextInt( 4 ) != 0 ) {
                            safeSetBlock( level, cursor, config.floorMixProvider, random, predicate );
                        }
                        // Build the cobble walls
                        else {
                            safeSetBlock( level, cursor, config.baseProvider, random, predicate );
                        }
                    }
                }
            }
        }
        
        // Place loot chests
        //TODO Figure out some way to base the chests on which feature generates (e.g., give better/thematic loot for harder/weirder dungeons)
        for( int i = 0; i < 2; ++i ) {
            for( int j = 0; j < 3; ++j ) {
                cursor.setWithOffset( origin,
                        random.nextInt( xOffset * 2 + 1 ) - xOffset,
                        0,
                        random.nextInt( zOffset * 2 + 1 ) - zOffset );
                
                if( level.isEmptyBlock( cursor ) ) {
                    // Count horizontal solid neighbor blocks
                    int solidNeighbors = 0;
                    for( Direction direction : Direction.Plane.HORIZONTAL ) {
                        BlockState neighborState = level.getBlockState( cursor2.setWithOffset( cursor, direction ) );
                        
                        // Don't count chests as solid so we don't place multiple chests right next to each other.
                        if( neighborState.isSolid() && !neighborState.is( Tags.Blocks.CHESTS ) ) {
                            solidNeighbors++;
                        }
                    }
                    
                    // There is only one solid neighbor, we are facing a wall!
                    if( solidNeighbors == 1 ) {
                        safeSetBlock( level, cursor, randomizeChestDirection( level, cursor,
                                Blocks.CHEST.defaultBlockState(), random, true ), predicate );
                        RandomizableContainerBlockEntity.setLootTable( level, random, cursor, BuiltInLootTables.SIMPLE_DUNGEON );
                        break;
                    }
                }
            }
        }
        
        final DungeonConfig.SimpleDungeonCategory featureConfig = Config.getDimensionConfigs( level.getLevel() ).SIMPLE_DUNGEONS.NORMAL;
        
        // Generate debug marker
        debugMarkerIfEnabled( level, origin, featureConfig );
        
        // TODO - Make it so subfeatures can be specified via data pack
        final List<String> subfeatureList = featureConfig.subfeatures.get();
        if( !subfeatureList.isEmpty() ) { // No subfeatures defined in config, very sad
            placeSubfeature( level, context.chunkGenerator(), origin, ResourceLocation.parse(
                            subfeatureList.get( random.nextInt( subfeatureList.size() ) ) ),
                    new ConfiguredFeature<>( this, config ) );
        }
        return true;
    }
}