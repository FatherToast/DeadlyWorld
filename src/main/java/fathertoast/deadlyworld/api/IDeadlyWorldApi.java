package fathertoast.deadlyworld.api;

import fathertoast.deadlyworld.api.client.IClientRegisterHelper;
import org.jetbrains.annotations.ApiStatus;

/**
 * This is the main API interface for accessing the Deadly World API's various helpers and registration utilities.
 * In order to interact with the API instance, you must create a {@link IDeadlyWorldPlugin mod plugin}.
 */
@ApiStatus.NonExtendable
public interface IDeadlyWorldApi {
    
    /** Deadly World's mod ID. */
    String MOD_ID = "deadlyworld";
    
    /**
     * @return Deadly World's client register helper instance.
     * @throws IllegalStateException If called on dedicated server.
     */
    IClientRegisterHelper getClientRegisterHelper();
}
