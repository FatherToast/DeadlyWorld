package fathertoast.deadlyworld.common.world.levelgen.trap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Predicate;

public class LoneSpawnerFeature extends DeadlyFeature<LoneSpawnerFeature.Configuration> {
    public record Configuration(
            BlockStateProvider spawnerProvider,
            BlockStateProvider topperProvider,
            SpawnerSettings spawnerSettings,
            TagKey<Block> cannotReplace,
            boolean vinesDecoration
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "trap_provider" ).forGetter( Configuration::spawnerProvider ),
                BlockStateProvider.CODEC.fieldOf( "topper_provider" ).forGetter( Configuration::topperProvider ),
                SpawnerSettings.CODEC.fieldOf( "spawner" ).forGetter( Configuration::spawnerSettings ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( Configuration::cannotReplace ),
                Codec.BOOL.fieldOf( "vines_decoration" ).orElse( false ).forGetter( Configuration::vinesDecoration )
        ).apply( instance, Configuration::new ) );
    }
    
    public LoneSpawnerFeature() { this( Configuration.CODEC ); }
    
    public LoneSpawnerFeature( Codec<Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        
        // Check if we can place here
        if( notSubfeature ) {
            // TODO - replace with something less bad
            if( hasNearbyTraps( level, context.origin(), 3 ) ) return false;
            
            // Make sure the spawner block at least can be placed
            if( !predicate.test( level.getBlockState( context.origin() ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( context.origin() ) != null ) return false;
        }
        
        // Place the spawner
        BlockState spawnerBlock = config.spawnerProvider.getState( random, context.origin() );
        setBlock( level, context.origin(), spawnerBlock );
        if( spawnerBlock.getBlock() instanceof DeadlySpawnerBlock ) {
            config.spawnerSettings.initializeSpawner( level, context.origin(), random );
        }
        
        // Optionally place the topper
        BlockPos topperPos = context.origin().above();
        BlockState topperBlock = config.topperProvider.getState( random, topperPos );
        boolean hasTopper = !topperBlock.isAir();
        if( hasTopper ) {
            safeSetBlock( level, topperPos, topperBlock, predicate );
        }
        
        // Optionally decorate the spawner with vines
        if( config.vinesDecoration() ) {
            final BlockPos.MutableBlockPos cursor = context.origin().mutable();
            placeVinesAround( level, context.origin(), cursor, random );
            if( hasTopper ) placeVinesAround( level, topperPos, cursor, random );
        }
        
        return true;
    }
    
    protected void placeVinesAround( WorldGenLevel level, BlockPos center, BlockPos.MutableBlockPos cursor, RandomSource random ) {
        for( Direction dir : Direction.Plane.HORIZONTAL ) {
            if( random.nextInt( 4 ) == 0 ) {
                cursor.setWithOffset( center, dir );
                safeSetBlock( level, cursor, Blocks.VINE.defaultBlockState()
                        .setValue( VineBlock.getPropertyForFace( dir.getOpposite() ), true ), IS_AIR );
            }
        }
    }
}