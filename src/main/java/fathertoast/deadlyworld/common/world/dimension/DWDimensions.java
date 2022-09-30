package fathertoast.deadlyworld.common.world.dimension;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DWDimensions {

    public static final RegistryKey<World> SEWERS_WORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, DeadlyWorld.resourceLoc("sewers"));
}
