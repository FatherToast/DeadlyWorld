package fathertoast.deadlyworld.api.impl;

import fathertoast.deadlyworld.api.IDeadlyWorldApi;
import fathertoast.deadlyworld.api.client.IClientRegisterHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class DeadlyWorldApi implements IDeadlyWorldApi {
    
    final IClientRegisterHelper helper = new ClientRegisterHelperImpl();
    
    /**
     * @return Deadly World's client register helper instance.
     * @throws IllegalStateException If called on dedicated server.
     */
    @Override
    public IClientRegisterHelper getClientRegisterHelper() {
        if( FMLEnvironment.dist == Dist.DEDICATED_SERVER )
            throw new IllegalStateException( "Client registry helper should not be accessed on dedicated servers!" );
        return helper;
    }
}
