package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.DecoyType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Collections;

public final class DWDecoyTypes {

    public static final DeferredRegister<DecoyType> REGISTRY = DeferredRegister.create( DeadlyWorld.resourceLoc( "decoy_types" ), DeadlyWorld.MOD_ID );
    static {
        DWRegistries.DECOY_TYPE_REGISTRY = REGISTRY.makeRegistry(RegistryBuilder::new);
    }


    public static final RegistryObject<DecoyType> CAKE = register( "cake" );
    public static final RegistryObject<DecoyType> ZOMBIE = register( "zombie" );
    public static final RegistryObject<DecoyType> CREEPER = register( "creeper" );


    private static RegistryObject<DecoyType> register( String name ) {
        return REGISTRY.register( name, DecoyType::new );
    }

    @Nullable
    public static DecoyType getRandomType( RandomSource random ) {
        DecoyType[] types = DWRegistries.DECOY_TYPE_REGISTRY.get().getValues().toArray( new DecoyType[0] );
        return types[ random.nextInt( types.length ) ];
    }
}
