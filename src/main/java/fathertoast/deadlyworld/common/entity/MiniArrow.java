package fathertoast.deadlyworld.common.entity;

import com.google.common.collect.Sets;
import fathertoast.crust.api.lib.CrustMath;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Set;

/**
 * Modified copy-paste of {@link net.minecraft.world.entity.projectile.Arrow}.
 */
public class MiniArrow extends AbstractArrow {//TODO maybe this whole thing can just extend Arrow?
    
    private static final String TAG_COLOR = "Color";
    private static final int EXPOSED_POTION_DECAY_TIME = 600;
    private static final int NO_EFFECT_COLOR = -1;
    
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId( MiniArrow.class, EntityDataSerializers.INT );
    
    private Potion potion = Potions.EMPTY;
    private final Set<MobEffectInstance> effects = Sets.newHashSet();
    private boolean fixedColor;
    
    public MiniArrow( EntityType<? extends MiniArrow> entityType, Level level ) {
        super( entityType, level );
    }
    
    @SuppressWarnings( "unused" ) // For parity with Arrow
    public MiniArrow( Level level, double x, double y, double z ) {
        super( DWEntities.MINI_ARROW.get(), x, y, z, level );
    }
    
    @SuppressWarnings( "unused" ) // For parity with Arrow
    public MiniArrow( Level level, LivingEntity livingEntity ) {
        super( DWEntities.MINI_ARROW.get(), livingEntity, level );
    }
    
    public void setEffectsFromItem( ItemStack itemStack ) {
        if( itemStack.is( Items.TIPPED_ARROW ) ) {
            potion = PotionUtils.getPotion( itemStack );
            
            Collection<MobEffectInstance> customEffects = PotionUtils.getCustomEffects( itemStack );
            if( !customEffects.isEmpty() ) {
                for( MobEffectInstance instance : customEffects ) {
                    effects.add( new MobEffectInstance( instance ) );
                }
            }
            
            int color = getCustomColor( itemStack );
            if( color == NO_EFFECT_COLOR ) {
                updateColor();
            }
            else {
                setFixedColor( color );
            }
        }
        else if( itemStack.is( Items.ARROW ) ) {
            potion = Potions.EMPTY;
            effects.clear();
            entityData.set( ID_EFFECT_COLOR, NO_EFFECT_COLOR );
        }
    }
    
    public static int getCustomColor( ItemStack itemStack ) {
        CompoundTag tag = itemStack.getTag();
        return tag != null && NBTHelper.containsNumber( tag, PotionUtils.TAG_CUSTOM_POTION_COLOR ) ?
                tag.getInt( PotionUtils.TAG_CUSTOM_POTION_COLOR ) : NO_EFFECT_COLOR;
    }
    
    private void updateColor() {
        fixedColor = false;
        
        if( potion == Potions.EMPTY && effects.isEmpty() ) {
            entityData.set( ID_EFFECT_COLOR, NO_EFFECT_COLOR );
        }
        else {
            entityData.set( ID_EFFECT_COLOR, PotionUtils.getColor( PotionUtils.getAllEffects( potion, effects ) ) );
        }
    }
    
    public void addEffect( MobEffectInstance effectInstance ) {
        effects.add( effectInstance );
        getEntityData().set( ID_EFFECT_COLOR, PotionUtils.getColor( PotionUtils.getAllEffects( potion, effects ) ) );
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define( ID_EFFECT_COLOR, NO_EFFECT_COLOR );
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if( level().isClientSide ) {
            if( inGround ) {
                if( inGroundTime % 5 == 0 ) {
                    makeParticle( 1 );
                }
            }
            else {
                makeParticle( 2 );
            }
        }
        else if( inGround && inGroundTime != 0 && !effects.isEmpty() && inGroundTime >= EXPOSED_POTION_DECAY_TIME ) {
            level().broadcastEntityEvent( this, (byte) 0 );
            potion = Potions.EMPTY;
            effects.clear();
            entityData.set( ID_EFFECT_COLOR, NO_EFFECT_COLOR );
        }
        
    }
    
    private void makeParticle( int count ) {
        int color = getColor();
        if( color != NO_EFFECT_COLOR && count > 0 ) {
            double r = CrustMath.getRed( color );
            double g = CrustMath.getGreen( color );
            double b = CrustMath.getBlue( color );
            
            for( int i = 0; i < count; ++i ) {
                level().addParticle( ParticleTypes.ENTITY_EFFECT,
                        getRandomX( 0.5 ), getRandomY(), getRandomZ( 0.5 ),
                        r, g, b );
            }
        }
    }
    
    public int getColor() { return entityData.get( ID_EFFECT_COLOR ); }
    
    private void setFixedColor( int color ) {
        fixedColor = true;
        entityData.set( ID_EFFECT_COLOR, color );
    }
    
    @Override
    public void addAdditionalSaveData( CompoundTag saveTag ) {
        super.addAdditionalSaveData( saveTag );
        
        if( potion != Potions.EMPTY && potion != null ) {
            //noinspection deprecation
            saveTag.putString( PotionUtils.TAG_POTION, BuiltInRegistries.POTION.getKey( potion ).toString() );
        }
        if( fixedColor ) {
            saveTag.putInt( TAG_COLOR, getColor() );
        }
        if( !effects.isEmpty() ) {
            ListTag fxTag = new ListTag();
            for( MobEffectInstance instance : effects ) {
                fxTag.add( instance.save( new CompoundTag() ) );
            }
            saveTag.put( PotionUtils.TAG_CUSTOM_POTION_EFFECTS, fxTag );
        }
    }
    
    @Override
    public void readAdditionalSaveData( CompoundTag loadTag ) {
        super.readAdditionalSaveData( loadTag );
        
        if( NBTHelper.containsString( loadTag, PotionUtils.TAG_POTION ) ) {
            potion = PotionUtils.getPotion( loadTag );
        }
        for( MobEffectInstance instance : PotionUtils.getCustomEffects( loadTag ) ) {
            addEffect( instance );
        }
        if( NBTHelper.containsNumber( loadTag, TAG_COLOR ) ) {
            setFixedColor( loadTag.getInt( TAG_COLOR ) );
        }
        else {
            updateColor();
        }
    }
    
    @Override
    protected void doPostHurtEffects( LivingEntity target ) {
        super.doPostHurtEffects( target );
        
        Entity shooter = getEffectSource();
        for( MobEffectInstance instance : potion.getEffects() ) {
            target.addEffect( new MobEffectInstance( instance.getEffect(),
                    Math.max( instance.mapDuration( t -> t / 8 ), 1 ),
                    instance.getAmplifier(), instance.isAmbient(), instance.isVisible() ), shooter );
        }
        if( !effects.isEmpty() ) {
            for( MobEffectInstance instance : effects ) {
                target.addEffect( instance, shooter );
            }
        }
    }
    
    @Override
    protected ItemStack getPickupItem() {
        if( effects.isEmpty() && potion == Potions.EMPTY ) {
            return new ItemStack( Items.ARROW );
        }
        else {
            ItemStack tippedArrow = new ItemStack( Items.TIPPED_ARROW );
            PotionUtils.setPotion( tippedArrow, potion );
            PotionUtils.setCustomEffects( tippedArrow, effects );
            if( fixedColor ) {
                tippedArrow.getOrCreateTag().putInt( PotionUtils.TAG_CUSTOM_POTION_COLOR, getColor() );
            }
            return tippedArrow;
        }
    }
    
    //TODO Are these needed?
    //
    //    @Override
    //    public IPacket<?> getAddEntityPacket() {
    //        return NetworkHooks.getEntitySpawningPacket( this );
    //    }
    //
    //    @Override
    //    public void writeSpawnData( PacketBuffer buffer ) {
    //        buffer.writeInt( getOwner() == null ? getId() : getOwner().getId() );
    //    }
    //
    //    @Override
    //    public void readSpawnData( PacketBuffer additionalData ) {
    //        setOwner( level.getEntity( additionalData.readInt() ) );
    //    }
    
    @Override
    public void handleEntityEvent( byte event ) {
        if( event == 0 ) {
            int color = getColor();
            if( color != NO_EFFECT_COLOR ) {
                double r = CrustMath.getRed( color );
                double g = CrustMath.getGreen( color );
                double b = CrustMath.getBlue( color );
                
                for( int i = 0; i < 20; i++ ) {
                    level().addParticle( ParticleTypes.ENTITY_EFFECT,
                            getRandomX( 0.5 ), getRandomY(), getRandomZ( 0.5 ),
                            r, g, b );
                }
            }
        }
        else {
            super.handleEntityEvent( event );
        }
    }
}