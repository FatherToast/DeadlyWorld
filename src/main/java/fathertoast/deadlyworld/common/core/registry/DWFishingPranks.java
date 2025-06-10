package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.api.FishingPrank;
import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.fishpranks.MobPrank;
import fathertoast.deadlyworld.common.fishpranks.SingleTntPrank;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DWFishingPranks {

    public static final DeferredRegister<FishingPrank> REGISTRY = DeferredRegister.create( DeadlyWorld.resourceLoc( "fishing_pranks" ), DeadlyWorld.MOD_ID );

    static {
        DWRegistries.FISHING_PRANKS_REGISTRY = REGISTRY.makeRegistry(() -> {
            return (new RegistryBuilder<FishingPrank>()).disableSync();
        });
    }


    public static final RegistryObject<FishingPrank> SINGLE_TNT = register( "single_tnt", SingleTntPrank::new );
    public static final RegistryObject<FishingPrank> MOB = register( "mob", MobPrank::new );

    private static RegistryObject<FishingPrank> register( String name, Supplier<FishingPrank> supplier ) {
        return REGISTRY.register( name, supplier );
    }
}
