package fathertoast.deadlyworld.datagen.loot;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DWLootTableProvider extends LootTableProvider {
    public DWLootTableProvider( PackOutput output ) {
        super( output, Set.of(), List.of(
                new SubProviderEntry( DWEntityLootTables::new, LootContextParamSets.ENTITY )
        ) );
    }
    
    @Override
    protected void validate( Map<ResourceLocation, LootTable> map, ValidationContext validationcontext ) {
        // Bruh
    }
}