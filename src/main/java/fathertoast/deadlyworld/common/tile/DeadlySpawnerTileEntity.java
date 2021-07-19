package fathertoast.deadlyworld.common.tile;

import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.properties.SpawnerType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.util.EntityList;
import fathertoast.deadlyworld.common.registry.DWBlocks;
import fathertoast.deadlyworld.common.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.OnClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

public class DeadlySpawnerTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int EVENT_TIMER_RESET = 1;

    // Attribute tags
    private static final String TAG_DYNAMIC_SPAWN_LIST = "DynamicSpawnList";

    private static final String TAG_ACTIVATION_RANGE = "ActivationRange";
    private static final String TAG_CHECK_SIGHT      = "CheckSight";

    private static final String TAG_DELAY_MIN         = "DelayMin";
    private static final String TAG_DELAY_MAX         = "DelayMax";
    private static final String TAG_DELAY_PROGRESSIVE = "DelayProgressive";

    private static final String TAG_SPAWN_COUNT = "SpawnCount";
    private static final String TAG_SPAWN_RANGE = "SpawnRange";

    // Logic tags
    private static final String TAG_SPAWN_ENTITY  = "SpawnEntity";
    private static final String TAG_DELAY         = "Delay";
    private static final String TAG_DELAY_BUILDUP = "DelayBuildup";

    // Attributes
    private WeightedRandomConfig dynamicSpawnList;

    private float   activationRange;
    private boolean checkSight;

    private int minSpawnDelay;
    private int maxSpawnDelay;
    private int progressiveSpawnDelay;

    private int   spawnCount;
    private float spawnRange;

    // Logic
    private Class< ? extends Entity > entityToSpawn = PigEntity.class;

    /** Whether or not this spawner is active. Reduces the number of times we need to iterate over the player list. */
    private boolean activated;
    /** Countdown until the next activation check. */
    private int     activationDelay;

    /** Countdown until the next spawn attempt. If this is set below 0, the countdown is reset without attempting to spawn. */
    private int    spawnDelay = 10;
    /** The spawn delay previously set; the core of the progressive delay logic. */
    private double spawnDelayBuildup;

    // Client logic
    /** Cached instance of the entity to render inside the spawner. */
    private Entity cachedEntity;
    /** The rotation of the mob inside the mob spawner */
    private double mobRotation;
    /** the previous rotation of the mob inside the mob spawner */
    private double prevMobRotation;


    public DeadlySpawnerTileEntity() {
        super(DWTileEntities.DEADLY_SPAWNER.get());
    }


    public void initializeSpawner( SpawnerType spawnerType, Config dimConfig ) {
        Config.SpawnerFeatures spawnerConfig = spawnerType.getFeatureConfig( dimConfig );

        // Set attributes from the config
        if( this.level.random.nextFloat( ) < spawnerConfig.DYNAMIC_CHANCE ) {
            dynamicSpawnList = spawnerConfig.SPAWN_LIST;
        }
        else {
            dynamicSpawnList = null;
        }

        activationRange = spawnerConfig.ACTIVATION_RANGE;
        checkSight = spawnerConfig.CHECK_SIGHT;

        minSpawnDelay = spawnerConfig.DELAY_MIN;
        maxSpawnDelay = spawnerConfig.DELAY_MAX;
        progressiveSpawnDelay = spawnerConfig.DELAY_PROGRESSIVE;

        spawnCount = spawnerConfig.SPAWN_COUNT;
        spawnRange = spawnerConfig.SPAWN_RANGE;

        // Initialize logic
        setEntityToSpawn( spawnerConfig.SPAWN_LIST.nextItem( world.rand ) );

    }

    private void setEntityToSpawn( ResourceLocation registryName ) {
        entityToSpawn = EntityList.getClass( registryName );
        if( entityToSpawn == null ) {
            DeadlyWorld.LOG.warn(
                    "Spawner received non-registered entity name '{}'" +
                            " - This is probably caused by an error or change in the config for dimension \"{}\" (expect to see pig spawners)",
                    registryName, this.level.dimension( ).getRegistryName().toString()
            );
            entityToSpawn = PigEntity.class;
        }
        cachedEntity = null;
    }

    private SpawnerType getSpawnerType( ) {
        if( this.worldPosition != null && this.level != null ) {
            BlockState block = this.level.getBlockState( this.worldPosition );

            if( block.getBlock( ) == DWBlocks.DEADLY_SPAWNER_BLOCK.get( ) ) {
                return block.getValue( DeadlySpawnerBlock.SPAWNER_TYPE );
            }
        }
        return SpawnerType.LONE;
    }

    @Override
    public void tick( ) {

        // Update activation status
        if( this.activationDelay > 0 ) {
            this.activationDelay--;
        }
        else {
            this.activationDelay = 4;
            this.activated = TrapHelper.isValidPlayerInRange( this.level, this.worldPosition, activationRange, false, false );
        }

        if( !(this.level instanceof ServerWorld) ) {
            // Run client-side effects
            World world = this.level;

            if (this.activated) {
                double d3 = this.worldPosition.getX() + world.random.nextDouble();
                double d4 = this.worldPosition.getY() + world.random.nextDouble();
                double d5 = this.worldPosition.getZ() + world.random.nextDouble();
                world.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

                if (this.spawnDelay > 0) {
                    this.spawnDelay--;
                }
                this.prevMobRotation = mobRotation;
                this.mobRotation = (mobRotation + 1000.0F / (spawnDelay + 200.0F)) % 360.0;
            }
        }
        else {
            // Run server-side logic
            if( activated ) {

                if( spawnDelay < 0 ) {
                    resetTimer( false );
                }

                if( spawnDelay > 0 ) {
                    // Spawner is on cooldown
                    spawnDelay--;
                }
                else if( checkSight && !TrapHelper.isValidPlayerInRange( this.level, this.worldPosition, this.activationRange, true, false ) ) {
                    // Failed sight check; impose a small delay so we don't spam ray traces
                    spawnDelay = 6 + this.level.random.nextInt( 10 );
                }
                else {
                    // Attempt spawning
                    this.doSpawn( );
                }
            }
            else if( spawnDelayBuildup > minSpawnDelay ) {
                // Decrement the progressive delay buildup
                spawnDelayBuildup -= progressiveSpawnDelay * Config.get( ).GENERAL.PROGRESSIVE_RECOVERY;
            }
        }
    }


    private void doSpawn( ) {
        // Should only be called server side anyways
        if (this.level.isClientSide)
            return;

        World world = this.level;
        BlockPos pos = this.worldPosition;

        final Config             dimConfig          = Config.getOrDefault( world );
        final SpawnerType        spawnerType        = this.getSpawnerType( );
        final DifficultyInstance difficultyInstance = world.getCurrentDifficultyAt( pos );

        boolean success = false;

        for( int i = 0; i < this.spawnCount; i++ ) {
            double xSpawn = pos.getX( ) + 0.5 + (world.random.nextDouble( ) - world.random.nextDouble( )) * this.spawnRange;
            double ySpawn = pos.getY( ) + world.random.nextInt( 3 ) - 1;
            double zSpawn = pos.getZ( ) + 0.5 + (world.random.nextDouble( ) - world.random.nextDouble( )) * this.spawnRange;

            // Try to create the entity to spawn
            final Entity entity;
            try {
                // TODO - Consider a different way to do this. It do indeed work, but reflection is very disgusting and awfully slow
                entity = this.entityToSpawn.getConstructor( World.class ).newInstance( world );
            }
            catch( Exception ex ) {
                DeadlyWorld.LOG.error( "Encountered exception while constructing entity '{}'", this.entityToSpawn, ex );
                break;
            }

            // Do max nearby entities check
            int nearbyEntities = world.getEntitiesOfClass(
                    entity.getClass( ),
                    new AxisAlignedBB(
                            pos.getX( ), pos.getY( ), pos.getZ( ),
                            pos.getX( ) + 1, pos.getY( ) + 1, pos.getZ( ) + 1
                    ).inflate( this.spawnRange )
            ).size( );
            if( nearbyEntities >= this.spawnCount * 2 ) {
                break;
            }

            // Initialize the entity
            entity.setPos( xSpawn, ySpawn, zSpawn );
            entity.setRot(world.random.nextFloat( ) * 360.0F, 0.0F);

            if( entity instanceof LivingEntity ) {
                LivingEntity livingEntity = (LivingEntity) entity;

                if( !canSpawnNearLocation( livingEntity, xSpawn, ySpawn, zSpawn ) ) {
                    continue;
                }
                if ( livingEntity instanceof MobEntity ) {
                    ((MobEntity) entity).finalizeSpawn((ServerWorld) this.level, this.level.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.SPAWNER, null, null);
                }
                spawnerType.initEntity( livingEntity, dimConfig, world, pos );
            }
            world.addFreshEntity( entity );
            world.levelEvent( 2004, pos, 0 );
            success = true;

            if( entity instanceof LivingEntity ) {
                //((LivingEntity) entity).spawnExplosionParticle( );
            }
        }
        this.resetTimer( success );
    }

    private boolean canSpawnNearLocation( LivingEntity entity, final double x, final double y, final double z ) {
        return entity.level.noCollision( entity ) ||
                trySpawnOffsets( entity, x, y, z, Direction.UP, Direction.DOWN ) ||
                trySpawnOffsets( entity, x, y, z, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH) || trySpawnOffsets( entity, x, y + 1, z, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH );
    }

    private boolean trySpawnOffsets( LivingEntity entity, final double x, final double y, final double z, Direction... facings ) {
        for( Direction direction : facings ) {
            entity.setPos( x + direction.getStepX( ), y + direction.getStepY( ), z + direction.getStepZ( ) );
            if( entity.level.noCollision( entity ) ) {
                return true;
            }
        }
        return false;
    }

    private void resetTimer( boolean incrProgressiveDelay ) {
        if ( this.level == null )
            return;

        World world = this.level;

        if( !world.isClientSide ) {
            if( maxSpawnDelay <= minSpawnDelay ) {
                // Spawn delay is a constant
                spawnDelay = minSpawnDelay;
            }
            else if( progressiveSpawnDelay <= 0 ) {
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
                            spawnDelayBuildup + progressiveSpawnDelay * (1.0 + 0.1 * (world.random.nextDouble( ) - 0.5))
                    );
                }
            }

            if( dynamicSpawnList != null ) {
                setEntityToSpawn( dynamicSpawnList.nextItem( world.random ) );

                BlockState block = world.getBlockState( worldPosition );
                world.sendBlockUpdated( worldPosition, block, block, 4 );
            }
            world.setBlock( this.worldPosition, DWBlocks.DEADLY_SPAWNER_BLOCK.get().defaultBlockState(), EVENT_TIMER_RESET, 0 );
        }
    }

    @Override
    public CompoundNBT save( CompoundNBT compound ) {
        super.save( compound );

        // Attributes
        compound.putString( TAG_DYNAMIC_SPAWN_LIST, dynamicSpawnList == null ? "" : dynamicSpawnList.toString( ) );

        compound.putBoolean( TAG_CHECK_SIGHT, checkSight );

        compound.putInt( TAG_DELAY_MAX, maxSpawnDelay );
        compound.putInt( TAG_DELAY_PROGRESSIVE, progressiveSpawnDelay );

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

        // Logic
        compound.putString( TAG_SPAWN_ENTITY, entityToSpawn == PigEntity.class ? "" : EntityList.getKey( entityToSpawn ).toString( ) );
        compound.putInt( TAG_DELAY, spawnDelay );

        return compound;
    }

    @Override
    public void load( BlockState state, CompoundNBT tag ) {
        super.load( state, tag );

        // Attributes
        if( tag.contains( TAG_DYNAMIC_SPAWN_LIST, Constants.NBT.TAG_STRING ) ) {
            String line = tag.getString( TAG_DYNAMIC_SPAWN_LIST );

            if( line.isEmpty( ) ) {
                dynamicSpawnList = null;
            }
            else {
                dynamicSpawnList = new WeightedRandomConfig( line );
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
        if( tag.contains( TAG_DELAY_PROGRESSIVE, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            progressiveSpawnDelay = tag.getInt( TAG_DELAY_PROGRESSIVE );
        }

        if( tag.contains( TAG_SPAWN_COUNT, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnCount = tag.getInt( TAG_SPAWN_COUNT );
        }
        if( tag.contains( TAG_SPAWN_RANGE, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnRange = tag.getFloat( TAG_SPAWN_RANGE );
        }

        // Logic
        if( tag.contains( TAG_SPAWN_ENTITY, Constants.NBT.TAG_STRING ) ) {
            String line = tag.getString( TAG_SPAWN_ENTITY );
            if( line.isEmpty( ) ) {
                entityToSpawn = PigEntity.class;
                cachedEntity = null;
            }
            else {
                setEntityToSpawn( new ResourceLocation( line ) );
            }
        }
        if( tag.contains( TAG_DELAY, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnDelay = tag.getInt( TAG_DELAY );
        }
        if( tag.contains( TAG_DELAY_BUILDUP, Constants.NBT.TAG_ANY_NUMERIC ) ) {
            spawnDelayBuildup = tag.getDouble( TAG_DELAY_BUILDUP );
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket( ) {
        return new SUpdateTileEntityPacket( this.worldPosition, 0, getUpdateTag( ) );
    }

    @Override
    public CompoundNBT getUpdateTag( ) {
        CompoundNBT tag = super.save( new CompoundNBT( ) );
        return writeNBTSentToClient( tag );
    }

    @Override
    public void onDataPacket( NetworkManager net, SUpdateTileEntityPacket pkt ) {
        if( this.level.isClientSide ) {
            handleUpdateTag( this.getBlockState(), pkt.getTag( ) );
        }
    }

    @Override
    public boolean triggerEvent( int id, int type ) {
        if( this.level != null && this.level.isClientSide ) {
            DeadlyWorld.LOG.warn( "Getting client event '{}:{}'", id, type );

            if( id == EVENT_TIMER_RESET ) {
                spawnDelay = minSpawnDelay;
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
    public Entity getRenderEntity( ) {
        if( cachedEntity == null && this.level != null ) {
            World world = this.level;

            try {
                // TODO - Same thing as the above todo
                cachedEntity = entityToSpawn.getConstructor( World.class ).newInstance( world );
            }
            catch( Exception ex ) {
                DeadlyWorld.LOG.error( "Encountered exception while constructing entity for render '{}'", entityToSpawn, ex );
                cachedEntity = new PigEntity( EntityType.PIG, world );
            }

            // TODO - finalizeSpawn only happens on the server. Is it needed?
            /*
            if( cachedEntity instanceof MobEntity) {
                ((MobEntity) cachedEntity).finalizeSpawn( (ServerWorld) this.level, this.level.getCurrentDifficultyAt(this.worldPosition), SpawnReason.SPAWNER, null, null );
            }
             */
        }
        return cachedEntity;
    }

    public float getRenderEntityRotation( float partialTicks ) {
        return (float) (prevMobRotation + (mobRotation - prevMobRotation) * partialTicks);
    }
}
