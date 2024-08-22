package fathertoast.deadlyworld.common.event;

import fathertoast.deadlyworld.common.entity.MiniArrowEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityEvents {

    // TODO - Get rid of this lazy stuff and actually do the override
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        // Too lazy to override the on hit method for the mini arrow entity,
        // setting damage to 1.0 here instead.
        if (event.getSource().getDirectEntity() instanceof MiniArrowEntity) {
            event.setAmount(1.0F);
        }
    }
}
