package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import javax.annotation.Nullable;

/**
 * Similar to normal TNT, but has a slightly larger explosion radius, does not damage blocks or entities and applies
 * massive knockback to living entities.<br><br>
 * <p>
 * Knockback is changed with a mixin, {@link fathertoast.deadlyworld.common.mixin.ExplosionMixin}<br>
 * Damage to entities is canceled via event at
 * {@link fathertoast.deadlyworld.common.event.GameEventHandler#onLivingDamage(LivingDamageEvent)}
 */
public class YeetTnt extends PrimedTnt {
    
    public YeetTnt( EntityType<? extends PrimedTnt> type, Level level ) {
        super( type, level );
    }
    
    public YeetTnt( Level level, double x, double y, double z, @Nullable LivingEntity owner ) {
        this( DWEntities.YEET_TNT.get(), level );
        xo = x;
        yo = y;
        zo = z;
        this.owner = owner;
        
        setPos( x, y, z );
        
        double bumpInertia = level.random.nextDouble() * (double) ((float) Math.PI * 2.0F);
        
        setDeltaMovement( -Math.sin( bumpInertia ) * 0.02D, 0.2F, -Math.cos( bumpInertia ) * 0.02D );
        setFuse( 80 );
    }
    
    @Override
    protected void explode() {
        // noinspection resource
        level().explode( this, getX(), getY( 0.0625D ), getZ(), 5.0F, Level.ExplosionInteraction.NONE );
    }
}
