package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Modified copy-paste of {@link ArrowEntity}
 */
public class MiniArrowEntity extends AbstractArrowEntity implements IEntityAdditionalSpawnData {
    
    private static final DataParameter<Integer> ID_EFFECT_COLOR = EntityDataManager.defineId( ArrowEntity.class, DataSerializers.INT );
    
    private Potion potion;
    private final Set<EffectInstance> effects;
    private boolean fixedColor;
    
    public MiniArrowEntity( EntityType<? extends MiniArrowEntity> entityType, World world ) {
        super( entityType, world );
        this.setBaseDamage( this.getBaseDamage() / 2.0 );
        this.potion = Potions.EMPTY;
        this.effects = new HashSet<>();
    }
    
    public MiniArrowEntity( World world, double x, double y, double z ) {
        super( DWEntities.MINI_ARROW.get(), x, y, z, world );
        this.setBaseDamage( this.getBaseDamage() / 2.0 );
        this.potion = Potions.EMPTY;
        this.effects = new HashSet<>();
    }
    
    public MiniArrowEntity( World world, LivingEntity livingEntity ) {
        super( DWEntities.MINI_ARROW.get(), livingEntity, world );
        this.setBaseDamage( this.getBaseDamage() / 2.0 );
        this.potion = Potions.EMPTY;
        this.effects = new HashSet<>();
    }
    
    public void setEffectsFromItem( ItemStack itemStack ) {
        if( itemStack.getItem() == Items.TIPPED_ARROW ) {
            this.potion = PotionUtils.getPotion( itemStack );
            Collection<EffectInstance> effects = PotionUtils.getCustomEffects( itemStack );
            
            if( !effects.isEmpty() ) {
                for( EffectInstance instance : effects ) {
                    this.effects.add( new EffectInstance( instance ) );
                }
            }
            int color = getCustomColor( itemStack );
            
            if( color == -1 ) {
                this.updateColor();
            }
            else {
                this.setFixedColor( color );
            }
        }
        else if( itemStack.getItem() == Items.ARROW ) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set( ID_EFFECT_COLOR, -1 );
        }
    }
    
    public static int getCustomColor( ItemStack itemStack ) {
        CompoundNBT compoundNBT = itemStack.getTag();
        
        return compoundNBT != null && compoundNBT.contains( "CustomPotionColor", 99 )
                ? compoundNBT.getInt( "CustomPotionColor" )
                : -1;
    }
    
    private void updateColor() {
        this.fixedColor = false;
        
        if( this.potion == Potions.EMPTY && this.effects.isEmpty() ) {
            this.entityData.set( ID_EFFECT_COLOR, -1 );
        }
        else {
            this.entityData.set( ID_EFFECT_COLOR, PotionUtils.getColor( PotionUtils.getAllEffects( this.potion, this.effects ) ) );
        }
    }
    
    public void addEffect( EffectInstance effectInstance ) {
        this.effects.add( effectInstance );
        this.getEntityData().set( ID_EFFECT_COLOR, PotionUtils.getColor( PotionUtils.getAllEffects( this.potion, this.effects ) ) );
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define( ID_EFFECT_COLOR, -1 );
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if( this.level.isClientSide ) {
            if( this.inGround ) {
                if( this.inGroundTime % 5 == 0 ) {
                    this.makeParticle( 1 );
                }
            }
            else {
                this.makeParticle( 2 );
            }
        }
        else if( this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600 ) {
            this.level.broadcastEntityEvent( this, (byte) 0 );
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set( ID_EFFECT_COLOR, -1 );
        }
        
    }
    
    private void makeParticle( int count ) {
        int color = this.getColor();
        
        if( color != -1 && count > 0 ) {
            double r = (double) (color >> 16 & 255) / 255.0D;
            double g = (double) (color >> 8 & 255) / 255.0D;
            double b = (double) (color & 255) / 255.0D;
            
            for( int i = 0; i < count; ++i ) {
                this.level.addParticle( ParticleTypes.ENTITY_EFFECT, this.getRandomX( 0.5D ), this.getRandomY(), this.getRandomZ( 0.5D ), r, g, b );
            }
        }
    }
    
    public int getColor() {
        return this.entityData.get( ID_EFFECT_COLOR );
    }
    
    private void setFixedColor( int color ) {
        this.fixedColor = true;
        this.entityData.set( ID_EFFECT_COLOR, color );
    }
    
    @Override
    public void addAdditionalSaveData( CompoundNBT compoundNBT ) {
        super.addAdditionalSaveData( compoundNBT );
        if( this.potion != Potions.EMPTY && this.potion != null ) {
            compoundNBT.putString( "Potion", Registry.POTION.getKey( this.potion ).toString() );
        }
        
        if( this.fixedColor ) {
            compoundNBT.putInt( "Color", this.getColor() );
        }
        
        if( !this.effects.isEmpty() ) {
            ListNBT listNBT = new ListNBT();
            
            for( EffectInstance instance : this.effects ) {
                listNBT.add( instance.save( new CompoundNBT() ) );
            }
            compoundNBT.put( "CustomPotionEffects", listNBT );
        }
    }
    
    @Override
    public void readAdditionalSaveData( CompoundNBT compoundNBT ) {
        super.readAdditionalSaveData( compoundNBT );
        if( compoundNBT.contains( "Potion", 8 ) ) {
            this.potion = PotionUtils.getPotion( compoundNBT );
        }
        
        for( EffectInstance instance : PotionUtils.getCustomEffects( compoundNBT ) ) {
            this.addEffect( instance );
        }
        
        if( compoundNBT.contains( "Color", 99 ) ) {
            this.setFixedColor( compoundNBT.getInt( "Color" ) );
        }
        else {
            this.updateColor();
        }
    }
    
    @Override
    protected void doPostHurtEffects( LivingEntity livingEntity ) {
        super.doPostHurtEffects( livingEntity );
        List<EffectInstance> effectInstances = this.potion.getEffects();
        
        for( EffectInstance instance : effectInstances ) {
            livingEntity.addEffect( new EffectInstance( instance.getEffect(), Math.max( instance.getDuration() / 8, 1 ), instance.getAmplifier(), instance.isAmbient(), instance.isVisible() ) );
        }
        
        if( !this.effects.isEmpty() ) {
            for( EffectInstance instance : this.effects ) {
                livingEntity.addEffect( instance );
            }
        }
    }
    
    @Override
    protected ItemStack getPickupItem() {
        if( this.effects.isEmpty() && this.potion == Potions.EMPTY ) {
            return new ItemStack( Items.ARROW );
        }
        else {
            ItemStack itemStack = new ItemStack( Items.TIPPED_ARROW );
            PotionUtils.setPotion( itemStack, this.potion );
            PotionUtils.setCustomEffects( itemStack, this.effects );
            
            if( this.fixedColor ) {
                itemStack.getOrCreateTag().putInt( "CustomPotionColor", this.getColor() );
            }
            return itemStack;
        }
    }
    
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket( this );
    }
    
    @Override
    public void writeSpawnData( PacketBuffer buffer ) {
        buffer.writeInt( this.getOwner() == null ? this.getId() : this.getOwner().getId() );
    }
    
    @Override
    public void readSpawnData( PacketBuffer additionalData ) {
        this.setOwner( this.level.getEntity( additionalData.readInt() ) );
    }
    
    @OnlyIn( Dist.CLIENT )
    @Override
    public void handleEntityEvent( byte event ) {
        if( event == 0 ) {
            int color = this.getColor();
            
            if( color != -1 ) {
                double r = (double) (color >> 16 & 255) / 255.0D;
                double g = (double) (color >> 8 & 255) / 255.0D;
                double b = (double) (color & 255) / 255.0D;
                
                for( int i = 0; i < 20; ++i ) {
                    this.level.addParticle( ParticleTypes.ENTITY_EFFECT, this.getRandomX( 0.5D ), this.getRandomY(), this.getRandomZ( 0.5D ), r, g, b );
                }
            }
        }
        else {
            super.handleEntityEvent( event );
        }
    }
}