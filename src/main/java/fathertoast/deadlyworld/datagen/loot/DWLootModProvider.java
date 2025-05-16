package fathertoast.deadlyworld.datagen.loot;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.loot.glm.SimpleAddLootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.Arrays;

public class DWLootModProvider extends GlobalLootModifierProvider {

    public DWLootModProvider( PackOutput packOutput ) {
        super( packOutput, DeadlyWorld.MOD_ID );
    }

    @Override
    protected void start() {
        add("mimic", new SimpleAddLootModifier(
                new LootItemCondition[]{},
                DWItems.MIMIC_CORE.get(),
                0.1D,
                Arrays.asList(
                        new ResourceLocation("chests/simple_dungeon"),
                        new ResourceLocation("chests/desert_pyramid"),
                        new ResourceLocation("chests/jungle_temple"),
                        new ResourceLocation("chests/abandoned_mineshaft")
                ))
        );
    }
}
