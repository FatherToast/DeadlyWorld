package fathertoast.deadlyworld.common.loot.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class SimpleAddLootModifier extends LootModifier {

    private static final ListCodec<ResourceLocation> RL_LIST_CODEC = new ListCodec<>( ResourceLocation.CODEC );

    public final Item itemToAdd;
    public final double chance;
    public final int maxStackCount;
    public final int minStackCount;
    public final List<ResourceLocation> lootTables;


    public static final Supplier<Codec<SimpleAddLootModifier>> CODEC = () -> RecordCodecBuilder.create( inst -> LootModifier.codecStart( inst )
            .and( inst.group(
                            ForgeRegistries.ITEMS.getCodec()
                                    .fieldOf( "item" )
                                    .forGetter( m -> m.itemToAdd ),
                            Codec.DOUBLE.fieldOf( "chance" )
                                    .forGetter( m -> m.chance ),
                            Codec.INT.fieldOf( "maxCount" )
                                    .forGetter( m -> m.maxStackCount ),
                            Codec.INT.fieldOf( "minCount" )
                                    .forGetter( m -> m.minStackCount ),
                            RL_LIST_CODEC
                                    .fieldOf( "lootTable" )
                                    .forGetter( m -> m.lootTables )
                    )
            )
            .apply( inst, SimpleAddLootModifier::new )
    );

    /**
     * Constructs a LootModifier.<br>
     * Variable minimum and maximum stack size.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public SimpleAddLootModifier( LootItemCondition[] conditionsIn, Item itemToAdd, double chance, int maxStackCount, int minStackCount, List<ResourceLocation> lootTables ) {
        super(conditionsIn);
        this.itemToAdd = itemToAdd;
        this.chance = chance;
        this.maxStackCount = maxStackCount;
        this.minStackCount = minStackCount;
        this.lootTables = lootTables;
    }

    /**
     * Constructs a LootModifier.<br>
     * The resulting loot stack will always have a size of 1.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public SimpleAddLootModifier( LootItemCondition[] conditionsIn, Item itemToAdd, double chance, List<ResourceLocation> lootTables ) {
        super(conditionsIn);
        this.itemToAdd = itemToAdd;
        this.chance = chance;
        this.maxStackCount = 1;
        this.minStackCount = 1;
        this.lootTables = lootTables;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply( ObjectArrayList<ItemStack> generatedLoot, LootContext context ) {
        if ( lootTables.contains( context.getQueriedLootTableId() ) ) {
            Random random = new Random();

            if ( random.nextDouble() <= chance ) {
                if ( minStackCount == 1 && maxStackCount == minStackCount ) {
                    generatedLoot.add( new ItemStack( itemToAdd ) );
                    return generatedLoot;
                }
                int stackSize = minStackCount + random.nextInt( (maxStackCount - minStackCount) + 1 );
                ItemStack stack = new ItemStack ( itemToAdd, stackSize );
                generatedLoot.add( stack );
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return DWLootModifiers.SIMPLE_ADD.get();
    }
}
