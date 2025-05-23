package fathertoast.deadlyworld.common.loot.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.MainConfig;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.core.registry.DWLootModifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * This loot modifier is 100% config driven and is responsible
 * for adding "Mimic Cores" to desired loot tables.
 */
public class ChestMimicLootModifier extends LootModifier {

    public static final Supplier<Codec<ChestMimicLootModifier>> CODEC = () -> RecordCodecBuilder.create(inst -> LootModifier.codecStart( inst )
            .apply( inst, ChestMimicLootModifier::new )
    );

    public ChestMimicLootModifier( LootItemCondition[] conditions ) {
        super( conditions );
    }

    @Override
    @NotNull
    protected ObjectArrayList<ItemStack> doApply( ObjectArrayList<ItemStack> generatedLoot, LootContext context ) {
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        Double[] values = Config.ENTITIES.MIMICS.chestTargetLootTables.getValuesFor( lootTableId );

        if ( values != null ) {
            double chance = values[0];

            if ( context.getRandom().nextFloat() <= chance ) {
                generatedLoot.add( new ItemStack( DWItems.MIMIC_CORE.get() ) );
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return DWLootModifiers.CHEST_MIMIC.get();
    }
}
