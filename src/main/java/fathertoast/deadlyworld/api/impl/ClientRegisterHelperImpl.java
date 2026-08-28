package fathertoast.deadlyworld.api.impl;

import fathertoast.deadlyworld.api.client.IClientRegisterHelper;
import fathertoast.deadlyworld.api.client.IDecoyRenderer;
import fathertoast.deadlyworld.api.registry.decoy.DecoyType;
import fathertoast.deadlyworld.client.DecoyRendererRegistry;

import java.util.function.Supplier;

public final class ClientRegisterHelperImpl implements IClientRegisterHelper {
    
    /**
     * Registers a decoy renderer.
     *
     * @param type          The decoy type to register a renderer for.
     * @param decoyRenderer The decoy renderer to register and associate with the given decoy type.
     * @throws NullPointerException If either {@code type} or {@code decoyRenderer} is null.
     */
    @Override
    public void registerDecoyRenderer( DecoyType type, Supplier<IDecoyRenderer> decoyRenderer ) {
        DecoyRendererRegistry.register( type, decoyRenderer );
    }
}
