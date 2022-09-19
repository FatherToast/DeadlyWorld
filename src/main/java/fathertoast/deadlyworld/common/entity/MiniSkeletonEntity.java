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

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MiniSkeletonEntity extends SkeletonEntity {
    
    public MiniSkeletonEntity( EntityType<? extends SkeletonEntity> entityType, World world ) { super( entityType, world ); }
    
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return DWEntities.standardMiniAttributes( SkeletonEntity.createAttributes(), 0.25 )
                .add( Attributes.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE.getDefaultValue() / 2.0 );
    }
    
    @Override
    public void performRangedAttack( LivingEntity p_82196_1_, float p_82196_2_ ) {
        ItemStack itemstack = this.getProjectile( this.getItemInHand( ProjectileHelper.getWeaponHoldingHand( this, item -> item instanceof BowItem ) ) );
        MiniArrowEntity miniArrowEntity = this.getArrow( itemstack, p_82196_2_ );
        
        double d0 = p_82196_1_.getX() - this.getX();
        double d1 = p_82196_1_.getY( 0.3333333333333333D ) - miniArrowEntity.getY();
        double d2 = p_82196_1_.getZ() - this.getZ();
        double d3 = MathHelper.sqrt( d0 * d0 + d2 * d2 );
        
        miniArrowEntity.shoot( d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4) );
        this.playSound( SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F) );
        this.level.addFreshEntity( miniArrowEntity );
    }
    
    /** Modified copy-paste of {@link ProjectileHelper#getMobArrow(LivingEntity, ItemStack, float)} */
    @Override
    protected MiniArrowEntity getArrow( ItemStack itemStack, float dist ) {
        MiniArrowEntity miniArrowEntity = new MiniArrowEntity( this.level, this );
        miniArrowEntity.setEnchantmentEffectsFromEntity( this, dist );
        
        if( itemStack.getItem() == Items.TIPPED_ARROW ) {
            miniArrowEntity.setEffectsFromItem( itemStack );
        }
        return miniArrowEntity;
    }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntitySize entitySize ) { return 0.7F; }
}