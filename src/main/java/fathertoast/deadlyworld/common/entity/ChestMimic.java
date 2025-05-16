package fathertoast.deadlyworld.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ChestMimic extends Monster {

    public static final EntityDataAccessor<BlockState> DISGUISE_BLOCK_STATE = SynchedEntityData.defineId( ChestMimic.class, EntityDataSerializers.BLOCK_STATE );

    public ChestMimic(EntityType<? extends Monster> entityType, Level level ) {
        super( entityType, level );
        // Lol!
        setMaxUpStep( 1.0F );
    }
    
    public static AttributeSupplier.Builder createChestMimicAttributes() {
        return Monster.createMonsterAttributes()
                .add( Attributes.MOVEMENT_SPEED, 0.35 )
                .add( Attributes.MAX_HEALTH, 30.0 );
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
        
        targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
        targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
    }
    
    @SuppressWarnings( "deprecation" ) // New Forge method falls back to this one, no need to override both
    @Override
    public boolean canBreatheUnderwater() { return true; }

    public BlockState getDisguiseState() {
        return entityData.get( DISGUISE_BLOCK_STATE );
    }

    public void setDisguiseState( @Nonnull BlockState blockState ) {
        entityData.set( DISGUISE_BLOCK_STATE, blockState );
    }
}