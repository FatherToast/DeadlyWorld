package fathertoast.deadlyworld.api.impl;

import fathertoast.deadlyworld.api.DecoyType;
import fathertoast.deadlyworld.api.client.IClientRegisterHelper;
import fathertoast.deadlyworld.api.client.IDecoyRenderer;
import fathertoast.deadlyworld.client.DecoyRendererRegistry;

import java.util.function.Supplier;

public class ClientRegisterHelperImpl implements IClientRegisterHelper {

    @Override
    public void registerDecoyRenderer( DecoyType type, Supplier<IDecoyRenderer> decoyRenderer ) {
        DecoyRendererRegistry.register( type, decoyRenderer );
    }
}
