package fathertoast.deadlyworld.common.fishpranks;

import fathertoast.deadlyworld.api.registry.fishprank.IFishingPrank;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Calendar;

//....................../´¯/)
//....................,/¯../
//.................../..../
//............./´¯/'...'/´¯¯`·¸
//........../'/.../..../......./¨¯\
//........('(...´...´.... ¯~/'...')
//.........\.................'...../
//..........''...\.......... _.·´
//............\..............(
//..............\.............\...
public class FakYouPrank implements IFishingPrank {
    
    @Override
    public void prank( ServerLevel level, ServerPlayer player, Vec3 hookPos, Vec3 moveVec ) {
        if( Calendar.getInstance().get( Calendar.MONTH ) == Calendar.APRIL && Calendar.getInstance().get( Calendar.DAY_OF_MONTH ) == 1 )
            player.disconnect();
    }
}
