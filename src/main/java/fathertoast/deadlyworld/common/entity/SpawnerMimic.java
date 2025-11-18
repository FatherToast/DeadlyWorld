package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.network.NetworkHelper;
import fathertoast.deadlyworld.common.world.logic.ISpawnerObject;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class SpawnerMimic extends PathfinderMob implements Enemy, ISpawnerObject, IEntityAdditionalSpawnData {
    /**
     * Modified copy-paste of the spawner portion of {@link SpawnEggItem#useOn(UseOnContext)}.
     */
    public static boolean spawnEggUseOn( PlayerInteractEvent.EntityInteract event, ServerLevel level ) {
        ItemStack heldItem = event.getItemStack();
        
        if( heldItem.getItem() instanceof SpawnEggItem egg && event.getTarget() instanceof SpawnerMimic spawnerMimic ) {
            Player player = event.getEntity();
            
            EntityType<?> spawnEntity = egg.getType( heldItem.getTag() );
            spawnerMimic.spawner.setEntityId( spawnEntity, level, level.getRandom(), event.getPos() );
            spawnerMimic.spawner.addSpawn(); // Let it spawn an extra mob, why not
            spawnerMimic.sendUpdate( spawnerMimic.spawner, level, event.getPos() );
            
            if( !player.getAbilities().instabuild ) { // idk why the vanilla method doesn't need this, but we do
                heldItem.shrink( 1 );
            }
            return true;
        }
        return false;
    }
    
    public static final String TAG_SPAWNER_LOGIC = "SpawnerLogic";
    public static final String TAG_SPAWNER_TYPE = "SpawnerType";
    
    private ProgressiveDelaySpawner spawner;
    
    
    public SpawnerMimic( EntityType<? extends PathfinderMob> entityType, Level level ) {
        super( entityType, level );
        // Lol!
        setMaxUpStep( 1.0F );
        setSpawner( new ProgressiveDelaySpawner( SpawnerType.SIMPLE, this ) );
    }
    
    public static AttributeSupplier.Builder createSpawnerMimicAttributes() {
        return Monster.createMonsterAttributes()
                .add( Attributes.MAX_HEALTH, 20.0 )
                .add( Attributes.ARMOR, 10.0 )
                .add( Attributes.MOVEMENT_SPEED, 0.25 )
                .add( Attributes.ATTACK_DAMAGE, 4.0 );
    }
    
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 0, new AvoidEntityGoal<>( this, Player.class, 10.0F, 1.0F, 1.4F ) );
        goalSelector.addGoal( 1, new WaterAvoidingRandomStrollGoal( this, 0.8D ) );
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if( spawner != null ) {
            // noinspection resource
            if( level().isClientSide ) {
                spawner.clientTick( level(), blockPosition() );
                
                if( spawner.isDisabled() )
                    ohMyGoshWhatDoIDoWHATDOIDO( level(), blockPosition() );
            }
            else {
                spawner.serverTick( (ServerLevel) level(), blockPosition() );
            }
        }
    }
    
    @Override
    public void addAdditionalSaveData( CompoundTag compoundTag ) {
        super.addAdditionalSaveData( compoundTag );
        
        if( spawner != null ) {
            compoundTag.put( TAG_SPAWNER_LOGIC, spawner.save( new CompoundTag() ) );
            compoundTag.putString( TAG_SPAWNER_TYPE, spawner.getSpawnerType().toString() );
        }
    }
    
    @Override
    public void readAdditionalSaveData( CompoundTag compoundTag ) {
        super.readAdditionalSaveData( compoundTag );
        
        if( compoundTag.contains( TAG_SPAWNER_TYPE, Tag.TAG_STRING ) && compoundTag.contains( TAG_SPAWNER_LOGIC, Tag.TAG_COMPOUND ) ) {
            SpawnerType spawnerType = SpawnerType.getFromID( compoundTag.getString( TAG_SPAWNER_TYPE ) );
            spawner = new ProgressiveDelaySpawner( spawnerType, this );
            spawner.load( level(), blockPosition(), compoundTag.getCompound( TAG_SPAWNER_LOGIC ) );
        }
    }
    
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        spawner.save( tag );
        tag.remove( ProgressiveDelaySpawner.TAG_DYNAMIC_SPAWN_LIST );
        tag.remove( ProgressiveDelaySpawner.TAG_SPAWN_POTENTIALS );
        tag.remove( ProgressiveDelaySpawner.TAG_IS_MIMIC );
        return tag;
    }
    
    /**
     * Overridden to return the loot table of the spawner block of the spawner type
     * this mimic's spawner logic uses.
     */
    @Override
    protected ResourceLocation getDefaultLootTable() {
        if( spawner != null ) {
            return DWBlocks.spawner( spawner.getSpawnerType() ).get().getLootTable();
        }
        return super.getDefaultLootTable();
    }
    
    @SuppressWarnings( "deprecation" ) // New Forge method falls back to this one, no need to override both
    @Override
    public boolean canBreatheUnderwater() { return true; }
    
    /** Does not despawn in peaceful, but becomes completely passive. */
    @Override
    protected boolean shouldDespawnInPeaceful() { return false; }
    
    /** Does not despawn naturally. */
    @Override
    public boolean requiresCustomPersistence() { return true; }
    
    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) { return DWSoundEvents.SPAWNER_MIMIC_HURT.get(); }
    
    @Override
    @SuppressWarnings( "ConstantConditions" )
    protected SoundEvent getAmbientSound() { return null; }
    
    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        playSound( DWSoundEvents.SPAWNER_MIMIC_STEP.get(), 0.15F, 1.0F );
    }
    
    @Override
    protected SoundEvent getDeathSound() { return DWSoundEvents.SPAWNER_MIMIC_DEATH.get(); }
    
    /** Sets the spawner logic for this mimic. */
    public void setSpawner( ProgressiveDelaySpawner spawnerLogic ) { this.spawner = spawnerLogic; }
    
    /**
     * @return The current spawner logic for this mimic.
     * Generally SHOULDN'T be null, but might be.
     */
    @Nullable
    public ProgressiveDelaySpawner getSpawner() { return spawner; }
    
    @Override // ISpawnerObject
    public void sendUpdate( ProgressiveDelaySpawner spawner, Level level, BlockPos pos ) {
        if( level instanceof ServerLevel serverLevel )
            NetworkHelper.updateSpawnerMimic( serverLevel, this );
    }
    
    @Override // ISpawnerObject
    public void spawnEffectParticle( ProgressiveDelaySpawner spawner, Level level, BlockPos pos ) {
        RandomSource random = level.getRandom();
        double x = (double) pos.getX() + random.nextDouble();
        double y = (pos.getY() + 0.5D) + random.nextDouble();
        double z = (double) pos.getZ() + random.nextDouble();
        level.addParticle( ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0 );
        level.addParticle( ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0 );
    }
    
    /**
     * Spawns "panic" particles when the mimic's
     * spawner logic is disabled/has run out of spawns.
     */
    protected void ohMyGoshWhatDoIDoWHATDOIDO( Level level, BlockPos pos ) {
        if( (level.getGameTime() & 0b11) != 0 ) return; // Only spawn every 4th tick
        
        RandomSource random = level.getRandom();
        double x = getX() + random.nextGaussian() / 2;
        double y = (double) pos.getY() + getBoundingBox().getYsize() + 0.2;
        double z = getZ() + random.nextGaussian() / 2;
        level.addParticle( ParticleTypes.RAIN, x, y, z, 0.0, 0.0, 0.0 );
    }
    
    /** Overridden to make use of additional spawn data. */
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket( this ); }
    
    /**
     * Called on server to write additional
     * data that should be synced to the client
     * when this entity is spawned in the world.
     */
    @Override
    public void writeSpawnData( FriendlyByteBuf buffer ) {
        if( spawner != null ) {
            buffer.writeUtf( spawner.getSpawnerType().toString() );
            buffer.writeNbt( spawner.save( new CompoundTag() ) );
        }
    }
    
    /** Called on client when a spawn packet is received from the server. */
    @Override
    public void readSpawnData( FriendlyByteBuf additionalData ) {
        SpawnerType spawnerType = SpawnerType.getFromID( additionalData.readUtf() );
        CompoundTag spawnerTag = additionalData.readNbt();
        
        ProgressiveDelaySpawner spawner = new ProgressiveDelaySpawner( spawnerType, this );
        spawner.load( level(), blockPosition(), spawnerTag );
        setSpawner( spawner );
    }
}