package fathertoast.deadlyworld.datagen.loot;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.loot.glm.ChestMimicLootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class DWLootModProvider extends GlobalLootModifierProvider {
    
    public DWLootModProvider( PackOutput packOutput ) {
        super( packOutput, DeadlyWorld.MOD_ID );
    }
    
    @Override
    protected void start() {
        add( "chest_mimic", new ChestMimicLootModifier( new LootItemCondition[] {} ) );
    }
}
