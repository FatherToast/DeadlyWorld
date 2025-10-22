package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.DecoyType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fathertoast.deadlyworld.common.core.registry.DWTags.DecoyTypes.*;

public final class DWDecoyTypes {

    public static final DeferredRegister<DecoyType> REGISTRY = DeferredRegister.create( DeadlyWorld.rl( "decoy_types" ), DeadlyWorld.MOD_ID );
    static {
        DWRegistries.DECOY_TYPE_REGISTRY = REGISTRY.makeRegistry( () -> new RegistryBuilder<DecoyType>().hasTags() );
    }
    public static final Map<TagKey<DecoyType>, List<RegistryObject<DecoyType>>> ENTRIES_FOR_TAGS = new HashMap<>();


    public static final RegistryObject<DecoyType> CAKE = register( "cake", ANY_DIMENSION, OVERWORLD, THE_NETHER );
    public static final RegistryObject<DecoyType> PIG = register( "pig", ANY_DIMENSION, OVERWORLD, THE_NETHER );
    public static final RegistryObject<DecoyType> ZOMBIE = register( "zombie", ANY_DIMENSION, OVERWORLD );
    public static final RegistryObject<DecoyType> CREEPER = register( "creeper", ANY_DIMENSION, OVERWORLD );
    public static final RegistryObject<DecoyType> SKELETON = register( "skeleton", ANY_DIMENSION, OVERWORLD );
    public static final RegistryObject<DecoyType> SPIDER = register( "spider", ANY_DIMENSION, OVERWORLD );
    public static final RegistryObject<DecoyType> SLIME = register( "slime", ANY_DIMENSION, OVERWORLD );
    public static final RegistryObject<DecoyType> ZOMBIFIED_PIGLIN = register( "zombified_piglin", THE_NETHER );
    public static final RegistryObject<DecoyType> WITHER_SKELETON = register( "wither_skeleton", THE_NETHER );
    public static final RegistryObject<DecoyType> MAGMA_CUBE = register( "magma_cube", THE_NETHER );


    /**
     *  Registers a decoy type and associates it with
     *  the given tags for data gen.
     */
    @SafeVarargs
    private static RegistryObject<DecoyType> register( String name, TagKey<DecoyType>... tags ) {
        RegistryObject<DecoyType> regObj = REGISTRY.register( name, DecoyType::new );

        for ( TagKey<DecoyType> tagKey : tags ) {
            if ( !ENTRIES_FOR_TAGS.containsKey( tagKey ) ) {
                ENTRIES_FOR_TAGS.put( tagKey, new ArrayList<>() );
            }
            ENTRIES_FOR_TAGS.get( tagKey ).add( regObj );
        }
        return regObj;
    }

    /** @return A random decoy type from the registry. */
    @Nullable
    public static DecoyType getRandomType( RandomSource random ) {
        DecoyType[] types = DWRegistries.DECOY_TYPE_REGISTRY.get().getValues().toArray( new DecoyType[0] );

        if ( types.length < 2 ) {
            DeadlyWorld.LOG.error( "Abnormal state for decoy type registry. Cannot pick a random type when registry contains less than 2 entries!" );
            return null;
        }
        return types[ random.nextInt( types.length ) ];
    }
}
