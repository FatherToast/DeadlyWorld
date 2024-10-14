package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MiniSkeleton extends Skeleton {
    
    public MiniSkeleton( EntityType<? extends Skeleton> entityType, Level level ) { super( entityType, level ); }
    
    public static AttributeSupplier.Builder createAttributes() {
        return DWEntities.standardMiniAttributes( Skeleton.createAttributes(), 0.25 )
                .add( Attributes.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE.getDefaultValue() / 2.0 );
    }
    
    /** Modified copy-paste of {@link ProjectileUtil#getMobArrow(LivingEntity, ItemStack, float)} */
    @Override
    protected MiniArrow getArrow( ItemStack ammo, float dist ) {
        MiniArrow miniArrow = new MiniArrow( level(), this );
        miniArrow.setBaseDamage( 0.05 );
        miniArrow.setEnchantmentEffectsFromEntity( this, dist );
        if( ammo.is( Items.TIPPED_ARROW ) ) {
            miniArrow.setEffectsFromItem( ammo );
        }
        return miniArrow;
    }
    
    @Override
    public int getMaxAirSupply() { return 100; }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntityDimensions entitySize ) { return 0.7F; }
}