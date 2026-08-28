package fathertoast.deadlyworld.api.client;

import fathertoast.deadlyworld.api.registry.decoy.DecoyType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/** Contains various helper methods related to clientside registration. */
@ApiStatus.NonExtendable
public interface IClientRegisterHelper {
    
    /**
     * Registers a decoy renderer.
     *
     * @param type          The decoy type to register a renderer for.
     * @param decoyRenderer The decoy renderer to register and associate with the given decoy type.
     * @throws NullPointerException If either {@code type} or {@code decoyRenderer} is null.
     */
    void registerDecoyRenderer( DecoyType type, Supplier<IDecoyRenderer> decoyRenderer );
}
