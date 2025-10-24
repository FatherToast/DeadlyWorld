package fathertoast.deadlyworld.common.world.levelgen.dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantFloatProvider;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.Tags;

import java.util.function.Predicate;

public class MiniDungeonFeature extends DeadlyFeature<MiniDungeonFeature.Configuration> {
    public record Configuration(
            BlockStateProvider floorProvider,
            BlockStateProvider wallProvider,
            FloatProvider infestedChance,
            SpawnerSettings spawnerSettings,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<MiniDungeonFeature.Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "floor_provider" ).forGetter( MiniDungeonFeature.Configuration::floorProvider ),
                BlockStateProvider.CODEC.fieldOf( "wall_provider" ).forGetter( MiniDungeonFeature.Configuration::wallProvider ),
                FloatProvider.CODEC.fieldOf( "infested_chance" ).forGetter( MiniDungeonFeature.Configuration::infestedChance ),
                SpawnerSettings.CODEC.fieldOf( "spawner" ).forGetter( MiniDungeonFeature.Configuration::spawnerSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( MiniDungeonFeature.Configuration::cannotReplace )
        ).apply( instance, MiniDungeonFeature.Configuration::new ) );
        
        public static Configuration of( DimensionConfigGroup dimConfigs, Block baseBlock, Block altBlock, TagKey<Block> cannotReplace ) {
            return new Configuration(
                    new WeightedStateProvider( new SimpleWeightedRandomList.Builder<BlockState>()
                            .add( baseBlock.defaultBlockState(), 1 ).add( altBlock.defaultBlockState(), 3 ) ),
                    BlockStateProvider.simple( baseBlock ),
                    ConfigConstantFloatProvider.of( dimConfigs.DUNGEONS.MINI.infestedChance ),
                    SpawnerSettings.of( SpawnerType.MINI, dimConfigs ),
                    cannotReplace );
        }
    }
    
    public MiniDungeonFeature() {
        this( MiniDungeonFeature.Configuration.CODEC );
    }
    
    public MiniDungeonFeature( Codec<MiniDungeonFeature.Configuration> codec ) {
        super( codec );
    }
    
    @Override
    public boolean place( FeaturePlaceContext<MiniDungeonFeature.Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        final BlockState AIR = Blocks.AIR.defaultBlockState();
        final BlockPos origin = context.origin();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos cursor2 = new BlockPos.MutableBlockPos();
        
        int xOffset = 2;
        int minX = -xOffset - 1;
        int maxX = xOffset + 1;
        
        int zOffset = 2;
        int minZ = -zOffset - 1;
        int maxZ = zOffset + 1;
        
        // Check if we can place here
        if( notSubfeature ) {
            // Scan for a moderate area of solid blocks that is close to a cave opening
            int openWallSpace = 0;
            for( int x = minX; x <= maxX; ++x ) {
                for( int y = -1; y <= 2; ++y ) {
                    for( int z = minZ; z <= maxZ; ++z ) {
                        cursor.setWithOffset( origin, x, y, z );
                        
                        // Return if we don't have a solid floor or ceiling
                        if( (y == -1 || y == 2) && !level.getBlockState( cursor ).isSolid() ) {
                            return false;
                        }
                        
                        // Check if each wall position at Y 0 plus the above block are air blocks
                        if( y == 0 && (x == minX || x == maxX || z == minZ || z == maxZ) &&
                                level.isEmptyBlock( cursor ) ) {
                            openWallSpace++;
                        }
                    }
                }
            }
            // Are there enough "holes" where the walls should be? If not, abort
            if( openWallSpace < 1 || openWallSpace > 3 ) return false;
        }
        
        // Build the dungeon room
        for( int x = minX; x <= maxX; ++x ) {
            for( int y = 1; y >= -1; --y ) {
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
                        safeSetInfestedBlock( level, cursor, y == -1 ? config.floorProvider : config.wallProvider,
                                config.infestedChance, random, predicate );
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
                                DWBlocks.MINI_CHEST.get().defaultBlockState(), random, true ), predicate );
                        RandomizableContainerBlockEntity.setLootTable( level, random, cursor, BuiltInLootTables.SIMPLE_DUNGEON );
                        break;
                    }
                }
            }
        }
        
        // Generate debug marker if enabled
        debugMarkerIfEnabled( level, origin, Config.getDimensionConfigs( level.getLevel() ).DUNGEONS.MINI );
        
        // Lastly, place mini spawner
        setBlock( level, origin, DWBlocks.spawner( SpawnerType.MINI ).get().defaultBlockState() );
        config.spawnerSettings.initializeSpawner( level, origin, random );
        
        return true;
    }
}