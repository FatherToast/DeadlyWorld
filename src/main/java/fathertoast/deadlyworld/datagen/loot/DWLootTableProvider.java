package fathertoast.deadlyworld.datagen.loot;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DWLootTableProvider extends LootTableProvider {
    
    public static final float RARITY_UNCOMMON = 0.25F;
    public static final float RARITY_RARE = 0.025F;
    
    public DWLootTableProvider( DataGenerator generator ) { super( generator ); }
    
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        ImmutableList.Builder<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> builder = new ImmutableList.Builder<>();
        
        //TODO Event loot tables
        //TODO Block loot tables
        builder.add( Pair.of( DWChestLootTables::new, LootParameterSets.CHEST ) );
        
        return builder.build();
    }
    
    @Override
    protected void validate( Map<ResourceLocation, LootTable> map, ValidationTracker validationTracker ) { /* NOOP */ }
}