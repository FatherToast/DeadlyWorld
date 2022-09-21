package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class MiniSkeletonEntity extends SkeletonEntity {
    
    public MiniSkeletonEntity( EntityType<? extends SkeletonEntity> entityType, World world ) { super( entityType, world ); }
    
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return DWEntities.standardMiniAttributes( SkeletonEntity.createAttributes(), 0.25 )
                .add( Attributes.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE.getDefaultValue() / 2.0 );
    }
    
    @Override
    public void performRangedAttack( LivingEntity target, float distance ) {
        ItemStack itemstack = getProjectile( this.getItemInHand( ProjectileHelper.getWeaponHoldingHand( this, item -> item instanceof BowItem ) ) );
        MiniArrowEntity miniArrowEntity = getArrow( itemstack, distance );
        
        double d0 = target.getX() - getX();
        double d1 = target.getY( 0.3333333333333333D ) - miniArrowEntity.getY();
        double d2 = target.getZ() - getZ();
        double d3 = MathHelper.sqrt( d0 * d0 + d2 * d2 );
        
        miniArrowEntity.shoot( d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - level.getDifficulty().getId() * 4) );
        playSound( SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F) );
        level.addFreshEntity( miniArrowEntity );
    }
    
    /** Modified copy-paste of {@link ProjectileHelper#getMobArrow(LivingEntity, ItemStack, float)} */
    @Override
    protected MiniArrowEntity getArrow( ItemStack itemStack, float dist ) {
        MiniArrowEntity miniArrowEntity = new MiniArrowEntity( level, this );
        miniArrowEntity.setEnchantmentEffectsFromEntity( this, dist );
        
        if( itemStack.getItem() == Items.TIPPED_ARROW ) {
            miniArrowEntity.setEffectsFromItem( itemStack );
        }
        return miniArrowEntity;
    }

    @Override
    public int getMaxAirSupply() { return 100; }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntitySize entitySize ) { return 0.7F; }
}