package fathertoast.deadlyworld.common.world.levelgen.dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
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
        public static final Codec<SimpleDungeonFeature.Configuration> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "base_provider" ).forGetter( SimpleDungeonFeature.Configuration::baseProvider ),
                BlockStateProvider.CODEC.fieldOf( "floor_mix_provider" ).forGetter( SimpleDungeonFeature.Configuration::floorMixProvider ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( SimpleDungeonFeature.Configuration::cannotReplace )
        ).apply( instance, SimpleDungeonFeature.Configuration::new ) );
    }

    public SimpleDungeonFeature( ) {
        this( Configuration.CODEC );
    }

    public SimpleDungeonFeature(Codec<Configuration> codec ) {
        super( codec );
    }

    // TODO - place wall blocks even if the target block is a liquid source block.
    //        Random source blocks in the wall that doesn't update look janky
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final SimpleDungeonFeature.Configuration config = context.config();
        Predicate<BlockState> replaceable = Feature.isReplaceable( config.cannotReplace );
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        WorldGenLevel level = context.level();

        final BlockState AIR = Blocks.AIR.defaultBlockState();

        int xOffset = random.nextInt( 2 ) + 2;
        int minX = -xOffset - 1;
        int maxX = xOffset + 1;

        int zOffset = random.nextInt( 2 ) + 2;
        int minZ = -zOffset - 1;
        int maxZ = zOffset + 1;

        int openWallSpace = 0;

        // Scan for a moderate area of solid blocks that is close to a cave opening
        for ( int x = minX; x <= maxX; ++x ) {
            for ( int y = -1; y <= 4; ++y ) {
                for ( int z = minZ; z <= maxZ; ++z ) {
                    BlockPos offsetPos = origin.offset( x, y, z );
                    boolean isBlockSolid = level.getBlockState( offsetPos ).isSolid();

                    // Return if we don't have a solid floor at lowest Y
                    if ( y == -1 && !isBlockSolid ) {
                        return false;
                    }
                    // Return if we don't have a solid ceiling at highest Y
                    if ( y == 4 && !isBlockSolid ) {
                        return false;
                    }
                    // Check if each wall position at Y 0 plus the above block are air blocks
                    if ( ( x == minX || x == maxX || z == minZ || z == maxZ )
                            && y == 0
                            && level.isEmptyBlock( offsetPos )
                            && level.isEmptyBlock( offsetPos.above() ) ) {
                        ++openWallSpace;
                    }
                }
            }
        }
        // Are there enough "holes" where the walls should be? If not, abort
        if ( openWallSpace < 1 || openWallSpace > 6 ) return false;

        // Build the dungeon room
        for ( int x = minX; x <= maxX; ++x ) {
            for ( int y = 3; y >= -1; --y ) {
                for ( int z = minZ; z <= maxZ; ++z ) {
                    BlockPos offsetPos = origin.offset( x, y, z );
                    BlockState state = level.getBlockState( offsetPos );

                    // Hollow out the inside of the room with air
                    if ( x != minX && y != -1 && z != minZ && x != maxX && y != 4 && z != maxZ ) {
                        // We leave existing chests and trap things alone,
                        // so if another dungeon generated nearby they can merge without much problem.
                        if ( !state.is( Blocks.CHEST ) && !state.is( DWTags.Blocks.SPAWNERS.blockTag() ) && !state.is( DWTags.Blocks.TOWER_DISPENSERS.blockTag() ) ) {
                            safeSetBlock( level, offsetPos, AIR, replaceable );
                        }
                    }
                    else if ( offsetPos.getY() >= level.getMinBuildHeight() && !level.getBlockState( offsetPos.below() ).isSolid() ) {
                        level.setBlock( offsetPos, AIR, 2 );
                    }
                    else if ( state.isSolid() && !state.is( Blocks.CHEST ) ) {
                        // Fill in the floor with a mix of mossy and normal cobble
                        if ( y == -1 && random.nextInt( 4 ) != 0 ) {
                            safeSetBlock( level, offsetPos, config.floorMixProvider.getState( random, offsetPos ), replaceable );
                        }
                        // Build the cobble walls
                        else {
                            safeSetBlock(level, offsetPos, config.baseProvider.getState( random, offsetPos ), replaceable);
                        }
                    }
                }
            }
        }
        // Place loot chest
        for ( int i = 0; i < 2; ++i ) {
            for ( int j = 0; j < 3; ++j ) {
                int x = origin.getX() + random.nextInt( xOffset * 2 + 1 ) - xOffset;
                int y = origin.getY();
                int z = origin.getZ() + random.nextInt( zOffset * 2 + 1 ) - zOffset;
                BlockPos offsetPos = new BlockPos( x, y, z );

                if ( level.isEmptyBlock( offsetPos ) ) {
                    // Count horizontal solid neighbor blocks
                    int solidNeighbors = 0;

                    for ( Direction direction : Direction.Plane.HORIZONTAL ) {
                        BlockState neighborState = level.getBlockState( offsetPos.relative( direction ) );

                        // Don't count chests as solid so we don't place multiple chests right next to each other.
                        if ( neighborState.isSolid() && !neighborState.is( Tags.Blocks.CHESTS ) ) {
                            ++solidNeighbors;
                        }
                    }

                    // There is only one solid neighbor, we are facing a wall!
                    if ( solidNeighbors == 1 ) {
                        safeSetBlock( level, offsetPos, StructurePiece.reorient( level, offsetPos, Blocks.CHEST.defaultBlockState() ), replaceable );
                        RandomizableContainerBlockEntity.setLootTable( level, random, offsetPos, BuiltInLootTables.SIMPLE_DUNGEON );
                        break;
                    }
                }
            }
        }
        // Generate debug marker if enabled
        debugMarkerIfEnabled( level, origin, Config.getDimensionConfigs( level.getLevel() ).SIMPLE_DUNGEONS.NORMAL );

        // TODO - Make it so subfeatures can be specified via data pack
        try {
            final List<String> subfeatureList = Config.getDimensionConfigs( level.getLevel() ).SIMPLE_DUNGEONS.NORMAL.subfeatures.get();

            // No subfeatures defined in config, very sad
            if ( subfeatureList.isEmpty() )
                return false;

            // Grab random subfeature ID from subfeature list
            final String subfeatureId = subfeatureList.get( random.nextInt( subfeatureList.size() ) );
            final Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().registryOrThrow( Registries.CONFIGURED_FEATURE );

            ConfiguredFeature<?, ?> subfeature = registry.get( ResourceLocation.parse( subfeatureId ) );

            if ( subfeature != null ) {
                return subfeature.place( level, context.chunkGenerator(), random, origin );
            }
        }
        catch ( Exception e ) {
            DeadlyWorld.LOG.warn( "Failed to place subfeature for \"{}\"!", getClass().getSimpleName() );
            e.printStackTrace();
        }
        return false;
    }
}
