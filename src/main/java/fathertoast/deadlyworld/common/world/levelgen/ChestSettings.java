package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigFieldReference;
import fathertoast.deadlyworld.common.block.chest.ChestType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

import java.util.Objects;

public record ChestSettings(
        ResourceLocation lootTable, long lootTableSeed, ConfigFieldReference<Boolean> debugMarker
) {
    public static final Codec<ChestSettings> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            ResourceLocation.CODEC.fieldOf( "loot_table" ).forGetter( ChestSettings::lootTable ),
            Codec.LONG.fieldOf( "loot_table_seed" ).forGetter( ChestSettings::lootTableSeed ),
            ConfigFieldReference.BOOLEAN_CODEC.fieldOf( "debug_marker" ).forGetter( ChestSettings::debugMarker )
    ).apply( instance, ChestSettings::new ) );
    
    public static ChestSettings of( ChestType type, DimensionConfigGroup dimConfigs ) {
        return new ChestSettings( type.getChestLootTable(), 0, // We never use a fixed lootTableSeed
                new ConfigFieldReference<>( Objects.requireNonNull( type.getConfig( dimConfigs ).debugMarker ) ) );
    }
    
    public void initializeChest( WorldGenLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof RandomizableContainerBlockEntity chestBlockEntity ) {
            chestBlockEntity.setLootTable( lootTable(), lootTableSeed() == 0 ? random.nextLong() : lootTableSeed() );
            
            if( debugMarker().get() ) DeadlyFeature.debugMarker( level, pos );
        }
    }
}