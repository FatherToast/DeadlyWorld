package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.DecoyType;
import fathertoast.deadlyworld.api.client.IDecoyRenderer;
import fathertoast.deadlyworld.client.renderer.decoy.SimpleBlockDecoyRenderer;
import fathertoast.deadlyworld.client.renderer.decoy.SimpleEntityDecoyRenderer;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWDecoyTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Helper class for registering decoy renderers.
 */
public class DecoyRendererRegistry {

    /** A map of type-to-factory. This map is queried on resource reload and constructs the renderers. */
    private static final Map<DecoyType, Supplier<IDecoyRenderer>> FACTORIES = new ConcurrentHashMap<>();
    /** The map containing the actual decoy renderers linked to their type. */
    private static final Map<DecoyType, IDecoyRenderer> RENDERERS = new HashMap<>();


    /** Registers decoy renderer factories added by Deadly World. */
    protected static void registerDefaults() {
        register( DWDecoyTypes.CAKE.get(), () -> new SimpleBlockDecoyRenderer( Blocks.CAKE ) );
        register( DWDecoyTypes.ZOMBIE.get(), () -> new SimpleEntityDecoyRenderer( EntityType.ZOMBIE ) );
        register( DWDecoyTypes.CREEPER.get(), () -> new SimpleEntityDecoyRenderer( EntityType.CREEPER ) );
    }


    /**
     * Registers a decoy factory and maps it to the given decoy type.
     * <br><br>
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     */
    public static void register( DecoyType type, Supplier<IDecoyRenderer> rendererSupplier ) {
        Objects.requireNonNull( type );
        Objects.requireNonNull( rendererSupplier );

        FACTORIES.put( type, rendererSupplier );
    }

    /**
     * @return A new decoy renderer instance for the given decoy type.
     *         Returns null if no factory is registered for the decoy type.
     */
    @Nullable
    public static IDecoyRenderer getRendererForType( DecoyType type ) {
        return RENDERERS.get( type );
    }

    /**
     * Queries the registered factories to instantiate
     * and validate decoy renderers.
     * <br><br>
     * Called from {@link ClientRegister#onAddLayers(EntityRenderersEvent.AddLayers)}
     */
    protected static void setupDecoyRenderers() {
        RENDERERS.clear();

        FACTORIES.forEach( (decoyType, supplier) -> {
            try {
                IDecoyRenderer renderer = supplier.get();
                RENDERERS.put( decoyType, renderer );
            }
            catch ( Exception e ) {
                DeadlyWorld.LOG.error( "Failed to construct decoy renderer for type \"{}\"!", DWRegistries.DECOY_TYPE_REGISTRY.get().getKey( decoyType ) );
                e.printStackTrace();
            }
        });
    }
}
