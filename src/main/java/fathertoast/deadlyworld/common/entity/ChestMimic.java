package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.entity.ai.PeacefulHurtByTargetGoal;
import fathertoast.deadlyworld.common.entity.ai.PeacefulNearestAttackableTargetGoal;
import fathertoast.deadlyworld.common.util.ItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nonnull;

public class ChestMimic extends Monster {
    
    public static final EntityDataAccessor<BlockState> DISGUISE_BLOCK_STATE = SynchedEntityData.defineId( ChestMimic.class, EntityDataSerializers.BLOCK_STATE );
    
    private NonNullList<ItemStack> items = NonNullList.of( ItemStack.EMPTY );
    
    
    public ChestMimic( EntityType<? extends Monster> entityType, Level level ) {
        super( entityType, level );
        // Lol!
        setMaxUpStep( 1.0F );
    }
    
    public static AttributeSupplier.Builder createChestMimicAttributes() {
        return Monster.createMonsterAttributes()
                .add( Attributes.MAX_HEALTH, 40.0 )
                .add( Attributes.MOVEMENT_SPEED, 0.3 )
                .add( Attributes.ATTACK_DAMAGE, 4.0 );
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define( DISGUISE_BLOCK_STATE, Blocks.CHEST.defaultBlockState() );
    }
    
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 0, new FloatGoal( this ) );
        goalSelector.addGoal( 3, new MeleeAttackGoal( this, 1.0, true ) );
        goalSelector.addGoal( 4, new WaterAvoidingRandomStrollGoal( this, 0.8 ) );
        goalSelector.addGoal( 5, new LookAtPlayerGoal( this, Player.class, 8.0F ) );
        goalSelector.addGoal( 5, new RandomLookAroundGoal( this ) );
        
        targetSelector.addGoal( 1, new PeacefulHurtByTargetGoal( this ) );
        targetSelector.addGoal( 2, new PeacefulNearestAttackableTargetGoal<>( this, Player.class, true ) );
    }
    
    @Override
    public void addAdditionalSaveData( CompoundTag saveTag ) {
        super.addAdditionalSaveData( saveTag );
        
        if( items != null )
            ContainerHelper.saveAllItems( saveTag, items );
    }
    
    @Override
    public void readAdditionalSaveData( CompoundTag compoundTag ) {
        super.readAdditionalSaveData( compoundTag );
        
        items = ItemHelper.loadAllItems( compoundTag, items );
    }
    
    /** Drops any additional custom loot after the loot table has been dropped. */
    @Override
    protected void dropCustomDeathLoot( DamageSource damageSource, int lootingLevel, boolean recentlyHurtBy ) {
        super.dropCustomDeathLoot( damageSource, lootingLevel, recentlyHurtBy );
        
        // Try dropping the chest this mimic is "made of"
        ItemStack chest = null;
        try { chest = new ItemStack( entityData.get( DISGUISE_BLOCK_STATE ).getBlock().asItem() ); }
        catch( Exception ignored ) { }
        
        if( chest != null ) spawnAtLocation( chest );
        
        // Drop chest contents, if any
        if( items != null && !items.isEmpty() ) {
            for( ItemStack itemStack : items ) {
                // Do not drop mimic cores. They should be lost and instead let the loot table
                // have a chance of dropping one.
                if( !itemStack.isEmpty() && itemStack.getItem() != DWItems.MIMIC_CORE.get() )
                    spawnAtLocation( itemStack );
            }
        }
    }
    
    @SuppressWarnings( "deprecation" ) // New Forge method falls back to this one, no need to override both
    @Override
    public boolean canBreatheUnderwater() { return true; }
    
    /** Does not despawn in peaceful, but becomes completely passive. */
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }
    
    /** Does not despawn naturally. */
    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return DWSoundEvents.CHEST_MIMIC_DEATH.get();
    }
    
    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) {
        return DWSoundEvents.CHEST_MIMIC_HURT.get();
    }
    
    /** Gets the mimic's "camo" block state. Should normally be a chest of some sort */
    public BlockState getDisguiseState() {
        return entityData.get( DISGUISE_BLOCK_STATE );
    }
    
    /**
     * Sets the mimic's "camo" block state.<br>
     * Called from {@link fathertoast.deadlyworld.common.event.GameEventHandler#onRightClickChest(PlayerInteractEvent.RightClickBlock)}
     */
    public void setDisguiseState( @Nonnull BlockState blockState ) {
        entityData.set( DISGUISE_BLOCK_STATE, blockState );
    }
    
    public void setItems( NonNullList<ItemStack> inventory ) {
        this.items = NonNullList.of( ItemStack.EMPTY, inventory.toArray( new ItemStack[0] ) );
    }
    
    public NonNullList<ItemStack> getItems() {
        return items;
    }
}