package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.util.DamageSource;

public class DWDamageSources {

    public static final DamageSource VORTEX = create("vortex").bypassArmor().bypassMagic();


    private static DamageSource create(String name) {
        return new DamageSource(DeadlyWorld.MOD_ID + "." + name);
    }

    public static void init() {}

    // Utility class, instantiation pointless
    private DWDamageSources() {}
}
