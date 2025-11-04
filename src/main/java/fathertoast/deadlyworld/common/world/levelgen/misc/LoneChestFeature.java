package fathertoast.deadlyworld.common.world.levelgen.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.entity.FloorTrapBlockEntity;
import fathertoast.deadlyworld.common.world.levelgen.ChestSettings;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Predicate;

public class LoneChestFeature extends DeadlyFeature<LoneChestFeature.Configuration> {
    public record Configuration(
            BlockStateProvider chestProvider,
            ChestSettings chestSettings,
            ResourceLocation floorTrap,
            TagKey<Block> cannotReplace
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
                BlockStateProvider.CODEC.fieldOf( "chest_provider" ).forGetter( Configuration::chestProvider ),
                ChestSettings.CODEC.fieldOf( "chest" ).forGetter( Configuration::chestSettings ),
                ResourceLocation.CODEC.fieldOf( "floor_trap" ).forGetter( Configuration::floorTrap ),
                TagKey.hashedCodec( Registries.BLOCK ).fieldOf( "cannot_replace" ).forGetter( Configuration::cannotReplace )
        ).apply( instance, Configuration::new ) );
    }
    
    public LoneChestFeature() { this( Configuration.CODEC ); }
    
    public LoneChestFeature( Codec<Configuration> codec ) { super( codec ); }
    
    @Override
    public boolean place( FeaturePlaceContext<Configuration> context ) {
        final boolean notSubfeature = context.topFeature().isEmpty();
        final Configuration config = context.config();
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final Predicate<BlockState> predicate = isReplaceable( config.cannotReplace );
        final BlockPos.MutableBlockPos chestPos = context.origin().mutable();
        
        final ConfiguredFeature<?, ?> floorTrap = getFeature( level, config.floorTrap );
        if( floorTrap != null ) {
            // Move up if on a lip; only should be done if we are generating a floor trap as well
            if( isOnLip( level, chestPos ) ) chestPos.move( Direction.UP );
        }
        
        // Check if we can place here
        if( notSubfeature ) {
            // Make sure the chest block can be placed
            if( !predicate.test( level.getBlockState( chestPos ) ) ) return false;
            // Don't replace blocks with block entities
            if( level.getExistingBlockEntity( chestPos ) != null ) return false;
            
            if( floorTrap != null ) {
                chestPos.move( Direction.DOWN );
                // Make sure the trap block can be placed
                if( !predicate.test( level.getBlockState( chestPos ) ) ) return false;
                // Don't replace blocks with block entities
                if( level.getExistingBlockEntity( chestPos ) != null ) return false;
                chestPos.move( Direction.UP );
            }
        }
        
        // Place the trap
        if( floorTrap != null ) {
            placeSubfeature( level, context.chunkGenerator(), context.origin(), floorTrap,
                    new ConfiguredFeature<>( this, config ) );
            
            // Prevent floor trap from using a decoy
            chestPos.move( Direction.DOWN );
            if( level.getBlockEntity( chestPos ) instanceof FloorTrapBlockEntity trapBlockEntity ) {
                trapBlockEntity.getTrapLogic().disableDecoy();
            }
            chestPos.move( Direction.UP );
        }
        
        // Place the chest
        setBlock( level, chestPos, randomizeChestDirection( level, chestPos,
                config.chestProvider.getState( random, chestPos ), random, false ) );
        config.chestSettings.initializeChest( level, chestPos, random );
        
        return true;
    }
}