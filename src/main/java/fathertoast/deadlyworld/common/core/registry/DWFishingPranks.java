package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.IFishingPrank;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.fishpranks.MobPrank;
import fathertoast.deadlyworld.common.fishpranks.SingleTntPrank;
import fathertoast.deadlyworld.common.fishpranks.SnagPrank;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class DWFishingPranks {
    
    public static final DeferredRegister<IFishingPrank> REGISTRY = DeferredRegister.create( DeadlyWorld.rl( "fishing_pranks" ), DeadlyWorld.MOD_ID );
    
    static {
        DWRegistries.FISHING_PRANKS_REGISTRY = REGISTRY.makeRegistry( () ->
                (new RegistryBuilder<IFishingPrank>()).disableSync() );
    }
    
    
    public static final RegistryObject<IFishingPrank> SINGLE_TNT = register( "single_tnt", SingleTntPrank::new );
    public static final RegistryObject<IFishingPrank> YEET_TNT = register( "yeet_tnt", SingleTntPrank::new );
    public static final RegistryObject<IFishingPrank> MOB = register( "mob", MobPrank::new );
    public static final RegistryObject<IFishingPrank> SNAG = register( "snag", SnagPrank::new );
    
    
    private static RegistryObject<IFishingPrank> register( String name, Supplier<IFishingPrank> supplier ) {
        return REGISTRY.register( name, supplier );
    }
}