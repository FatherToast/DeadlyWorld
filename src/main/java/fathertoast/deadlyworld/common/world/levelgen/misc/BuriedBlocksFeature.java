package fathertoast.deadlyworld.common.world.levelgen.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.EnvHazardConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;

public class BuriedBlocksFeature extends DeadlyFeature<BuriedBlocksFeature.Configuration> {
    public record Configuration(
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( Configuration::cannotReplace )
        ).apply( instance, Configuration::new ) );
    }
    
    public BuriedBlocksFeature() {
        super( Configuration.CODEC );
    }
    
    public BuriedBlocksFeature( Codec<Configuration> codec ) {
        super( codec );
    }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final WorldGenLevel level = context.level();
        final RandomSource random = context.random();
        final ChunkPos chunkPos = new ChunkPos( context.origin() );
        final EnvHazardConfig.BuriedBlocksCategory category = Config.getDimensionConfigs( level.getLevel() ).ENV_HAZARDS.BURIED_BLOCKS;
        final Predicate<BlockState> predicate = ( state ) -> !state.is( context.config().cannotReplace );
        
        try {
            final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            
            // Go through each buried block specified in the config
            for( RegistryValueEntry<Block> entry : category.list.get().getEntries() ) {
                final Block block = ForgeRegistries.BLOCKS.getValue( entry.REG_KEY );
                if( block == null ) continue;
                final BlockState blockState = block.defaultBlockState();
                final int minY = Math.max( (int) entry.VALUES[0], level.getMinBuildHeight() );
                final int maxY = Math.min( (int) entry.VALUES[1], level.getMaxBuildHeight() );
                final boolean areYEqual = minY >= maxY;
                final int placementTries = (int) entry.VALUES[2];
                
                for( int i = 0; i < placementTries; i++ ) {
                    // Pick a random position
                    pos.set(
                            chunkPos.getMinBlockX() + random.nextInt( chunkPos.getMaxBlockX() - chunkPos.getMinBlockX() ),
                            areYEqual ? minY : Mth.randomBetweenInclusive( random, minY, maxY ),
                            chunkPos.getMinBlockZ() + random.nextInt( chunkPos.getMaxBlockZ() - chunkPos.getMinBlockZ() )
                    );
                    
                    // Check placement conditions
                    boolean canPlace = true;
                    for( Direction dir : Direction.values() ) {
                        cursor.setWithOffset( pos, dir );
                        if( level.getExistingBlockEntity( cursor ) != null
                                || !level.getBlockState( cursor ).isSolidRender( level, cursor ) ) {
                            canPlace = false;
                            break;
                        }
                    }
                    if( !canPlace ) continue;
                    
                    // Place the block
                    if( block instanceof ChestBlock ) {
                        safeSetBlock( level, pos, blockState.setValue( HorizontalDirectionalBlock.FACING,
                                Direction.Plane.HORIZONTAL.getRandomDirection( random ) ), predicate );
                        ResourceLocation lootTable = ResourceLocation.tryParse( category.chestLootTable.get() );
                        if( lootTable != null && level.getBlockEntity( pos ) instanceof RandomizableContainerBlockEntity chestBlockEntity ) {
                            chestBlockEntity.setLootTable( lootTable, random.nextLong() );
                        }
                    }
                    else {
                        safeSetBlock( level, pos, blockState, predicate );
                    }
                }
            }
            return true;
        }
        catch( Exception ex ) {
            DeadlyWorld.LOG.error( "Encountered a problem while generating buried blocks!" );
            ex.printStackTrace();
            return false;
        }
    }
}