package fathertoast.deadlyworld.common.tile.spawner;

import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.SpawnerConfig;
import fathertoast.deadlyworld.common.core.config.util.WeightedEntityList;
import fathertoast.deadlyworld.common.network.NetworkHelper;
import fathertoast.deadlyworld.common.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.OnClient;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class DeadlySpawnerTileEntity extends TileEntity implements ITickableTileEntity {
    
    private static final int EVENT_TIMER_RESET = 1;
    
    // Attribute tags
    private static final String TAG_DYNAMIC_SPAWN_LIST = "DynamicSpawnList";
    
    private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
    private static final String TAG_CHECK_SIGHT = "CheckSight";
    
    private static final String TAG_DELAY_MIN = "DelayMin";
    private static final String TAG_DELAY_MAX = "DelayMax";
    private static final String TAG_DELAY_PROGRESSION = "DelayProgression";
    private static final String TAG_DELAY_RECOVERY = "DelayRecovery";
    
    private static final String TAG_SPAWN_COUNT = "SpawnCount";
    private static final String TAG_SPAWN_RANGE = "SpawnRange";
    
    // Logic tags
    private static final String TAG_SPAWN_ENTITY = "SpawnEntity";
    private static final String TAG_DELAY = "Delay";
    private static final String TAG_DELAY_BUILDUP = "DelayBuildup";
    
    // Attributes
    private WeightedEntityList dynamicSpawnList;
    
    private float activationRange;
    private boolean checkSight;
    
    private int minSpawnDelay;
    private int maxSpawnDelay;
    private int spawnDelayProgression;
    private float spawnDelayRecovery;
    
    private int spawnCount;
    private float spawnRange;
    
    // Logic
    private WeightedSpawnerEntity entityToSpawn = new WeightedSpawnerEntity();

    
    /** Whether this spawner is active. Reduces the number of times we need to iterate over the player list. */
    private boolean activated;
    /** Countdown until the next activation check. */
    private int activationDelay;
    
    /** Countdown until the next spawn attempt. If this is set below 0, the countdown is reset without attempting to spawn. */
    private int spawnDelay = 10;
    /** The spawn delay previously set; the core of the progressive delay logic. */
    private double spawnDelayBuildup;
    
    // Client logic
    /** Cached instance of the entity to render inside the spawner. */
    private Entity cachedEntity;
    /** The rotation of the mob inside the mob spawner */
    private double mobRotation;
    /** the previous rotation of the mob inside the mob spawner */
    private double prevMobRotation;
    
    public DeadlySpawnerTileEntity() { super( DWTileEntities.DEADLY_SPAWNER.get() ); }
    
    public void initializeSpawner( SpawnerType spawnerType, DimensionConfigGroup dimConfigs ) {
        final Random random = level == null ? new Random() : level.random;
        final SpawnerConfig.SpawnerTypeCategory spawnerConfig = spawnerType.getFeatureConfig( dimConfigs );
        
        // Set attributes from the config
        if( random.nextFloat() < spawnerConfig.dynamicChance.get() ) {
            dynamicSpawnList = spawnerConfig.spawnList.get();
        }
        else {
            dynamicSpawnList = null;
        }
        
        activationRange = (float) spawnerConfig.activationRange.get();
        checkSight = spawnerConfig.checkSight.get();
        
        minSpawnDelay = spawnerConfig.delay.getMin();
        maxSpawnDelay = spawnerConfig.delay.getMax();
        spawnDelayProgression = spawnerConfig.delayProgression.get();
        spawnDelayRecovery = (float) spawnerConfig.delayRecovery.get();
        
        spawnCount = spawnerConfig.spawnCount.get();
        spawnRange = (float) spawnerConfig.spawnRange.get();
        
        // Initialize logic
        this.setEntityToSpawn(spawnerConfig.spawnList.get().next(random));
    }
    
    public void setEntityToSpawn(EntityType<? extends Entity> entityType) {
        this.entityToSpawn.getTag().putString("id", Objects.requireNonNull(entityType.getRegistryName()).toString());
        NetworkHelper.updateSpawnerRenderEntity(entityType, this.getBlockPos());
    }
    
    private SpawnerType getSpawnerType() {
        if( this.worldPosition != null && this.level != null ) {
            Block block = this.level.getBlockState( this.worldPosition ).getBlock( );
            
            if( block instanceof DeadlySpawnerBlock ) {
                return ((DeadlySpawnerBlock) block).getSpawnerType( );
            }
        }
        return SpawnerType.LONE;
    }
    
    @Override
    public void tick() {
        // Update activation status
        if( activationDelay > 0 ) {
            activationDelay--;
        }
        else {
            activationDelay = 4;
            activated = TrapHelper.isValidPlayerInRange(this.level, this.worldPosition, activationRange, false, false);
        }
        
        if(this.level.isClientSide) {
            // Run client-side effects
            if(activated) {
                final World world = level;
                final double xP = worldPosition.getX() + world.random.nextDouble();
                final double yP = worldPosition.getY() + world.random.nextDouble();
                final double zP = worldPosition.getZ() + world.random.nextDouble();
                world.addParticle( ParticleTypes.SMOKE, xP, yP, zP, 0.0, 0.0, 0.0 );
                world.addParticle( ParticleTypes.FLAME, xP, yP, zP, 0.0, 0.0, 0.0 );
                
                if( spawnDelay > 0 ) {
                    spawnDelay--;
                }
                prevMobRotation = mobRotation;
                mobRotation = (mobRotation + 1000.0F / (spawnDelay + 200.0F)) % 360.0;
            }
        }
        else {
            // Run server-side logic
            if(activated) {
                
                if( spawnDelay < 0 ) {
                    resetTimer( false );
                }
                
                if( spawnDelay > 0 ) {
                    // Spawner is on cooldown
                    spawnDelay--;
                }
                //TODO
                else if(checkSight && !TrapHelper.isValidPlayerInRange( level, worldPosition, activationRange, true, false )) {
                    // Failed sight check; impose a small delay, so we don't spam ray traces
                    spawnDelay = 6 + level.random.nextInt( 10 );
                }
                else {
                    // Attempt spawning
                    this.doSpawn();
                }
            }
            else if( spawnDelayBuildup > minSpawnDelay ) {
                // Decrement the progressive delay buildup
                spawnDelayBuildup = Math.max( spawnDelayBuildup - spawnDelayRecovery, minSpawnDelay );
            }
        }
    }
    
    private void doSpawn() {
        // Should only be called server side anyways
        if(level == null || level.isClientSide) return;

        ServerWorld world = (ServerWorld) this.level;
        BlockPos pos = this.worldPosition;

        final DifficultyInstance difficultyInstance = world.getCurrentDifficultyAt( pos );
        final DimensionConfigGroup dimConfig = Config.getDimensionConfigs( world );
        final SpawnerType spawnerType = this.getSpawnerType();

        boolean success = false;

        for( int i = 0; i < this.spawnCount; i++ ) {
            double xSpawn = pos.getX() + 0.5 + (world.random.nextDouble() - world.random.nextDouble()) * this.spawnRange;
            double ySpawn = pos.getY() + world.random.nextInt( 3 ) - 1;
            double zSpawn = pos.getZ() + 0.5 + (world.random.nextDouble() - world.random.nextDouble()) * this.spawnRange;

            // Try to create the entity to spawn
            final Entity entity;

            try {
                entity = EntityType.loadEntityRecursive(this.entityToSpawn.getTag(), world, (spawnedEntity) -> {
                    spawnedEntity.moveTo(xSpawn, ySpawn, zSpawn, spawnedEntity.yRot, spawnedEntity.xRot);
                    return spawnedEntity;
                });
            }
            catch(Exception ex) {
                DeadlyWorld.LOG.error("Encountered exception while constructing entity '{}'", this.entityToSpawn, ex);
                break;
            }

            // Do max nearby entities check
            int nearbyEntities = world.getEntitiesOfClass(
                    entity.getClass(),
                    new AxisAlignedBB(
                            pos.getX(), pos.getY(), pos.getZ(),
                            pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1
                    ).inflate(this.spawnRange)
            ).size();
            if(nearbyEntities >= this.spawnCount * 2) {
                break;
            }

            // Initialize the entity
            entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), world.random.nextFloat() * 360.0F, 0.0F);

            if(entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                if(!canSpawnNearLocation(livingEntity, xSpawn, ySpawn, zSpawn)) {
                    continue;
                }
                if (livingEntity instanceof MobEntity) {
                    ((MobEntity) entity).finalizeSpawn((ServerWorld) this.level, this.level.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.SPAWNER, null, null);
                }
                spawnerType.initEntity( livingEntity, dimConfig, world, pos );
            }
            world.addFreshEntity(entity);
            world.levelEvent( 2004, pos, 0 );
            success = true;

            if(entity instanceof MobEntity) {
                ((MobEntity) entity).spawnAnim();
            }
        }
        this.resetTimer(success);
    }
    
    private boolean canSpawnNearLocation(LivingEntity entity, final double x, final double y, final double z) {
        return entity.level.noCollision(entity) ||
                trySpawnOffsets(entity, x, y, z, Direction.UP, Direction.DOWN ) ||
                trySpawnOffsets(entity, x, y, z, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH) || trySpawnOffsets(entity, x, y + 1, z, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH);
    }
    
    private boolean trySpawnOffsets(LivingEntity entity, final double x, final double y, final double z, Direction... facings) {
        for( Direction direction : facings ) {
            entity.setPos(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
            if(entity.level.noCollision(entity)) {
                return true;
            }
        }
        return false;
    }
    
    private void resetTimer(boolean incrProgressiveDelay) {
        if(level == null)
            return;
        
        final World world = level;
        
        if( !world.isClientSide ) {
            if( maxSpawnDelay <= minSpawnDelay ) {
                // Spawn delay is a constant
                spawnDelay = minSpawnDelay;
            }
            else if( spawnDelayProgression <= 0 ) {
                // Progressive delay is disabled, use vanilla logic
                spawnDelay = minSpawnDelay + world.random.nextInt( maxSpawnDelay - minSpawnDelay );
            }
            else {
                // Reset timer based on progressive delay
                if( spawnDelayBuildup < minSpawnDelay ) {
                    spawnDelayBuildup = minSpawnDelay;
                }
                
                spawnDelay = (int) spawnDelayBuildup;
                
                if( incrProgressiveDelay ) {
                    spawnDelayBuildup = Math.min(
                            maxSpawnDelay,
                            spawnDelayBuildup + spawnDelayProgression * (1.0 + 0.2 * (world.random.nextDouble() - 0.5))
                    );
                }
            }
            
            if( dynamicSpawnList != null ) {
                setEntityToSpawn( dynamicSpawnList.next(world.random ));

                final BlockState block = world.getBlockState( worldPosition);
                world.sendBlockUpdated(worldPosition, block, block, 4);
            }
            world.blockEvent(worldPosition, this.getBlockState().getBlock(), EVENT_TIMER_RESET, 0);
        }
    }
    
    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        
        // Attributes
        compound.putString( TAG_DYNAMIC_SPAWN_LIST, dynamicSpawnList == null ? "" : dynamicSpawnList.toString());
        
        compound.putBoolean( TAG_CHECK_SIGHT, checkSight );
        
        compound.putInt( TAG_DELAY_MAX, maxSpawnDelay );
        compound.putInt( TAG_DELAY_PROGRESSION, spawnDelayProgression );
        compound.putFloat( TAG_DELAY_RECOVERY, spawnDelayRecovery );
        
        compound.putInt( TAG_SPAWN_COUNT, spawnCount );
        compound.putFloat( TAG_SPAWN_RANGE, spawnRange );
        
        // Logic
        compound.putDouble( TAG_DELAY_BUILDUP, spawnDelayBuildup );
        
        return writeNBTSentToClient( compound );
    }
    
    private CompoundNBT writeNBTSentToClient( CompoundNBT compound ) {
        // Attributes
        compound.putFloat( TAG_ACTIVATION_RANGE, activationRange );
        
        compound.putInt( TAG_DELAY_MIN, minSpawnDelay );

        compound.put( TAG_SPAWN_ENTITY, entityToSpawn.getTag() );
        compound.putInt( TAG_DELAY, spawnDelay );
        
        return compound;
    }
    
    @Override
    public void load( BlockState state, CompoundNBT tag ) {
        super.load( state, tag );
        
        // Attributes
        if( tag.contains( TAG_DYNAMIC_SPAWN_LIST, Constants.NBT.TAG_STRING ) ) {
            String line = tag.getString( TAG_DYNAMIC_SPAWN_LIST );

            if( line.isEmpty() ) {
                dynamicSpawnList = null;
            }
            else {
                //TODO
                dynamicSpawnList = null;//new WeightedRandomConfig( line );
            }
        }
        
        if( tag.contains( TAG_ACTIVATION_RANGE, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            activationRange = tag.getFloat( TAG_ACTIVATION_RANGE );
        }
        if( tag.contains( TAG_CHECK_SIGHT, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            checkSight = tag.getBoolean( TAG_CHECK_SIGHT );
        }
        
        if( tag.contains( TAG_DELAY_MIN, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            minSpawnDelay = tag.getInt( TAG_DELAY_MIN );
        }
        if( tag.contains( TAG_DELAY_MAX, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            maxSpawnDelay = tag.getInt( TAG_DELAY_MAX );
        }
        if( tag.contains( TAG_DELAY_PROGRESSION, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnDelayProgression = tag.getInt( TAG_DELAY_PROGRESSION );
        }
        if( tag.contains( TAG_DELAY_RECOVERY, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnDelayRecovery = tag.getFloat( TAG_DELAY_RECOVERY );
        }
        
        if( tag.contains( TAG_SPAWN_COUNT, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnCount = tag.getInt( TAG_SPAWN_COUNT );
        }
        if( tag.contains( TAG_SPAWN_RANGE, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnRange = tag.getFloat( TAG_SPAWN_RANGE );
        }
        
        // Logic
        this.setSpawnEntityFromTag(tag);

        if( tag.contains( TAG_DELAY, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnDelay = tag.getInt( TAG_DELAY );
        }
        if( tag.contains( TAG_DELAY_BUILDUP, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnDelayBuildup = tag.getDouble( TAG_DELAY_BUILDUP );
        }
    }
    
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 0, this.getUpdateTag());
    }
    
    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if(this.level.isClientSide) {
            super.handleUpdateTag(this.getBlockState(), pkt.getTag());

            CompoundNBT tag = pkt.getTag();
            this.setSpawnEntityFromTag(tag);
        }
    }

    private void setSpawnEntityFromTag(CompoundNBT tag) {
        if(tag.contains(TAG_SPAWN_ENTITY, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT spawnEntityTag = tag.getCompound(TAG_SPAWN_ENTITY);

            if (spawnEntityTag.contains("id", Constants.NBT.TAG_STRING)) {

                String line = spawnEntityTag.getString("id");

                if (line.isEmpty()) {
                    entityToSpawn = new WeightedSpawnerEntity();
                    cachedEntity = null;
                } else {
                    EntityType<?> entityType = EntityType.PIG;
                    ResourceLocation entityRegName = ResourceLocation.tryParse(line);

                    if (entityRegName != null && ForgeRegistries.ENTITIES.containsKey(entityRegName)) {
                        entityType = ForgeRegistries.ENTITIES.getValue(entityRegName);
                    }
                    setEntityToSpawn(entityType);
                }
            }
        }
    }

    @Override
    public boolean triggerEvent( int id, int type ) {
        if(this.level.isClientSide) {
            DeadlyWorld.LOG.warn( "Getting client event '{}:{}'", id, type );

            if( id == EVENT_TIMER_RESET ) {
                this.spawnDelay = this.minSpawnDelay;
                return true;
            }
        }
        return super.triggerEvent( id, type );
    }
    
    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @OnClient
    public void setRenderEntity(EntityType<?> entityType) {
        this.cachedEntity = entityType.create(this.level);
    }
    
    @OnClient
    public Entity getRenderEntity() {
        if (this.cachedEntity == null) {
            this.cachedEntity = EntityType.loadEntityRecursive(this.entityToSpawn.getTag(), this.level, Function.identity());
        }
        return this.cachedEntity;
    }
    
    @OnClient
    public float getRenderEntityRotation( float partialTicks ) {
        return (float) (prevMobRotation + (mobRotation - prevMobRotation) * partialTicks);
    }
}