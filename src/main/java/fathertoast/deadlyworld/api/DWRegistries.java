package fathertoast.deadlyworld.api;

import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class DWRegistries {

    /**
     * The forge registry for Deadly World fishing pranks.<br><br>
     * Note that any new pranks registered will not be picked in-game
     * unless a new entry is added in the Deadly World fishing pranks config.
     */
    public static Supplier<IForgeRegistry<FishingPrank>> FISHING_PRANKS_REGISTRY;

    private DWRegistries() {}
}
