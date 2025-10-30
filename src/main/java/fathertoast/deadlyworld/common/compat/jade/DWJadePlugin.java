package fathertoast.deadlyworld.common.compat.jade;

import fathertoast.deadlyworld.common.core.registry.IAutoGenBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;

@WailaPlugin
public class DWJadePlugin implements IWailaPlugin {
    @Override
    public void register( IWailaCommonRegistration registration ) {
        //TODO Add support for displaying extra info about our block entities
    }
    
    @Override
    public void registerClient( IWailaClientRegistration registration ) {
        registration.addRayTraceCallback( ( hitResult, accessor, originalAccessor ) -> {
            if( accessor instanceof BlockAccessor blockAccessor && !accessor.getPlayer().isCreative() &&
                    blockAccessor.getBlock() instanceof IAutoGenBlock autoGenBlock ) {
                accessor.getServerData().putString( "givenName",
                        Component.Serializer.toJson( blockAccessor.getBlock().getName() ) );
                return registration.blockAccessor().from( blockAccessor )
                        .fakeBlock( new ItemStack( autoGenBlock.getOriginBlock() ) ).build();
            }
            return accessor;
        } );
    }
}